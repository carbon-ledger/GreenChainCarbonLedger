package com.frontleaves.greenchaincarbonledger.common

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * 项目配置类
 *
 * 用于获取项目信息
 *
 * @param env Spring 环境变量
 * @since v1.0.0-SNAPSHOT
 */
@Component
class ProjectConfig(private val env: Environment) {
    val getProjectInfoMap = HashMap<String, String?>()

    init {
        getProjectInfoMap
            .also {
                it["version"] = env.getProperty("project.version")
                it["buildTime"] = env.getProperty("project.build.time")
                it["javaVersion"] = env.getProperty("project.java")
                it["kotlinVersion"] = env.getProperty("project.kotlin")
            }
    }
}
