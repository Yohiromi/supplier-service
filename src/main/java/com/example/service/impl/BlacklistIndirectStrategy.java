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
import static com.example.service.impl.RiskBuilders.sameLegalCompanies;

@Service
public class BlacklistIndirectStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() { return RiskType.BLACKLIST_INDIRECT; }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        String legal = (String) data.get("legalPersonName");
        List<String> matches = sameLegalCompanies(legal, data.get("groupBlacklist"));

        if (!matches.isEmpty()) {
            return build(supplierInfo, getRiskType(),
                    "The company shares the same legal representative with: " + String.join(", ", matches) + ". See details.");
        }
        return null;
    }
}
