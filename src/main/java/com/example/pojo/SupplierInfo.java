package com.example.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SupplierInfo {
    @NotNull
    private Long id;                     // 自增Id
    private Long supplierId;             // 供应商ID
    private String organizationName;     // 主体名称
    private String organizationCode;     // 主体编号
    private Integer supplierNature;      // 供应商性质
    private String note;                  // 备注
    private Map<String, Object> qualification; // 供应商资质 (JSON)
    private LocalDateTime createTime;    // 创建时间
    private String createOperator;       // 创建者
    private LocalDateTime lastUpdateTime;// 更新时间
    private String lastUpdateOperator;   // 更新者
    private Integer isDeleted;           // 是否已删除
}
