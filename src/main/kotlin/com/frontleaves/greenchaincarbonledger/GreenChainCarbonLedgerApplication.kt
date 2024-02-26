package com.frontleaves.greenchaincarbonledger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
open class GreenChainCarbonLedgerApplication

fun main(args: Array<String>) {
    runApplication<GreenChainCarbonLedgerApplication>(*args)
}
