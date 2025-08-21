package com.example.dao;

/**
 * 风险类型枚举，对应不同风险识别策略
 * P0 = 高风险，P1 = 中风险
 */
/**
 * 风险类型枚举，对应不同风险识别策略
 * P0 = 高风险，P1 = 中风险
 */
public enum RiskType {
    ABNORMAL_OPERATION,       // 经营状态异常（P0）
    BLACKLIST_DIRECT,         // 直接拉黑（P0）
    BLACKLIST_INDIRECT,       // 间接拉黑（P1）
    ESTABLISH_TIME_SHORT,     // 成立时间较短（P1）
    CAPITAL_TOO_LOW,          // 注册资本较低（P1）
    OPERATION_RISK            // 存在经营风险（P1）
}
