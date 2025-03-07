package com.frontleaves.greenchaincarbonledger.config.startup

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.common.constants.SqlPrepareData
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil
import com.google.gson.Gson
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
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
class PrepareData(
    private val jdbcTemplate: JdbcTemplate,
) {

    /**
     * 准备上传 SQL
     *
     * 准备上传 SQL, 用于系统启动时进行数据准备
     *
     * @param getPermissionList List<RoleDO> 角色列表
     */
    fun prepareUploadSqlWithPermission(getPermissionList: List<PermissionDO>?) {
        val permissionList = getPermissionList?.toTypedArray() ?: arrayOf()
        SqlPrepareData.PERMISSION_LIST.forEach { permissionName ->
            if (permissionList.isNotEmpty()) {
                permissionList.any { permission -> permission.name.equals(permissionName[0]) }
                    .takeIf { !it }?.let {
                        log.debug("\t\t> 权限数据库缺失 {} 权限，正在初始化...", permissionName[0])
                        jdbcTemplate.update(
                            "INSERT INTO fy_permission (name, description) VALUES (?, ?)",
                            permissionName[0],
                            permissionName[1]
                        )
                    }
            } else {
                log.debug("\t\t> 权限数据库缺失 {} 权限，正在初始化...", permissionName[0])
                jdbcTemplate.update(
                    "INSERT INTO fy_permission (name, description) VALUES (?, ?)",
                    permissionName[0],
                    permissionName[1]
                )
            }
        }
    }

    /**
     * 准备上传 SQL
     *
     * 准备上传 SQL, 用于系统启动时进行数据准备
     *
     * @param getRoleList List<RoleDO> 角色列表
     */
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
            if (role.name.equals("default")) {
                if (getRolePermissions.isNullOrEmpty()) {
                    log.debug("\t> 角色 default 未设置权限，开始初始化默认权限")
                    val createPermission = SqlPrepareData.SQL_ROLE_DEFAULT_PERMISSION_LIST
                    jdbcTemplate.update(
                        "UPDATE fy_role SET permission = ? WHERE name = ?",
                        gson.toJson(createPermission),
                        "default"
                    )
                }
            }
        }
    }

    fun prepareCheckDefaultUser() {
        val getDefaultUser = jdbcTemplate.query("SELECT * FROM fy_user WHERE uid = 1") { rs, _ ->
            return@query UserDO().also {
                it.uid = rs.getLong("uid")
                it.uuid = rs.getString("uuid")
                it.userName = rs.getString("user_name")
                it.email = rs.getString("email")
                it.phone = rs.getString("phone")
                it.role = rs.getString("role")
                it.permission = rs.getString("permission")
            }
        }
        if (getDefaultUser.isEmpty()) {
            log.debug("\t> 默认超级管理员账户不存在，开始初始化默认超级管理员账户")
            // 获取超级管理员Role
            val getConsoleRoleUuid = jdbcTemplate.query("SELECT * FROM fy_role WHERE name = 'console'") { rs, _ ->
                return@query rs.getString("uuid")
            }
            // 插入用户
            jdbcTemplate.update(
                "INSERT INTO fy_user (uuid, user_name, real_name, email, password, role, permission) VALUES (?, ?, ?, ?, ?, ?, ?)",
                ProcessingUtil.createUuid(),
                "console_user",
                "超级管理员",
                "admin@admin.com",
                ProcessingUtil.passwordEncrypt("admin"),
                getConsoleRoleUuid[0],
                "[]"
            )
        }
    }

    fun sqlGetRoleList(): List<RoleDO> {
        return jdbcTemplate.query("SELECT * FROM fy_role") { rs, _ ->
            return@query RoleDO().also {
                it.id = rs.getShort("id")
                it.uuid = rs.getString("uuid")
                it.name = rs.getString("name")
                it.displayName = rs.getString("display_name")
                it.permission = rs.getString("permission")
                it.createdAt = rs.getTimestamp("created_at")
                it.createdUser = rs.getString("created_user")
            }
        }
    }

    fun sqlGetPermissionList(): List<PermissionDO> {
        return jdbcTemplate.query("SELECT * FROM fy_permission") { rs, _ ->
            return@query PermissionDO().also {
                it.pid = rs.getLong("pid")
                it.name = rs.getString("name")
                it.description = rs.getString("description")
            }
        }
    }

    fun sqlCarbonItemType() {
        SqlPrepareData.SQL_CARBON_ITEM_LIST.forEach {
            val getCarbonItem =
                jdbcTemplate.query("SELECT * FROM fy_carbon_item_type WHERE name = ? LIMIT 1", it["name"]!!) { rs, _ ->
                    return@query rs.getString("name")
                }
            if (getCarbonItem.isEmpty()) {
                log.debug("\t> 碳排放数据库缺失 {}[{}] 原料类型，开始初始化...", it["name"]!!, it["displayName"]!!)
                jdbcTemplate.update(
                    "INSERT INTO fy_carbon_item_type (mode, name, display_name, low_calorific, carbon_unit_calorific, fuel_oxidation_rate) VALUES (?, ?, ?, ?, ?, ?)",
                    it["mode"]!!,
                    it["name"]!!,
                    it["displayName"]!!,
                    it["lowCalorific"]!!,
                    it["carbonUnitCalorific"]!!,
                    it["fuelOxidationRate"]!!
                )
            }
        }
    }

    fun sqlProcessFactor() {
        SqlPrepareData.SQL_PROCESS_EMISSION_FACTOR.forEach {
            val getProcessFactor = jdbcTemplate.query(
                "SELECT * FROM fy_process_emission_factor WHERE name = ? LIMIT 1",
                it["name"]!!
            ) { rs, _ ->
                return@query rs.getString("name")
            }
            if (getProcessFactor.isEmpty()) {
                log.debug("\t> 碳排放数据库缺失 {}[{}] 过程因子，开始初始化...", it["name"]!!, it["displayName"]!!)
                jdbcTemplate.update(
                    "INSERT INTO fy_process_emission_factor (name, display_name, factor) VALUES (?, ?, ?)",
                    it["name"]!!,
                    it["displayName"]!!,
                    it["factor"]!!.toDouble()
                )
            }
        }
    }

    fun sqlOtherFactor() {
        SqlPrepareData.SQL_OTHER_EMISSION_FACTOR.forEach {
            val getOtherFactor = jdbcTemplate.query(
                "SELECT * FROM fy_other_emission_factor WHERE name = ? LIMIT 1",
                it["name"]!!
            ) { rs, _ ->
                return@query rs.getString("name")
            }
            if (getOtherFactor.isEmpty()) {
                log.debug("\t> 碳排放数据库缺失 {}[{}] 其他因子，开始初始化...", it["name"]!!, it["displayName"]!!)
                jdbcTemplate.update(
                    "INSERT INTO fy_other_emission_factor (name, display_name, factor, unit) VALUES (?, ?, ?, ?)",
                    it["name"]!!,
                    it["displayName"]!!,
                    it["factor"]!!.toDouble(),
                    it["unit"]!!
                )
            }
        }
    }

    fun sqlCarbonType() {
        SqlPrepareData.SQL_CARBON_TYPE.forEach {
            val getCarbonType =
                jdbcTemplate.query("SELECT * FROM fy_carbon_type WHERE name = ? LIMIT 1", it["name"]!!) { rs, _ ->
                    return@query rs.getString("name")
                }
            if (getCarbonType.isEmpty()) {
                log.debug("\t> 碳排放数据库缺失 {}[{}] 碳排放类型，开始初始化...", it["name"]!!, it["displayName"]!!)
                jdbcTemplate.update(
                    "INSERT INTO fy_carbon_type (uuid, name, display_name, created_at) VALUES (?, ?, ?, ?)",
                    ProcessingUtil.createUuid(),
                    it["name"]!!,
                    it["displayName"]!!,
                    Timestamp(System.currentTimeMillis())
                )
            }
        }
    }

    fun sqlDesulfurizationFactor() {
        SqlPrepareData.SQL_DESULFURIZATION_EMISSION_FACTOR.forEach {
            val getOtherFactor = jdbcTemplate.query(
                "SELECT * FROM fy_desulfurization_factor WHERE name = ? LIMIT 1",
                it["name"]!!
            ) { rs, _ ->
                return@query rs.getString("name")
            }
            if (getOtherFactor.isEmpty()) {
                log.debug("\t> 脱硫排放因子数据库缺失 {}[{}] 其他因子，开始初始化...", it["name"]!!, it["displayName"]!!)
                jdbcTemplate.update(
                    "INSERT INTO fy_desulfurization_factor (name, display_name, desulfurizer_main_content, factor, carbonate_content, unit) VALUES (?, ?, ?, ?, ?, ?)",
                    it["name"]!!,
                    it["displayName"]!!,
                    it["desulfurizerMainContent"]!!,
                    it["factor"]!!.toDouble(),
                    it["carbonateContent"]!!,
                    it["unit"]!!
                )
            }
        }
    }
}