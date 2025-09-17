package com.example.service;

import com.example.dao.RiskType;
import com.example.dao.SupplierRisk;
import com.example.dto.SupplierRiskDTO;

import java.util.List;

public interface SupplierRiskService {
    SupplierRisk getSupplierRiskById(long id);

    List<SupplierRiskDTO> identifyAndSaveRisk(long supplierId);

    boolean isExistRisk(long supplierInfoId, RiskType riskType);

    List<SupplierRisk> searchRisks(long supplierInfoId);

    List<SupplierRisk> queryAllRisks();
}
