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

import static com.example.service.impl.RiskBuilders.build;
import static com.example.service.impl.RiskBuilders.parseCapital;

@Service
public class CapitalTooLowStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() { return RiskType.CAPITAL_TOO_LOW; }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        double capital = parseCapital(data.get("registeredCapital"));
        if (capital < 100) {
            return build(supplierInfo, getRiskType(),
                    "The registered capital is " + capital + " million CNY, which is below the minimum requirement. See details.");
        }
        return null;
    }
}
