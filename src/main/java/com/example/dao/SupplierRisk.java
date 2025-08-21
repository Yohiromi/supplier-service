package com.example.dao;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupplierRisk {
    @NotNull
    private Long id;                      // 自增Id
    @NotNull
    private Long supplierInfoId;         // 关联 supplier_info.id

    private String tagName;              // 标签英文名（后端使用）
    private String tagCnname;            // 标签中文名（前端展示）
    private String riskType;             // 风险类型（如 经营异常/黑名单）

    private RiskLevel riskLevel;         // 风险等级（枚举：P0/P1）

    private String riskDetail;           // 风险详情（如 吊销）
    private LocalDateTime createTime;    // 创建时间
}
