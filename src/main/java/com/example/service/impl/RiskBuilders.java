package com.example.service.impl;

import com.example.dao.RiskLevel;
import com.example.dao.RiskType;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;

import java.time.LocalDateTime;
import java.util.*;

public final class RiskBuilders {

    private RiskBuilders() {}

    // 天眼查“正常”状态集合（英文版本）
    public static final Set<String> NORMAL_STATUSES = Set.of("Active", "Operating", "Open", "Normal");

    // operationRisks 过滤条件
    public static final Set<String> OP_NAMES = Set.of("Peripheral Risk", "Early Warning", "Self Risk", "Historical Risk");
    public static final Set<Integer> OP_TYPES = Set.of(1,3,7,22,31,32,33,45,59,62,63,99);

    /** 统一构建 SupplierRisk */
    public static SupplierRisk build(SupplierInfo supplier, RiskType type, String detail) {
        SupplierRisk r = new SupplierRisk();
        r.setSupplierInfoId(supplier.getId());
        r.setTagName(type.name());
        r.setTagCnname(cnName(type));
        r.setRiskType(typeLabel(type));
        r.setRiskLevel(level(type));
        r.setRiskDetail(detail);
        r.setCreateTime(LocalDateTime.now());
        return r;
    }

    public static String cnName(RiskType t) {
        switch (t) {
            case BLACKLIST_DIRECT: return "直接拉黑";
            case BLACKLIST_INDIRECT: return "间接拉黑";
            case ABNORMAL_OPERATION: return "经营状态异常";
            case ESTABLISH_TIME_SHORT: return "成立时间较短";
            case CAPITAL_TOO_LOW: return "注册资本较低";
            case OPERATION_RISK: return "存在经营风险";
            default: return "未知风险";
        }
    }

    public static String typeLabel(RiskType t) {
        switch (t) {
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

    public static RiskLevel level(RiskType t) {
        switch (t) {
            case BLACKLIST_DIRECT:
            case ABNORMAL_OPERATION:
                return RiskLevel.P0;
            default:
                return RiskLevel.P1;
        }
    }

    /** 解析注册资本（支持纯数字或字符串），单位：million CNY */
    public static double parseCapital(Object raw) {
        if (raw == null) return 0;
        String s = raw.toString().trim().replace(",", "");
        // 你的 mock 已经用纯数字，这里预留兼容
        if (s.endsWith("USD million")) {
            return Double.parseDouble(s.replace("USD million", "").trim()) * 7;
        }
        if (s.endsWith("CNY million")) {
            return Double.parseDouble(s.replace("CNY million", "").trim());
        }
        return Double.parseDouble(s); // 纯数字
    }

    /** groupBlacklist 中是否存在与当前法人同名的公司，返回匹配公司名列表 */
    @SuppressWarnings("unchecked")
    public static List<String> sameLegalCompanies(String currentLegalName, Object groupBlacklist) {
        if (currentLegalName == null || !(groupBlacklist instanceof List)) return Collections.emptyList();
        List<Map<String, Object>> list = (List<Map<String, Object>>) groupBlacklist;
        List<String> hit = new ArrayList<>();
        for (Map<String, Object> it : list) {
            if (currentLegalName.equals(it.get("legalPersonName"))) {
                hit.add(Objects.toString(it.getOrDefault("companyName", ""), ""));
            }
        }
        return hit;
    }

    /** 从 operationRisks 里筛出符合高风险规则的 name 列表 */
    @SuppressWarnings("unchecked")
    public static List<String> matchOperationRiskNames(Object operationRisks) {
        if (!(operationRisks instanceof List)) return Collections.emptyList();
        List<Map<String, Object>> list = (List<Map<String, Object>>) operationRisks;
        List<String> names = new ArrayList<>();
        for (Map<String, Object> r : list) {
            String name = Objects.toString(r.get("name"), "");
            String tag  = Objects.toString(r.get("tag"), "");
            Integer type = (r.get("type") instanceof Integer) ? (Integer) r.get("type") : null;
            if (OP_NAMES.contains(name) && "High".equals(tag) && type != null && OP_TYPES.contains(type)) {
                names.add(name);
            }
        }
        return names;
    }
}
