package com.example.utils;

import com.example.dao.RiskAuditLog;
import com.example.dao.SupplierInfo;
import com.example.mapper.SupplierRiskAuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 1) 定义组件
@Component
public class AuditLogger {
    @Autowired
    private SupplierRiskAuditLogMapper logMapper;

    public void log(String requestId, SupplierInfo supplier, String action,
                    int resultCode, String dataSource, String riskTags, String detail) {
        RiskAuditLog log = new RiskAuditLog();
        log.setRequestId(requestId);
        log.setSupplierInfoId(supplier.getId());
        log.setCompanyName(supplier.getOrganizationName());
        log.setAction(action);
        log.setResultCode(resultCode);
        log.setDataSource(dataSource);
        log.setRiskTags(riskTags);
        log.setDetail(detail);
        log.setCreatedAt(java.time.LocalDateTime.now());
        logMapper.insert(log);
    }
}
