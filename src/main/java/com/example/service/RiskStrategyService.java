package com.example.service;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;

public interface RiskStrategyService {
    SupplierRisk executeStrategy(RiskType riskType, SupplierInfo supplierInfo);
}
