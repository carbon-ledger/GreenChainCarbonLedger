package com.frontleaves.greenchaincarbonledger.common

/**
 * Redis常量类
 *
 * 用于存放Redis常量
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author 筱锋xiao_lfeng
 */
object RedisConstant {
    /*
     * 类型分类
     */
    // 邮件相关
    const val TYPE_EMAIL: String = "mail:"

    // 登陆相关
    const val TYPE_AUTH: String = "auth:"

    // 权限相关
    const val TYPE_PERMISSION: String = "permission:"

    /*
     * 表分类
     */
    // 邮箱验证码
    const val TABLE_EMAIL: String = "code:"

    // 令牌相关
    const val TABLE_TOKEN: String = "token:"

    // 用户相关
    const val TABLE_USER: String = "user:"

    // 角色相关
    const val TABLE_ROLE: String = "role:"
}