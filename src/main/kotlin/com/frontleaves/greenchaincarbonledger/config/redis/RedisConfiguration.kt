package com.frontleaves.greenchaincarbonledger.config.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
open class RedisConfiguration(env: Environment) {

    private val host = env.getProperty("spring.data.redis.host")
    private val port = env.getProperty("spring.data.redis.port")?.toInt()
    private val password = env.getProperty("spring.data.redis.password")

    @Bean
    open fun jedisConnectionFactory(): JedisConnectionFactory {
        val config = RedisStandaloneConfiguration().also {
            it.hostName = host!!
            it.port = port!!
            it.setPassword(password)
        }
        return JedisConnectionFactory(config)
    }

    @Bean
    open fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()

        // 配置Redis编码格式
        val stringSerializer: RedisSerializer<String> = StringRedisSerializer()
        val jsonSerializer: RedisSerializer<Any> = GenericJackson2JsonRedisSerializer()

        return redisTemplate.also {
            it.connectionFactory = connectionFactory
            it.keySerializer = stringSerializer
            it.valueSerializer = jsonSerializer
            it.hashKeySerializer = stringSerializer
            it.hashValueSerializer = jsonSerializer
        }
    }
}