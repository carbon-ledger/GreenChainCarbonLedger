package com.frontleaves.greenchaincarbonledger.common.constants

/**
 * Redis过期时间
 *
 * Redis过期时间, 用于定义Redis过期时间(TimeUnit 默认为毫秒)
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
enum class RedisExpiration(val expirationTime: Long) {
    SECOND(1000L),
    MINUTE(60000L),
    MINUTE_5(300000L),
    MINUTE_10(600000L),
    MINUTE_15(900000L),
    MINUTE_30(1800000L),
    HOUR(3600000L),
    HOUR_2(7200000L),
    HOUR_3(10800000L),
    HOUR_6(21600000L),
    HOUR_12(43200000L),
    DAY(86400000L),
    DAY_2(172800000L),
    DAY_3(259200000L),
    WEEK(604800000L),
}