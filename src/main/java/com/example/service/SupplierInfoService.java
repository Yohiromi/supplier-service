package com.example.service;

import com.example.dao.SupplierInfo;

public interface SupplierInfoService {

    SupplierInfo getSupplierInfoById(Long id);
    SupplierInfo getSupplierInfoBySupplierId(long supplierId);
}
