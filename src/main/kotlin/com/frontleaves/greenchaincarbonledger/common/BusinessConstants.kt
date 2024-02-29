package com.frontleaves.greenchaincarbonledger.common

/**
 * 业务常量
 *
 * 业务常量, 用于定义业务常量, 操作Redis时使用
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
enum class BusinessConstants(
    val value: String,
    val description: String
) {
    ALL("all:", "所有"),
    USER("user:", "用户"),
    EMAIL("email:", "邮箱"),
    PHONE("phone:", "手机"),
    NONE("", "null")
}