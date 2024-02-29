package com.frontleaves.greenchaincarbonledger.utils.security

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.dao.UserDAO
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

/**
 * # JWT 工具类
 * 用于生成和验证 JWT Token
 *
 * @author xiao_lfeng
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 */
@Component
class JwtUtil(private val userDAO: UserDAO) {
    private val expiredTime = 60 * 60 * 1000L

    fun verifyToken(uuid: String, token: String): Boolean {
        log.debug("[JwtUtil] 进行对 Token 进行验证")
        // 生成加密密钥
        val getUserDO = userDAO.getUserByUuid(uuid)
        if (getUserDO != null) {
            val secret: Key = Keys.hmacShaKeyFor(
                DigestUtils.sha256(getUserDO.password + getUserDO.userName + getUserDO.email + getUserDO.phone)
            )
            // 解析 Token 获取用户
            try {
                val claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                // 从JWT中获取用户名进行匹配
                return if (claimsJws.body.subject == uuid) {
                    log.info("[TOKEN] UUID: $uuid | ${getUserDO.userName} 验证成功")
                    true
                } else {
                    log.debug("[TOKEN] UUID: $uuid 验证失败")
                    false
                }
            } catch (e: JwtException) {
                log.debug("[TOKEN] UUID: $uuid 验证失败")
                return false
            }
        } else {
            log.debug("[TOKEN] UUID: $uuid 不存在")
            return false
        }
    }

    fun signToken(uuid: String): String? {
        // 生成加密密钥
        val getUserDO = userDAO.getUserByUuid(uuid)
        if (getUserDO != null) {
            val secret: Key = Keys.hmacShaKeyFor(
                DigestUtils.sha256(getUserDO.password + getUserDO.userName + getUserDO.email + getUserDO.phone)
            )
            // 生成签名密钥
            return Jwts.builder()
                .setSubject(uuid)
                .setExpiration(Date(System.currentTimeMillis() + expiredTime))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact()
        } else {
            log.debug("[TOKEN] UUID: $uuid 不存在")
            return null
        }
    }
}