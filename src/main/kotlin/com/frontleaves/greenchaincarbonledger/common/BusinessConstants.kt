package com.frontleaves.greenchaincarbonledger.common

/**
 * 业务常量
 *
 * 业务常量
 *
 * @since v1.0.0
 * @version v1.0.0
 * @author xiao_lfeng
 */
enum class BusinessConstants(
    val value: String,
    val description: String
) {
    BUSINESS_LOGIN("login:", "登陆实现"),
    ALL_PERMISSION("all:", "所有权限"),
    USER("user:", "用户"),
    NONE("", "null")
}