package com.example.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class RiskAuditLog {
    @NotNull
    private Long id;                  // 主键
    private String requestId;         // 请求ID
    private Long supplierInfoId;      // 关联 supplier_info.id
    private String companyName;       // 公司全称
    private String action;            // 动作
    private Integer resultCode;       // 业务结果码
    private List<Map<String, Object>> riskTags; // 风险标签快照 (JSON)
    private String dataSource;        // 数据来源
    private String operator;          // 操作者
    private String sourceIp;          // 请求来源IP
    private String detail;            // 补充信息
    private LocalDateTime createdAt;  // 创建时间
}
