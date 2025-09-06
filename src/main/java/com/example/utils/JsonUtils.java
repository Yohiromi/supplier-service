package com.example.utils;

import com.example.dao.SupplierRisk;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册 Java 8 时间模块，处理 LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        // 避免时间序列化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * JSON 字符串转 List<Map<String, Object>>
     */
    public static List<Map<String, Object>> toListMap(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 解析失败：" + json, e);
        }
    }

    /**
     * List<Map<String, Object>> 转 JSON 字符串
     */
    public static String toJson(List<Map<String, Object>> data) {
        try {
            if (data == null || data.isEmpty()) return "[]";
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * JSON 字符串转 Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyMap();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 转 Map 失败", e);
        }
    }

    /**
     * List<SupplierRisk> 转 JSON 字符串
     */
    public static String SupplierRiskListToJson(List<SupplierRisk> data) {
        try {
            if (data == null || data.isEmpty()) return "[]";
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace(); //  打印错误信息以便调试
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * JSON 字符串转 List<SupplierRisk>
     */
    public static List<SupplierRisk> toSupplierRiskList(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return objectMapper.readValue(json, new TypeReference<List<SupplierRisk>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
}
