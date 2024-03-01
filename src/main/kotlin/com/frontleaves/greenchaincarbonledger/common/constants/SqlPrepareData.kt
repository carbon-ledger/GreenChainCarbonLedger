package com.frontleaves.greenchaincarbonledger.common.constants

/**
 * SQL 准备数据
 *
 * SQL 准备数据, 用于系统启动时进行数据准备
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
object SqlPrepareData {

    /**
     * SQL 角色列表
     *
     * CONSOLE 控制台角色
     */
    val SQL_ROLE_CONSOLE_PERMISSION_LIST = ArrayList<String>().apply {
        add("admin:resetUserPassword")
        add("admin:resetUserRole")
        add("admin:resetUserPermission")
    }

    /**
     * SQL 角色列表
     *
     * ADMIN 管理员角色
     */
    val SQL_ROLE_ADMIN_PERMISSION_LIST = ArrayList<String>().apply {

    }


    /**
     * SQL 角色列表
     *
     * ORGANIZE 组织角色
     */
    val SQL_ROLE_ORGANIZE_PERMISSION_LIST = ArrayList<String>().apply {

    }
}