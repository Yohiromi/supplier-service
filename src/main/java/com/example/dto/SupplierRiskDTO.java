package com.example.dto;

import com.example.dao.RiskLevel;
import com.example.dao.SupplierInfo;
import com.example.dao.SupplierRisk;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SupplierRiskDTO {
    private Long supplierId;                  // 供应商ID
    private String organizationName;          // 主体名称
    private String organizationCode;          // 主体编号

    @NotNull
    private Long supplierInfoId;         // 关联 supplier_info.id

    private String tagName;              // 标签英文名（后端使用）
    private String tagCnname;            // 标签中文名（前端展示）
    private String riskType;             // 风险类型（如 经营异常/黑名单）

    private RiskLevel riskLevel;         // 风险等级（枚举：P0/P1）

    private String riskDetail;           // 风险详情（如 吊销）
    private LocalDateTime createTime;    // 创建时间


    public SupplierRiskDTO(SupplierInfo supplierInfo, SupplierRisk supplierRisk){
        this.supplierId = supplierInfo.getSupplierId();
        this.organizationName = supplierInfo.getOrganizationName();
        this.organizationCode = supplierInfo.getOrganizationCode();

        this.supplierInfoId = supplierInfo.getId();
        this.tagName = supplierRisk.getTagName();
        this.tagCnname = supplierRisk.getTagCnname();
        this.riskType = supplierRisk.getRiskType();
        this.riskLevel = supplierRisk.getRiskLevel();
        this.riskDetail = supplierRisk.getRiskDetail();
        this.createTime = supplierRisk.getCreateTime();

    }

}
