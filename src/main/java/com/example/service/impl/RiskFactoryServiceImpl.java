package com.example.service.impl.factory;

import com.example.dao.RiskType;
import com.example.service.RiskFactoryService;
import com.example.service.RiskStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskFactoryServiceImpl implements RiskFactoryService {

    private final Map<RiskType, RiskStrategy> map = new EnumMap<>(RiskType.class);

    /** Spring 会把所有实现 RiskStrategy 的 bean 注入进来 */
    public RiskFactoryServiceImpl(List<RiskStrategy> strategies) {
        for (RiskStrategy s : strategies) {
            map.put(s.getRiskType(), s);//在这里进行分发
        }
    }

    @Override
    public RiskStrategy get(RiskType type) {
        return map.get(type);
    }
}
