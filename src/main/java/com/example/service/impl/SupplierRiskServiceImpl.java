package com.example.service.impl;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.dto.SupplierRiskDTO;
import com.example.mapper.SupplierRiskMapper;
import com.example.service.RiskFactoryService;
import com.example.service.RiskStrategy;
import com.example.service.SupplierInfoService;
import com.example.service.SupplierRiskService;
import com.example.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SupplierRiskServiceImpl implements SupplierRiskService {

    @Autowired private SupplierInfoService supplierInfoService;
    @Autowired private SupplierRiskMapper supplierRiskMapper;
    @Autowired private StringRedisTemplate stringRedisTemplate;

    @Autowired private RiskFactoryService riskFactory;

    @Autowired
    @Qualifier("riskExecutor")
    private ExecutorService riskExecutor;

    @Override
    public SupplierRisk getSupplierRiskById(long id) {
        // 补全实现，直接走 mapper
        return supplierRiskMapper.getSupplierRiskById(id);
    }


    @Override
    public List<SupplierRiskDTO> identifyAndSaveRisk(long supplierId) {
        // 1) 基础信息
        SupplierInfo supplierInfo = supplierInfoService.getSupplierInfoBySupplierId(supplierId);
        if (supplierInfo == null) {
            throw new RuntimeException("SupplierInfo not found for ID: " + supplierId);
        }

        List<SupplierRiskDTO> dtoList = new ArrayList<>();
        String redisKey = "supplier:risk:" + supplierInfo.getId();

        // 2) 先读缓存（避免空数组长期短路）
        String cached = stringRedisTemplate.opsForValue().get(redisKey);
        if (cached != null && !cached.isBlank() && !"[]".equals(cached.trim())) {
            for (SupplierRisk r : JsonUtils.toSupplierRiskList(cached)) {
                dtoList.add(new SupplierRiskDTO(supplierInfo, r));
            }
            return dtoList;
        }

        // 3) 缓存未命中 → 查数据库；有则直接回写缓存并返回
        List<SupplierRisk> dbRisks = supplierRiskMapper.searchRisks(supplierInfo.getId());
        if (dbRisks != null && !dbRisks.isEmpty()) {
            long ttlSeconds = 3600 + new java.util.Random().nextInt(300); // 1h ~ 1h+5min
            stringRedisTemplate.opsForValue().set(
                    redisKey, JsonUtils.SupplierRiskListToJson(dbRisks),
                    ttlSeconds, TimeUnit.SECONDS
            );
            for (SupplierRisk r : dbRisks) {
                dtoList.add(new SupplierRiskDTO(supplierInfo, r));
            }
            return dtoList;
        }

        // 4) 数据库也没有 → 并发执行各策略
        List<CompletableFuture<SupplierRisk>> futures = Arrays.stream(RiskType.values())
                .map(type -> {
                    RiskStrategy strategy = riskFactory.get(type);
                    if (strategy == null) {
                        return CompletableFuture.<SupplierRisk>completedFuture(null);
                    }
                    return CompletableFuture
                            .supplyAsync(() -> strategy.executeStrategy(supplierInfo), riskExecutor)
                            .orTimeout(2, TimeUnit.SECONDS)
                            .exceptionally(ex -> {
                                System.err.println("[RiskTag Fail] type=" + type + ", reason=" + ex.getClass().getSimpleName());
                                return null;
                            });
                })
                .collect(Collectors.toList());

        // （可选）先等待全部完成（语义更清晰）
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 5) 汇总结果 + 判重（判重仍建议DB唯一索引兜底）
        List<SupplierRisk> newRisks = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .filter(r -> {
                    try {
                        RiskType type = RiskType.valueOf(r.getTagName());
                        return !isExistRisk(r.getSupplierInfoId(), type);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // 6) 入库并构建返回
        for (SupplierRisk r : newRisks) {
            // 建议在 mapper 层使用 INSERT IGNORE 或 ON DUPLICATE KEY，结合唯一索引完全防重
            supplierRiskMapper.insert(r);
            dtoList.add(new SupplierRiskDTO(supplierInfo, r));
        }

        // 7) 回写缓存 —— 这里要写 newRisks（你原来误用了 dbRisks）
        if (!newRisks.isEmpty()) {
            long ttlSeconds = 3600 + new java.util.Random().nextInt(300); // 1h ~ 1h+5min
            stringRedisTemplate.opsForValue().set(
                    redisKey, JsonUtils.SupplierRiskListToJson(newRisks),
                    ttlSeconds, TimeUnit.SECONDS
            );
        } else {
            // 没有命中：空值缓存 + 短 TTL 防穿透
            stringRedisTemplate.opsForValue().set(redisKey, "[]", 5, TimeUnit.MINUTES);
        }

        return dtoList;
    }


    @Override
    public boolean isExistRisk(long supplierInfoId, RiskType riskType) {
        return supplierRiskMapper.isExistRisk(supplierInfoId, riskType) > 0;
    }

    @Override
    public List<SupplierRisk> searchRisks(long supplierInfoId) {
        String key = "supplier:risk:" + supplierInfoId;
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (cache != null) {
            return JsonUtils.toSupplierRiskList(cache);
        }

        List<SupplierRisk> db = supplierRiskMapper.searchRisks(supplierInfoId);
        if (!db.isEmpty()) {
            stringRedisTemplate.opsForValue()
                    .set(key, JsonUtils.SupplierRiskListToJson(db), 1, TimeUnit.HOURS);
        }
        return db;
    }

    @Override
    public List<SupplierRisk> queryAllRisks() {
        return supplierRiskMapper.queryAllRisks();
    }
}
