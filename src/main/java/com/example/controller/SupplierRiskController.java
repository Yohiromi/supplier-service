package com.example.controller;

import com.example.dao.Result;
import com.example.dao.SupplierRisk;
import com.example.service.SupplierRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplierRisk")
public class SupplierRiskController {
    @Autowired
    private SupplierRiskService supplierRiskService;

    @GetMapping("/queryById")
    public Result queryById(@RequestParam long id) {
        SupplierRisk supplierRisk = supplierRiskService.getSupplierRiskById(id);
        if (supplierRisk != null) {return Result.success(supplierRisk);}
        else {
            return Result.error("Not Found");
        }

    }
    @PostMapping("/identify")
    public Result identifyRisk(@RequestParam long supplierId) {
        try {
            supplierRiskService.identifyAndSaveRisk(supplierId);
            return Result.success("Risk identification completed.");
        } catch (Exception e) {
            return Result.error("Risk identification failed: " + e.getMessage());
        }
    }

    @GetMapping("/searchRisk")
    public Result searchRisk(@RequestParam long supplierInfoId) {
        try {
            List<SupplierRisk> supplierRisks = supplierRiskService.searchRisks(supplierInfoId);
            return Result.success(supplierRisks);
        } catch (Exception e) {
            return Result.error("Risk identification failed: " + e.getMessage());
        }
    }

}
