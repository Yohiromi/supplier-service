package com.example.controller;

import com.example.dao.Result;
import com.example.dao.SupplierRisk;
import com.example.dto.SupplierRiskDTO;
import com.example.service.SupplierRiskService;
import com.example.utils.GlobalRequestLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplierRisk")
public class SupplierRiskController {
    @Autowired
    private SupplierRiskService supplierRiskService;
    @Autowired
    private GlobalRequestLimiter globalRequestLimiter;

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
            if (!globalRequestLimiter.tryAcquireWithTimeout(5000)) {
                System.out.println("拒绝");
                return Result.error("Too many requests, please try again later.");
            }
            List<SupplierRiskDTO> supplierRiskDTOList = supplierRiskService.identifyAndSaveRisk(supplierId);
            return Result.success(supplierRiskDTOList);
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
