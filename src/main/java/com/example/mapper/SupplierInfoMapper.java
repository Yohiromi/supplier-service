package com.example.mapper;

import com.example.dao.SupplierInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SupplierInfoMapper {


    @Select("select * from supplier_info where id = #{id}")
    SupplierInfo getSupplierInfoById(Long id);

    @Select("select * from supplier_info where supplier_id = #{supplierId}")
    SupplierInfo getSupplierInfoBySupplierId(long supplierId);
}
