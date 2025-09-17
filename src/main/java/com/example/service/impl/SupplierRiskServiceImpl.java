package com.example.service.impl;

import com.example.dao.RiskAuditLog;
import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.dto.SupplierRiskDTO;
import com.example.mapper.SupplierRiskAuditLogMapper;
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
    @Autowired private SupplierRiskAuditLogMapper supplierRiskAuditLogMapper;

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

        // —— 新增：本次调用的统一 requestId（贯穿整条链路）——
        String requestId = java.util.UUID.randomUUID().toString().replace("-", "");

        // 审计：CHECK
        {
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "CHECK");
            log.setRequestId(requestId);
            supplierRiskAuditLogMapper.insert(log);
        }

        List<SupplierRiskDTO> dtoList = new ArrayList<>();
        String redisKey = "supplier:risk:" + supplierInfo.getId();

        // 2) 先读缓存（避免空数组长期短路）
        String cached = stringRedisTemplate.opsForValue().get(redisKey);
        if (cached != null && !cached.isBlank() && !"[]".equals(cached.trim())) {
            List<SupplierRisk> cachedList = JsonUtils.toSupplierRiskList(cached);
            for (SupplierRisk r : cachedList) {
                dtoList.add(new SupplierRiskDTO(supplierInfo, r));
            }
            // 审计：CACHE_HIT
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "CACHE_HIT");
            log.setRequestId(requestId);
            log.setDataSource("cache");
            log.setRiskTags(JsonUtils.SupplierRiskListToJson(cachedList)); // 直接存快照
            supplierRiskAuditLogMapper.insert(log);

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
            // 审计：DB_HIT
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "DB_HIT");
            log.setRequestId(requestId);
            log.setDataSource("db");
            log.setRiskTags(JsonUtils.SupplierRiskListToJson(dbRisks));
            supplierRiskAuditLogMapper.insert(log);

            return dtoList;
        }

        // 4) DB 也没有 → 并发执行各策略（LIVE_CALL）
        {
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "LIVE_CALL");
            log.setRequestId(requestId);
            supplierRiskAuditLogMapper.insert(log);
        }

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
                .collect(java.util.stream.Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 5) 汇总结果 + 判重
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
                .collect(java.util.stream.Collectors.toList());

        // 审计：EVALUATE（无论是否命中，都记录快照）
        {
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "EVALUATE");
            log.setRequestId(requestId);
            log.setDataSource("live");
            log.setRiskTags(JsonUtils.SupplierRiskListToJson(newRisks));
            supplierRiskAuditLogMapper.insert(log);
        }

        // 6) 入库并构建返回
        for (SupplierRisk r : newRisks) {
            supplierRiskMapper.insert(r);
            dtoList.add(new SupplierRiskDTO(supplierInfo, r));
        }

        // 7) 回写缓存（修正：这里应写 newRisks，而不是 dbRisks）
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

        // 审计：PASS（收尾）
        {
            RiskAuditLog log = RiskAuditLogBuilders.baseLog(supplierInfo, "PASS");
            log.setRequestId(requestId);
            log.setDetail("inserted=" + newRisks.size());
            log.setRiskTags(JsonUtils.SupplierRiskListToJson(newRisks));
            supplierRiskAuditLogMapper.insert(log);
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
