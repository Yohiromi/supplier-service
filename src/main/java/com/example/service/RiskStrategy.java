package com.example.service;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;

/** 每个实现类负责一个风险类型 */
public interface RiskStrategy {
    /** 该策略负责的风险类型标识 */
    RiskType getRiskType();

    /** 执行识别（命中返回 SupplierRisk，不命中返回 null） */
    SupplierRisk executeStrategy(SupplierInfo supplierInfo);
}
