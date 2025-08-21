package com.example.mapper;

import com.example.dao.RiskType;
import com.example.dao.SupplierRisk;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupplierRiskMapper {
    @Select("select * from supplier_risk where id = #{id}")
    SupplierRisk getSupplierRiskById(long id);


    @Insert("insert into supplier_risk (supplier_info_id,tag_name,tag_cnname,risk_type,risk_level,risk_detail,create_time) values (#{supplierInfoId},#{tagName},#{tagCnname},#{riskType},#{riskLevel},#{riskDetail},#{createTime})")
    void insert(SupplierRisk risk);

    @Select("select COUNT(*) from supplier_risk where supplier_info_id = #{supplierInfoId} and tag_name=#{riskType} ")
    int isExistRisk(long supplierInfoId, RiskType riskType);

    @Select("select * from supplier_risk where supplier_info_id = #{supplierInfoId}")
    List<SupplierRisk> searchRisks(long supplierInfoId);
}
