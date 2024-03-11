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
     * SQL 权限列表
     *
     * 获取本程序的所有权限组的内容信息
     */
    val PERMISSION_LIST = ArrayList<ArrayList<String>>().apply {
        add(ArrayList(listOf("auth:userChangePassword", "用户修改密码")))
        add(ArrayList(listOf("auth:userDelete", "用户删除")))
        add(ArrayList(listOf("auth:userLogout", "用户登出")))
        add(ArrayList(listOf("role:getCurrentRole", "获取当前角色")))
        add(ArrayList(listOf("user:getUserCurrentInfo", "获取当前用户信息")))
        add(ArrayList(listOf("user:getUserList", "获取用户列表")))
        add(ArrayList(listOf("user:editUserInformation", "编辑用户信息")))
        add(ArrayList(listOf("user:putUserForceEdit", "强制编辑用户信息")))
        add(ArrayList(listOf("user:banUser", "封禁用户")))
        add(ArrayList(listOf("admin:resetUserPassword", "重置用户密码")))
    }

    /**
     * SQL 角色列表
     *
     * CONSOLE 控制台角色
     */
    val SQL_ROLE_CONSOLE_PERMISSION_LIST = ArrayList<String>().apply {
        PERMISSION_LIST.forEach{
            add(it[0])
        }
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