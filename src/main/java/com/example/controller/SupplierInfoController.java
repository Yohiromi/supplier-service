package com.example.controller;


import com.example.dao.Result;
import com.example.dao.SupplierInfo;
import com.example.dto.SupplierInfoDTO;
import com.example.service.SupplierInfoService;
import com.example.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supplierInfo")
public class SupplierInfoController {
    @Autowired
    SupplierInfoService supplierInfoService;

    @GetMapping("/queryById")
    public Result queryById(@RequestParam Long id){
        SupplierInfo supplierInfo = supplierInfoService.getSupplierInfoById(id);
        //将string转换为JSON格式
        SupplierInfoDTO supplierInfoDTO = new SupplierInfoDTO(supplierInfo, JsonUtils.toListMap("["+supplierInfo.getQualification()+"]"));
        return Result.success(supplierInfoDTO);
    }

}
