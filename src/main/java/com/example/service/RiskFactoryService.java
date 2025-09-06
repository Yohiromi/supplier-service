package com.example.service;

import com.example.dao.RiskType;

public interface RiskFactoryService {
    /** 根据风险类型获取对应策略实现 */
    RiskStrategy get(RiskType type);
}
