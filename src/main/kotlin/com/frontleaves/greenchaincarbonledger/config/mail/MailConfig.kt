package com.frontleaves.greenchaincarbonledger.config.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * 邮件配置
 *
 * 用于配置邮件发送的配置
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Configuration
open class MailConfig {
    @Value("\${spring.mail.host}")
    private lateinit var emailHost: String

    @Value("\${spring.mail.username}")
    private lateinit var emailUsername: String

    @Value("\${spring.mail.password}")
    private lateinit var emailPassword: String

    @Bean
    open fun javaMailSender(): JavaMailSender {
        return JavaMailSenderImpl()
            .apply {
                defaultEncoding = "UTF-8"
                host = emailHost
                port = 25
                username = emailUsername
                password = emailPassword
                javaMailProperties
                    .also {
                        it["mail.smtp.auth"] = "true"
                        it["mail.smtp.starttls.enable"] = "true"
                        it["mail.debug"] = "false"
                        it["mail.transport.protocol"] = "smtp"
                    }
            }
    }
}