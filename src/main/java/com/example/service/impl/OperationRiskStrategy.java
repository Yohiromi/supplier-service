package com.example.service.impl;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.service.RiskStrategy;
import com.example.utils.TianyanchaLimiter;
import com.example.utils.TianyanchaMockAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.service.impl.RiskBuilders.build;
import static com.example.service.impl.RiskBuilders.matchOperationRiskNames;

@Service
public class OperationRiskStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() { return RiskType.OPERATION_RISK; }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        List<String> names = matchOperationRiskNames(data.get("operationRisks"));
        if (!names.isEmpty()) {
            return build(supplierInfo, getRiskType(),
                    "The company has operational risks in the following areas: " + String.join(", ", names) + ". See details.");
        }
        return null;
    }
}
