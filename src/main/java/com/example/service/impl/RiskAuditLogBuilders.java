package com.example.service.impl;

import com.example.dao.RiskAuditLog;
import com.example.dao.SupplierInfo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RiskAuditLog 构建工具类
 * 统一封装日志对象的创建逻辑，避免在业务代码中手动 set 每个字段。
 */
public final class RiskAuditLogBuilders {

    private RiskAuditLogBuilders() {
        // 工具类不允许实例化
    }

    /**
     * 构建基础日志对象（包含 traceId、supplierInfoId、companyName、createdAt）
     */
    public static RiskAuditLog baseLog(SupplierInfo supplierInfo, String action) {
        RiskAuditLog log = new RiskAuditLog();
        log.setRequestId(generateRequestId());
        log.setSupplierInfoId(supplierInfo.getId());
        log.setCompanyName(supplierInfo.getOrganizationName());
        log.setAction(action);
        log.setResultCode(0); // 默认成功
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }

    /**
     * 带有错误信息的日志
     */
    public static RiskAuditLog errorLog(SupplierInfo supplierInfo, String action, String errorDetail) {
        RiskAuditLog log = baseLog(supplierInfo, action);
        log.setResultCode(-1); // 约定非 0 表示失败
        log.setDetail(errorDetail);
        return log;
    }

    /**
     * 设置 riskTags 快照
     */
    public static RiskAuditLog withRiskTags(RiskAuditLog log, String riskTagsJson) {
        log.setRiskTags(riskTagsJson);
        return log;
    }

    /**
     * 设置数据来源
     */
    public static RiskAuditLog withDataSource(RiskAuditLog log, String dataSource) {
        log.setDataSource(dataSource);
        return log;
    }

    /**
     * 设置操作人（可用于区分是用户还是系统）
     */
    public static RiskAuditLog withOperator(RiskAuditLog log, String operator) {
        log.setOperator(operator);
        return log;
    }

    /**
     * 生成 traceId/requestId
     */
    private static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
