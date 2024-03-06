package com.frontleaves.greenchaincarbonledger.controllers

import com.frontleaves.greenchaincarbonledger.context
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/super")
class SuperConsoleController(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jdbcTemplate: JdbcTemplate,
) {

    @GetMapping("/closeServer")
    fun closeServer() {
        context.close()
    }

    @GetMapping("/resetSql")
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
            "SET FOREIGN_KEY_CHECKS = 1"
        ).forEach { sql ->
            jdbcTemplate.execute(sql)
        }

        context.close()
    }
}