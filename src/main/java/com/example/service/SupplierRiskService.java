package com.example.service;

import com.example.dao.RiskType;
import com.example.dao.SupplierRisk;

import java.util.List;

public interface SupplierRiskService {
    SupplierRisk getSupplierRiskById(long id);

    void identifyAndSaveRisk(long supplierId);

    boolean isExistRisk(long supplierInfoId, RiskType riskType);

    List<SupplierRisk> searchRisks(long supplierInfoId);
}
