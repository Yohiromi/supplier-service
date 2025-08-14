package com.example.controller;


import com.example.pojo.Result;
import com.example.pojo.SupplierInfo;
import com.example.service.SupplierInfoService;
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
        System.out.println(supplierInfo);
        return Result.success(supplierInfo);
    }

}
