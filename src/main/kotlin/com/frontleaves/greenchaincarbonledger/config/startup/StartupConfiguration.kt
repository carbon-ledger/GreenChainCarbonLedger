package com.frontleaves.greenchaincarbonledger.config.startup

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File

@Configuration
open class StartupConfiguration(
    jdbcTemplate: JdbcTemplate,
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
            log.debug("\t> 检查角色数据库是否完整")
            // 检查角色数据库是否完整
            val getRoleList = prepareData.sqlGetRoleList()
            prepareData.prepareUploadSqlWithRole(getRoleList)
            log.debug("\t> 检查权限数据库是否完整")
            // 检查权限数据库是否完整
            val getPermissionList = prepareData.sqlGetPermissionList()
            prepareData.prepareUploadSqlWithPermission(getPermissionList)
        }
    }

    @Bean
    @Order(3)
    open fun checkRoleHasPermission(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查角色是否拥有权限")
            val getRoleList = prepareData.sqlGetRoleList()
            prepareData.prepareCheckRoleHasPermission(getRoleList)
        }
    }

    @Bean
    @Order(4)
    open fun checkDefaultUser(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查默认超级管理用户是否存在")
            prepareData.prepareCheckDefaultUser()
        }
    }

    @Bean
    @Order(5)
    open fun checkFolderExist(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查文件夹是否存在")
            if (!File("upload").exists()) {
                log.debug("\t> 创建文件夹: upload")
                File("upload").mkdirs()
            }
            // 分别检查文件夹内部内容是否存在
            if (!File("upload/license").exists()) {
                log.debug("\t> 创建文件夹: upload/license")
                File("upload/license").mkdirs()
            }
            if (!File("upload/legal_id_card").exists()) {
                log.debug("\t> 创建文件夹: upload/legal_id_card")
                File("upload/legal_id_card").mkdirs()
            }
            if (!File("upload/avatar").exists()) {
                log.debug("\t> 创建文件夹: upload/avatar")
                File("upload/avatar").mkdirs()
            }
        }
    }

    @Bean
    @Order(6)
    open fun checkCarbonAccountingRelatedData(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查碳核算相关数据表信息是否完整")
            // 准备碳排放类型表
            prepareData.sqlCarbonItemType()
            // 准备过程因子
            prepareData.sqlProcessFactor()
            // 准备其他因子
            prepareData.sqlOtherFactor()
            prepareData.sqlDesulfurizationFactor()
        }
    }

    @Bean
    @Order(7)
    open fun checkCarbonTypeData(): CommandLineRunner {
        return CommandLineRunner {
            log.info("[Preparation] 检查碳排放采用生产类型是否完整")
            // 准备碳排放类型数据
            prepareData.sqlCarbonType()
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
}