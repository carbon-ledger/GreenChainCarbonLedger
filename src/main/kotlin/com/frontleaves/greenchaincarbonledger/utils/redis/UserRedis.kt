package com.frontleaves.greenchaincarbonledger.utils.redis

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.common.BusinessConstants
import com.frontleaves.greenchaincarbonledger.common.RedisConstant
import com.frontleaves.greenchaincarbonledger.config.redis.RedisOperation
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 用户Redis
 *
 * 用户Redis，用于用户相关操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Component
class UserRedis(
    redisTemplate: RedisTemplate<String, String>,
    stringRedisTemplate: StringRedisTemplate
) : RedisOperation<String>(redisTemplate, stringRedisTemplate) {
    /**
     * 获取Redis中元素过期时间
     *
     * 获取Redis中元素过期时间, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回过期时间
     */
    override fun getExpiredAt(businessConstants: BusinessConstants, field: String): Long {
        val key = (RedisConstant.TYPE_AUTH + RedisConstant.TABLE_USER + businessConstants.value) + field
        log.info("\t\t> 读取 Redis 键为 {} 的过期时间", key)
        return redisTemplate.getExpire(key)
    }

    /**
     * 删除Redis中元素
     *
     * 删除Redis中元素, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回是否删除成功
     */
    override fun delData(businessConstants: BusinessConstants, field: String): Boolean {
        val key = (RedisConstant.TYPE_AUTH + RedisConstant.TABLE_USER + businessConstants.value) + field
        log.info("\t\t> 删除 Redis 键为 {} 的数据", key)
        return redisTemplate.delete(key)
    }

    /**
     * 获取Redis中元素
     *
     * 获取Redis中元素, 通过业务常量和字段
     *
     * @param businessConstants 业务常量
     * @param field             字段
     * @return 返回元素
     */
    override fun getData(businessConstants: BusinessConstants, field: String): String? {
        val key = (RedisConstant.TYPE_AUTH + RedisConstant.TABLE_USER + businessConstants.value) + field
        log.info("\t\t> 读取 Redis 键为 {} 的数据", key)
        return redisTemplate.opsForValue()[key]
    }

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
    override fun setData(businessConstants: BusinessConstants, field: String, value: String, time: Long): Boolean {
        val key = (RedisConstant.TYPE_AUTH + RedisConstant.TABLE_USER + businessConstants.value) + field
        log.info("\t\t> 设置 Redis 键为 {} 的数据", key)
        redisTemplate.also {
            it.opsForValue()[key] = value
            it.expire(key, time, TimeUnit.MINUTES)
        }
        return true
    }
}