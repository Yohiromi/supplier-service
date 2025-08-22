package com.example.service.impl;

import com.example.dao.RiskLevel;
import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import com.example.service.RiskStrategyService;
import com.example.utils.TianyanchaLimiter;
import com.example.utils.TianyanchaMockAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
/**
 * 这是策略模块
 * 通过比对传递模拟的天眼查信息挨个判断是否又对应的risk
 * 然后通过buildRisk组成一个SupplierRisk对象返回
 * **/


@Service
public class RiskStrategyServiceImpl implements RiskStrategyService {

    private static final Set<String> NORMAL_STATUSES = Set.of("Active", "Operating", "Open", "Normal");
    @Autowired
    private TianyanchaLimiter tianyanchaLimiter;

    @Override
    public SupplierRisk executeStrategy(RiskType riskType, SupplierInfo supplierInfo) {
        // QPS 限流：调用天眼查前检查<10
        if (!tianyanchaLimiter.tryAcquire()) {
            throw new RuntimeException("调用天眼查频率过高，请稍后再试.");
        }
        Map<String, Object> data = TianyanchaMockAPI.queryRiskInfo(supplierInfo.getOrganizationCode());

        if (data == null || data.isEmpty()) return null;

        switch (riskType) {

            case ABNORMAL_OPERATION:
                String status = (String) data.get("registrationStatus");
                if (status != null && !NORMAL_STATUSES.contains(status)) {
                    return buildRisk(supplierInfo, riskType, "The company's operating status is: " + status + ". See details.");
                }
                break;

            case BLACKLIST_DIRECT:
                String blockStatus = (String) data.get("corpBlacklist");
                if ("CORP_LVL_BLOCKLIST".equals(blockStatus)) {
                    return buildRisk(supplierInfo, riskType, "The company is listed in the official blacklist.");
                }
                break;

            case BLACKLIST_INDIRECT:
                String currentLegalPerson = (String) data.get("legalPersonName");
                List<Map<String, Object>> blackList = (List<Map<String, Object>>) data.get("groupBlacklist");
                if (currentLegalPerson != null && blackList != null) {
                    List<String> matchedCompanies = new ArrayList<>();
                    for (Map<String, Object> entry : blackList) {
                        if (currentLegalPerson.equals(entry.get("legalPersonName"))) {
                            matchedCompanies.add((String) entry.get("companyName"));
                        }
                    }
                    if (!matchedCompanies.isEmpty()) {
                        return buildRisk(supplierInfo, riskType, "The company shares the same legal representative with: " + String.join(", ", matchedCompanies) + ". See details.");
                    }
                }
                break;

            case ESTABLISH_TIME_SHORT:
                String estDateStr = (String) data.get("establishDate");
                if (estDateStr != null) {
                    try {
                        LocalDate estDate = LocalDate.parse(estDateStr);
                        long days = ChronoUnit.DAYS.between(estDate, LocalDate.now());
                        if (days < 365) {
                            return buildRisk(supplierInfo, riskType, "The company was established " + days + " days ago, which does not meet the minimum requirement. See details.");
                        }
                    } catch (Exception ignored) {}
                }
                break;

            case CAPITAL_TOO_LOW:
                Object capitalObj = data.get("registeredCapital");
                if (capitalObj != null) {
                    try {
                        double capital = Double.parseDouble(capitalObj.toString());
                        if (capital < 100) {
                            return buildRisk(supplierInfo, riskType, "The registered capital is " + capital + " million CNY, which is below the minimum requirement. See details.");
                        }
                    } catch (Exception ignored) {}
                }
                break;

            case OPERATION_RISK:
                List<Map<String, Object>> risks = (List<Map<String, Object>>) data.get("operationRisks");
                if (risks != null) {
                    List<String> matchedRiskNames = new ArrayList<>();
                    for (Map<String, Object> risk : risks) {
                        String name = (String) risk.get("name");
                        String tag = (String) risk.get("tag");
                        Integer type = (Integer) risk.get("type");
                        if (Set.of("Peripheral Risk", "Early Warning", "Self Risk", "Historical Risk").contains(name)
                                && "High".equals(tag)
                                && Set.of(1, 3, 7, 22, 31, 32, 33, 45, 59, 62, 63, 99).contains(type)) {
                            matchedRiskNames.add(name);
                        }
                    }
                    if (!matchedRiskNames.isEmpty()) {
                        return buildRisk(supplierInfo, riskType, "The company has operational risks in the following areas: " + String.join(", ", matchedRiskNames) + ". See details.");
                    }
                }
                break;

            default:
                break;
        }

        return null;
    }

    private SupplierRisk buildRisk(SupplierInfo supplierInfo, RiskType riskType, String riskDetail) {
        SupplierRisk risk = new SupplierRisk();
        risk.setSupplierInfoId(supplierInfo.getId());
        risk.setTagName(riskType.name());
        risk.setTagCnname(getRiskCnName(riskType));
        risk.setRiskType(getRiskTypeLabel(riskType));
        risk.setRiskLevel(getRiskLevel(riskType));
        risk.setRiskDetail(riskDetail);
        risk.setCreateTime(LocalDateTime.now());
        return risk;
    }

    private String getRiskCnName(RiskType type) {
        switch (type) {
            case BLACKLIST_DIRECT: return "直接拉黑";
            case BLACKLIST_INDIRECT: return "间接拉黑";
            case ABNORMAL_OPERATION: return "经营状态异常";
            case ESTABLISH_TIME_SHORT: return "成立时间较短";
            case CAPITAL_TOO_LOW: return "注册资本较低";
            case OPERATION_RISK: return "存在经营风险";
            default: return "Unknown Risk";
        }
    }

    private String getRiskTypeLabel(RiskType type) {
        switch (type) {
            case BLACKLIST_DIRECT:
            case BLACKLIST_INDIRECT:
                return "Blacklist Category";
            case ABNORMAL_OPERATION:
            case ESTABLISH_TIME_SHORT:
            case CAPITAL_TOO_LOW:
                return "Business Registration Category";
            case OPERATION_RISK:
                return "Operational Risk Category";
            default:
                return "Other";
        }
    }

    private RiskLevel getRiskLevel(RiskType type) {
        switch (type) {
            case BLACKLIST_DIRECT:
            case ABNORMAL_OPERATION:
                return RiskLevel.P0;
            default:
                return RiskLevel.P1;
        }
    }
}
