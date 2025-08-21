package com.example.utils;

import java.util.*;

public class TianyanchaMockAPI {

    public static Map<String, Object> queryRiskInfo(String organizationCode) {
        Map<String, Object> riskData = new HashMap<>();

        switch (organizationCode) {

            // XCKJ-001：Judicial + Low Capital + Short Establishment + Operation Risk
            case "XCKJ-001":
                riskData.put("registrationStatus", "Active");
                riskData.put("corpBlacklist", "");
                riskData.put("legalPersonName", "Li Qiang");
                riskData.put("groupBlacklist", List.of());
                riskData.put("establishDate", "2025-07-01");
                riskData.put("registeredCapital", "50");
                riskData.put("operationRisks", List.of(
                        Map.of("name", "Self Risk", "tag", "High", "type", 3),
                        Map.of("name", "Historical Risk", "tag", "Medium", "type", 22)
                ));
                break;

            // CXYM-002：Abnormal Business (Revoked)
            case "CXYM-002":
                riskData.put("registrationStatus", "Revoked");
                riskData.put("corpBlacklist", "");
                riskData.put("legalPersonName", "Wang Ming");
                riskData.put("groupBlacklist", List.of());
                riskData.put("establishDate", "2020-05-10");
                riskData.put("registeredCapital", "300");
                riskData.put("operationRisks", List.of());
                break;

            // HHNY-003：Direct Blacklist
            case "HHNY-003":
                riskData.put("registrationStatus", "Active");
                riskData.put("corpBlacklist", "CORP_LVL_BLOCKLIST");
                riskData.put("legalPersonName", "Zhou Zhiqiang");
                riskData.put("groupBlacklist", List.of());
                riskData.put("establishDate", "2018-03-22");
                riskData.put("registeredCapital", "1000");
                riskData.put("operationRisks", List.of());
                break;

            // LHPH-004：Indirect Blacklist + Abnormal Status
            case "LHPH-004":
                riskData.put("registrationStatus", "Cancelled");
                riskData.put("corpBlacklist", "");
                riskData.put("legalPersonName", "Zhao Lixin");
                riskData.put("groupBlacklist", List.of(
                        Map.of("companyName", "Blacklist Tech", "legalPersonName", "Zhao Lixin"),
                        Map.of("companyName", "Risk Management Co.", "legalPersonName", "Wang Gang")
                ));
                riskData.put("establishDate", "2019-11-11");
                riskData.put("registeredCapital", "500");
                riskData.put("operationRisks", List.of());
                break;

            // TQLW-005：High Operation Risk
            case "TQLW-005":
                riskData.put("registrationStatus", "Open");
                riskData.put("corpBlacklist", "");
                riskData.put("legalPersonName", "Lin Tao");
                riskData.put("groupBlacklist", List.of());
                riskData.put("establishDate", "2021-02-01");
                riskData.put("registeredCapital", "200");
                riskData.put("operationRisks", List.of(
                        Map.of("name", "Surrounding Risk", "tag", "High", "type", 3),
                        Map.of("name", "Warning Alert", "tag", "High", "type", 1)
                ));
                break;

            default:
                riskData.put("registrationStatus", "Active");
                riskData.put("corpBlacklist", "");
                riskData.put("legalPersonName", "Unknown");
                riskData.put("groupBlacklist", List.of());
                riskData.put("establishDate", "2015-01-01");
                riskData.put("registeredCapital", "500");
                riskData.put("operationRisks", List.of());
                break;
        }

        return riskData;
    }
}
