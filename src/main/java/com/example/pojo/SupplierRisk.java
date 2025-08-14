package com.example.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupplierRisk {
    @NotNull
    private Long id;                 // 主键
    private Long supplierInfoId;     // 关联 supplier_info.id
    private String tagName;          // 标签英文名
    private String tagCnname;        // 标签中文名
    private String riskType;         // 风险类型
    private String riskLevel;        // 风险等级
    private String riskDetail;       // 风险详情
    private LocalDateTime createTime;// 创建时间
}
