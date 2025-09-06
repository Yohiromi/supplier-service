package com.example.service.impl;
import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.service.RiskStrategy;
import com.example.utils.TianyanchaLimiter;
import com.example.utils.TianyanchaMockAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.service.impl.RiskBuilders.NORMAL_STATUSES;
import static com.example.service.impl.RiskBuilders.build;

@Service
public class AbnormalOperationStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() {
        return RiskType.ABNORMAL_OPERATION;
    }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        String status = (String) data.get("registrationStatus");
        if (status != null && !NORMAL_STATUSES.contains(status)) {
            return build(supplierInfo, getRiskType(), "The company's operating status is: " + status + ". See details.");
        }
        return null;
    }
}
