package com.example.dao;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupplierInfo {
    @NotNull
    private Long id;                          // 自增Id
    @NotNull
    private Long supplierId;                  // 供应商ID
    private String organizationName;          // 主体名称
    private String organizationCode;          // 主体编号
    private Integer supplierNature;           // 供应商性质
    private String note;                      // 备注

    // JSON 字段：供应商资质

    private String qualification;

    private LocalDateTime createTime;         // 创建时间
    private String createOperator;            // 创建者
    private LocalDateTime lastUpdateTime;     // 更新时间
    private String lastUpdateOperator;        // 更新者
    private Boolean isDeleted;                // 是否已删除


}
