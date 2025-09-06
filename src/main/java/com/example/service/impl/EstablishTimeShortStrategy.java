package com.example.service.impl;

import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.service.RiskStrategy;
import com.example.utils.TianyanchaLimiter;
import com.example.utils.TianyanchaMockAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.example.service.impl.RiskBuilders.build;

@Service
public class EstablishTimeShortStrategy implements RiskStrategy {

    @Autowired private TianyanchaLimiter limiter;

    @Override
    public RiskType getRiskType() { return RiskType.ESTABLISH_TIME_SHORT; }

    @Override
    public SupplierRisk executeStrategy(SupplierInfo supplierInfo) {
        if (!limiter.tryAcquireWithTimeout(200)) return null;

        Map<String,Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());
        if (data == null || data.isEmpty()) return null;

        String dateStr = (String) data.get("establishDate");
        if (dateStr == null) return null;

        try {
            long days = ChronoUnit.DAYS.between(LocalDate.parse(dateStr), LocalDate.now());
            if (days < 365) {
                return build(supplierInfo, getRiskType(),
                        "The company was established " + days + " days ago, which does not meet the minimum requirement. See details.");
            }
        } catch (Exception ignore) {}
        return null;
    }
}
