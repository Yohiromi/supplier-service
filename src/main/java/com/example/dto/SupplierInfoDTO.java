package com.example.dto;

import com.example.dao.SupplierInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SupplierInfoDTO {
    @NotNull
    private Long id;                          // 自增Id
    private Long supplierId;                  // 供应商ID
    private String organizationName;          // 主体名称
    private String organizationCode;          // 主体编号
    private Integer supplierNature;           // 供应商性质
    private String note;                      // 备注

    // JSON 字段：供应商资质

    private List<Map<String, Object>> qualification;

    private LocalDateTime createTime;         // 创建时间
    private String createOperator;            // 创建者
    private LocalDateTime lastUpdateTime;     // 更新时间
    private String lastUpdateOperator;        // 更新者
    private Boolean isDeleted;                // 是否已删除

    public SupplierInfoDTO(SupplierInfo supplierInfo, List<Map<String, Object>> qualificationList){
        this.id = supplierInfo.getId();
        this.supplierId = supplierInfo.getSupplierId();
        this.organizationName = supplierInfo.getOrganizationName();
        this.organizationCode = supplierInfo.getOrganizationCode();
        this.supplierNature = supplierInfo.getSupplierNature();
        this.note = supplierInfo.getNote();
        this.qualification = qualificationList;
        this.createTime = supplierInfo.getCreateTime();
        this.createOperator = supplierInfo.getCreateOperator();
        this.lastUpdateTime = supplierInfo.getLastUpdateTime();
        this.lastUpdateOperator = supplierInfo.getLastUpdateOperator();
        this.isDeleted = supplierInfo.getIsDeleted();
    }
}
