package com.example.mapper;

import com.example.pojo.SupplierInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SupplierInfoMapper {


    @Select("select * from supplier_info where id = #{id}")
    SupplierInfo getSupplierInfoById(Long id);
}
