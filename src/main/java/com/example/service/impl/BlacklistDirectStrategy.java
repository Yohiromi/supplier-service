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

@Service
public class BlacklistDirectStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() { return RiskType.BLACKLIST_DIRECT; }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        String block = (String) data.get("corpBlacklist");
        if ("CORP_LVL_BLOCKLIST".equals(block)) {
            return build(supplierInfo, getRiskType(), "The company is listed in the official blacklist.");
        }
        return null;
    }
}
