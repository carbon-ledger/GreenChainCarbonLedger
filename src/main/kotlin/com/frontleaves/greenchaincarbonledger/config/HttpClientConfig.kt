package com.frontleaves.greenchaincarbonledger.config

import org.apache.commons.httpclient.HttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * HttpClient配置
 *
 * HttpClient配置类, 用于配置HttpClient的连接参数
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Configuration
open class HttpClientConfig {

    @Bean
    open fun httpClient(): HttpClient {
        return HttpClient()
            .apply { httpConnectionManager.params.connectionTimeout = 5000 }
    }
}