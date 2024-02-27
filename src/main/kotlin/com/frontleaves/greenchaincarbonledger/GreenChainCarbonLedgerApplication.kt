package com.frontleaves.greenchaincarbonledger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * GreenChainCarbonLedgerApplication
 *
 * GreenChainCarbonLedgerApplication, 用于启动 Spring Boot 项目
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
open class GreenChainCarbonLedgerApplication

fun main(args: Array<String>) {
    runApplication<GreenChainCarbonLedgerApplication>(*args)
}
