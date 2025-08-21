package com.example.service.impl;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.mapper.SupplierRiskMapper;
import com.example.service.RiskStrategyService;
import com.example.service.SupplierInfoService;
import com.example.service.SupplierRiskService;
import com.example.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SupplierRiskServiceImpl implements SupplierRiskService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SupplierInfoService supplierInfoService;

    @Autowired
    private RiskStrategyService riskStrategyService;

    @Autowired
    private SupplierRiskMapper supplierRiskMapper;

    @Override
    public SupplierRisk getSupplierRiskById(long id) {
        return supplierRiskMapper.getSupplierRiskById(id);
    }

    @Override
    public void identifyAndSaveRisk(long supplierId) {
        // 1. 获取供应商信息
        SupplierInfo supplierInfo = supplierInfoService.getSupplierInfoBySupplierId(supplierId);
        if (supplierInfo == null) {
            throw new RuntimeException("SupplierInfo not found for ID: " + supplierId);
        }

        // Redis key 定义
        String redisKey = "supplier:risk:" + supplierInfo.getId();

        // 检查是否已经存在缓存（防止重复识别）
        if (stringRedisTemplate.hasKey(redisKey)) {
            System.out.println("Risk info for supplier " + supplierId + " already cached.");
            return;
        }

        // 收集所有识别出的风险
        List<SupplierRisk> riskList = new ArrayList<>();

        // 2. 遍历所有策略
        for (RiskType riskType : RiskType.values()) {
            SupplierRisk result = riskStrategyService.executeStrategy(riskType, supplierInfo);


            if (result != null && !isExistRisk(result.getSupplierInfoId(), riskType)) {
                // 插入数据库
                supplierRiskMapper.insert(result);
                riskList.add(result);
                //插入日志
            }
        }

        // 3. 缓存到 Redis
        if (!riskList.isEmpty()) {
            String json = JsonUtils.SupplierRiskListToJson(riskList);
            stringRedisTemplate.opsForValue().set(redisKey, json, 1, TimeUnit.HOURS);
        }
    }

    @Override
    public boolean isExistRisk(long supplierInfoId, RiskType riskType) {
        int res = supplierRiskMapper.isExistRisk(supplierInfoId, riskType);
        if (res > 0) {
            return true;
        }
        else return false;
    }

    @Override
    public List<SupplierRisk> searchRisks(long supplierInfoId) {
        //先查询redis
        //如果命中写入日志然后返回
        //如果没有命中
        //查询数据库
        //然后重新写入redis
        return supplierRiskMapper.searchRisks(supplierInfoId);
    }
}
