package com.frontleaves.greenchaincarbonledger.config.startup

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
open class StartupConfiguration(
    private val jdbcTemplate: JdbcTemplate
) {
    private val prepareData = PrepareData(jdbcTemplate)

    @Bean
    @Order(1)
    open fun startUpPreparation(): CommandLineRunner {
        return CommandLineRunner {
            log.info("============================================================")
            log.info("[Preparation] 系统进行准备检查")
        }
    }

    @Bean
    @Order(2)
    open fun sqlDataPreparation(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] SQL数据进行准备检查")
            log.info("\t> 检查角色数据库是否完整")
            // 检查角色数据库是否完整
            val getRoleList = this.sqlGetRoleList()
            prepareData.prepareUploadSqlWithRole(getRoleList)
            log.info("\t\t> 角色数据库检查完成")
            log.info("\t> 检查权限数据库是否完整")
            // 检查权限数据库是否完整
            val getPermissionList = this.sqlGetPermissionList()
            prepareData.prepareUploadSqlWithPermission(getPermissionList)
            log.info("\t\t> 权限数据库检查完成")
            log.info("[Preparation] SQL数据准备完成")
        }
    }

    @Bean
    @Order(3)
    open fun checkRoleHasPermission(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查角色是否拥有权限")
            val getRoleList = this.sqlGetRoleList()
            prepareData.prepareCheckRoleHasPermission(getRoleList)
        }
    }

    @Bean
    @Order(1000)
    open fun endPreparation(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 系统准备完成")
            log.info("============================================================")
        }
    }

    private fun sqlGetRoleList(): List<RoleDO> {
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

    private fun sqlGetPermissionList(): List<PermissionDO> {
        return jdbcTemplate.query("SELECT * FROM fy_permission") { rs, _ ->
            return@query PermissionDO().also {
                it.pid = rs.getLong("pid")
                it.name = rs.getString("name")
                it.description = rs.getString("description")
            }
        }
    }
}