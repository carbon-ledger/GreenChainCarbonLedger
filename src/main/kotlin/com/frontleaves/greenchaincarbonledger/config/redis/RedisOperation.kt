package com.frontleaves.greenchaincarbonledger.config.redis

import com.frontleaves.commons.BusinessConstants
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

abstract class RedisOperation<R>(
    val redisTemplate: RedisTemplate<String, R>,
    val stringRedisTemplate: StringRedisTemplate
) {

    /**
     * 获取Redis中元素过期时间
     *
     * 获取Redis中元素过期时间, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回过期时间
     */
    abstract fun getExpiredAt(businessConstants: BusinessConstants, field: String): Long

    /**
     * 删除Redis中元素
     *
     * 删除Redis中元素, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回是否删除成功
     */
    abstract fun delData(businessConstants: BusinessConstants, field: String): Boolean

    /**
     * 获取Redis中元素
     *
     * 获取Redis中元素, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回元素
     */
    abstract fun getData(businessConstants: BusinessConstants, field: String): R?

    /**
     * 添加Redis中元素
     *
     * 添加Redis中元素, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @param value             值
     * @param time              过期时间
     * @return 返回是否添加成功
     */
    abstract fun setData(businessConstants: BusinessConstants, field: String, value: R, time: Long): Boolean

    /**
     * 获取Redis中元素过期时间
     *
     * 基础方法，用于添加String元素到Redis
     *
     * @param key 索引
     * @return 返回过期时间
     */
    fun getExpiredAt(key: String): Long {
        return redisTemplate.getExpire(key)
    }

    /**
     * 基础添加String元素到Redis
     *
     * 基础方法，用于添加String元素到Redis
     * 默认处理时间，单位时间秒
     *
     * @param key   键
     * @param value 值
     */
    fun set(key: String, value: String, time: Int) {
        stringRedisTemplate.opsForValue()[key] = value
        stringRedisTemplate.expire(key, time.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 基础添加元素到Redis
     *
     * 基础方法，用于添加元素元素到Redis
     * 默认处理时间，单位时间秒
     *
     * @param key   键
     * @param value 值
     */
    fun set(key: String, value: R & Any, time: Int) {
        redisTemplate.opsForValue()[key] = value
        redisTemplate.expire(key, time.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 基础从Redis获取List
     *
     * 基础方法，用于从Redis获取List
     *
     * @param pattern 正则表达式
     * @return 返回List
     */
    fun getList(pattern: String): List<R>? {
        // 获取全部匹配的key
        val keys = stringRedisTemplate.keys(pattern)
        // 获取全部匹配的value
        return if (keys.size > 0) {
            redisTemplate.opsForValue().multiGet(keys)
        } else {
            null
        }
    }
}