package com.frontleaves.greenchaincarbonledger.controllers

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission
import com.frontleaves.greenchaincarbonledger.context
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

/**
 * 超级控制台控制器
 *
 * 超级控制台控制器, 用于系统超级控制台操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@RestController
@RequestMapping("/super")
class SuperConsoleController(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jdbcTemplate: JdbcTemplate,
) {

    /**
     * closeServer
     *
     * 关闭服务器, 用于关闭服务器
     */
    @GetMapping("/closeServer")
    @CheckAccountPermission("super:closeServer")
    fun closeServer() {
        context.close()
    }

    /**
     * resetSql
     *
     * 重置数据库, 用于重置数据库
     */
    @GetMapping("/resetSql")
    @CheckAccountPermission("super:resetSql")
    fun resetSql() {
        // 删除Redis所有数据
        redisTemplate.keys("*").forEach {
            redisTemplate.delete(it)
        }

        // 删除数据库所有数据
        listOf(
            "SET FOREIGN_KEY_CHECKS = 0",
            "TRUNCATE TABLE fy_approve_manage",
            "TRUNCATE TABLE fy_approve_organize",
            "TRUNCATE TABLE fy_invite",
            "TRUNCATE TABLE fy_permission",
            "TRUNCATE TABLE fy_role",
            "TRUNCATE TABLE fy_user",
            "TRUNCATE TABLE fy_user_ram",
            "TRUNCATE TABLE fy_user_verify",
            "TRUNCATE TABLE fy_process_emission_factor",
            "TRUNCATE TABLE fy_other_emission_factor",
            "TRUNCATE TABLE fy_carbon_item_type",
            "SET FOREIGN_KEY_CHECKS = 1"
        ).forEach { sql ->
            jdbcTemplate.execute(sql)
        }

        context.close()
    }

    /**
     * resetUploadFolder
     *
     * 重置上传文件夹, 用于重置上传文件夹
     */
    @GetMapping("/resetUploadFolder")
    @CheckAccountPermission("super:resetUploadFolder")
    fun resetUploadFolder() {
        // 删除上传文件夹
        val uploadFolder = File("upload")
        if (uploadFolder.exists()) {
            uploadFolder.deleteRecursively()
        }
        context.close()
    }
}