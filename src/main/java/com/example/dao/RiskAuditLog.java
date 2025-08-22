package com.example.dao;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RiskAuditLog {
    @NotNull
    private Long id;                           // 自增Id
    private String requestId;                  // 请求ID/traceId
    @NotNull
    private Long supplierInfoId;               // 关联 supplier_info.id
    private String companyName;                // 公司名称

    private String action;                     // 动作（CHECK/CACHE_HIT/BLOCK/PASS 等）
    private Integer resultCode;                // 结果码（0成功，非0失败/降级）

    // 先用string接收，然后用JsonUtils转成Json
    private String riskTags;

    private String dataSource;                 // 数据来源（live/cache）
    private String operator;                   // 操作者
    private String sourceIp;                   // 请求来源IP
    private String detail;                     // 详情（限流/降级说明等）

    private LocalDateTime createdAt;           // 创建时间
}
