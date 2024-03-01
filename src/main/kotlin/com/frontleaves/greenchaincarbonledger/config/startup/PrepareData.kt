package com.frontleaves.greenchaincarbonledger.config.startup

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.common.constants.SqlPrepareData
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil
import com.google.gson.Gson
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Timestamp

/**
 * 数据准备
 *
 * 数据准备, 用于系统启动时进行数据准备
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @param jdbcTemplate JdbcTemplate
 * @author xiao_lfeng
 */
class PrepareData(private val jdbcTemplate: JdbcTemplate) {
    /**
     * 准备上传 SQL
     *
     * 准备上传 SQL, 用于系统启动时进行数据准备
     *
     * @param getPermissionList List<RoleDO> 角色列表
     */
    fun prepareUploadSqlWithPermission(getPermissionList: List<PermissionDO>) {
        getPermissionList.apply {
            any { it.name.equals("admin:resetUserPassword") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 权限数据库缺失 admin:resetUserPassword 权限，正在初始化...")
                    jdbcTemplate.update(
                        "INSERT INTO fy_permission (name, description) VALUES (?, ?)",
                        "admin:resetUserPassword",
                        "重置用户密码"
                    )
                }
            any { it.name.equals("admin:resetUserRole") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 权限数据库缺失 admin:resetUserRole 权限，正在初始化...")
                    jdbcTemplate.update(
                        "INSERT INTO fy_permission (name, description) VALUES (?, ?)",
                        "admin:resetUserRole",
                        "重置用户角色"
                    )
                }
            any { it.name.equals("admin:resetUserPermission") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 权限数据库缺失 admin:resetUserPermission 权限，正在初始化...")
                    jdbcTemplate.update(
                        "INSERT INTO fy_permission (name, description) VALUES (?, ?)",
                        "admin:resetUserPermission",
                        "重置用户权限"
                    )
                }
        }
    }

    fun prepareUploadSqlWithRole(getRoleList: List<RoleDO>) {
        getRoleList.apply {
            any { it.name.equals("default") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 角色数据库缺失默认账户，开始初始化默认账户")
                    jdbcTemplate.update(
                        "INSERT INTO fy_role (uuid, name, display_name, permission, created_at, created_user) VALUES (?, ?, ?, ?, ?, ?)",
                        ProcessingUtil.createUuid(),
                        "default",
                        "默认账户",
                        null,
                        Timestamp(System.currentTimeMillis()),
                        null
                    )
                }
            any { it.name.equals("organize") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 角色数据库缺失组织账户，开始初始化组织账户")
                    jdbcTemplate.update(
                        "INSERT INTO fy_role (uuid, name, display_name, permission, created_at, created_user) VALUES (?, ?, ?, ?, ?, ?)",
                        ProcessingUtil.createUuid(),
                        "organize",
                        "组织账户",
                        null,
                        Timestamp(System.currentTimeMillis()),
                        null
                    )
                }
            any { it.name.equals("admin") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 角色数据库缺失管理账户，开始初始化管理账户")
                    jdbcTemplate.update(
                        "INSERT INTO fy_role (uuid, name, display_name, permission, created_at, created_user) VALUES (?, ?, ?, ?, ?, ?)",
                        ProcessingUtil.createUuid(),
                        "admin",
                        "管理账户",
                        null,
                        Timestamp(System.currentTimeMillis()),
                        null
                    )
                }
            any { it.name.equals("console") }
                .takeIf { !it }?.let {
                    log.debug("\t\t> 角色数据库缺失超级管理员账户，开始初始化超级管理员账户")
                    jdbcTemplate.update(
                        "INSERT INTO fy_role (uuid, name, display_name, permission, created_at, created_user) VALUES (?, ?, ?, ?, ?, ?)",
                        ProcessingUtil.createUuid(),
                        "console",
                        "超级管理员账户",
                        null,
                        Timestamp(System.currentTimeMillis()),
                        null
                    )
                }
        }
    }

    fun prepareCheckRoleHasPermission(roleList: List<RoleDO>) {
        val gson = Gson()
        // 获取角色权限
        roleList.forEach { role ->
            val getRolePermissions = gson.fromJson(role.permission, Array<String>::class.java)
            // 构建角色权限列表
            if (role.name.equals("console")) {
                if (getRolePermissions.isNullOrEmpty()) {
                    log.debug("\t> 角色 console 未设置权限，开始初始化默认权限")
                    val createPermission = SqlPrepareData.SQL_ROLE_CONSOLE_PERMISSION_LIST
                    jdbcTemplate.update(
                        "UPDATE fy_role SET permission = ? WHERE name = ?",
                        gson.toJson(createPermission),
                        "console"
                    )
                }
            }
            if (role.name.equals("admin")) {
                if (getRolePermissions.isNullOrEmpty()) {
                    log.debug("\t> 角色 admin 未设置权限，开始初始化默认权限")
                    val createPermission = SqlPrepareData.SQL_ROLE_ADMIN_PERMISSION_LIST
                    jdbcTemplate.update(
                        "UPDATE fy_role SET permission = ? WHERE name = ?",
                        gson.toJson(createPermission),
                        "admin"
                    )
                }
            }
            if (role.name.equals("organize")) {
                if (getRolePermissions.isNullOrEmpty()) {
                    log.debug("\t> 角色 organize 未设置权限，开始初始化默认权限")
                    val createPermission = SqlPrepareData.SQL_ROLE_ORGANIZE_PERMISSION_LIST
                    jdbcTemplate.update(
                        "UPDATE fy_role SET permission = ? WHERE name = ?",
                        gson.toJson(createPermission),
                        "organize"
                    )
                }
            }
        }
    }
}