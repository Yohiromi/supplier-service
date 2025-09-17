package com.example.mapper;

import com.example.dao.RiskAuditLog;
import com.example.dao.SupplierRisk;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface SupplierRiskAuditLogMapper {

    @Insert("""
        INSERT INTO risk_audit_log
        (request_id, supplier_info_id, company_name,
         action, result_code, risk_tags,
         data_source, operator, source_ip,
         detail, created_at)
        VALUES
        (#{requestId}, #{supplierInfoId}, #{companyName},
         #{action}, #{resultCode}, #{riskTags},
         #{dataSource}, #{operator}, #{sourceIp},
         #{detail}, #{createdAt})
        """)
    void insert(RiskAuditLog log);
}
