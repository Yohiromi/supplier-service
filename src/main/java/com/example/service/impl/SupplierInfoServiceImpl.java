package com.example.service.impl;

import com.example.mapper.SupplierInfoMapper;
import com.example.dao.SupplierInfo;
import com.example.service.SupplierInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierInfoServiceImpl implements SupplierInfoService {

    @Autowired
    private SupplierInfoMapper supplierInfoMapper;

    @Override
    public SupplierInfo getSupplierInfoById(Long id) {
        return supplierInfoMapper.getSupplierInfoById(id);
    }

    @Override
    public SupplierInfo getSupplierInfoBySupplierId(long supplierId) {
        return supplierInfoMapper.getSupplierInfoBySupplierId(supplierId);
    }
}
