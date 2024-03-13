package com.frontleaves.greenchaincarbonledger.config.filter

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.dao.AuthDAO
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil
import com.google.gson.Gson
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.springframework.stereotype.Component

/**
 * JwtFilter
 *
 * 用于处理 JWT 认证
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val authDAO: AuthDAO
) : BasicHttpAuthenticationFilter() {
    private val gson = Gson()

    override fun onPreHandle(servletRequest: ServletRequest, servletResponse: ServletResponse, mappedValue: Any?): Boolean {
        val request = servletRequest as HttpServletRequest
        log.info("[Filter] 执行 JwtFilter 方法 | 处理登陆权限认证")
        // 获取请求头中的 token 及 uuid 进行验证
        var token = getAuthzHeader(servletRequest)
        val uuid = request.getHeader("X-Auth-UUID")
        log.debug("\tUUID: $uuid")
        // 从数据库获取uuid进行解密检查
        if (uuid != null) {
            token = token.replace("Bearer ", "")
            if (uuid.isNotBlank() && token.isNotBlank()) {
                return this.isAccessAllowed(servletRequest, servletResponse, mappedValue)
            }
        }
        return this.onAccessDenied(servletRequest, servletResponse)
    }

    override fun isAccessAllowed(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        mappedValue: Any?
    ): Boolean {
        val response = servletResponse as HttpServletResponse
        response.contentType = "application/json;charset=UTF-8"
        val request = servletRequest as HttpServletRequest
        log.debug("\tOverride Function isAccessAllowed")
        val timestamp = System.currentTimeMillis()
        // 获取请求头中的 token 及 uuid 进行验证
        val token = getAuthzHeader(servletRequest)
        val uuid = request.getHeader("X-Auth-UUID")
        // token 解密
        if (jwtUtil.verifyToken(uuid!!, token.replace("Bearer ", ""))) {
            var isExist = false
            // 从缓存获取登陆信息
            val getUserLoginList = authDAO.getAuthorize(ProcessingUtil.getAuthorizeUserUuid(request))
            for (userLoginDO in getUserLoginList) {
                if (userLoginDO != null) {
                    val getAuthorizeToken = ProcessingUtil.getAuthorizeToken(request)
                    if (userLoginDO.token == getAuthorizeToken) {
                        // 检查Ip与UserAgent
                        if (userLoginDO.userIp.equals(request.remoteAddr, ignoreCase = true)
                            && userLoginDO.userAgent.equals(request.getHeader("User-Agent"), ignoreCase = true)
                        ) {
                            log.info("\t\t> Token 验证成功")
                            isExist = true
                            break
                        } else {
                            log.info("\t\t> UserAgent 或 IP 不匹配")
                        }
                    }
                }
            }
            return if (isExist) {
                true
            } else {
                log.info("\t\t> Token 验证失败")
                response.writer.println(gson.toJson(ResultUtil.error(timestamp, "用户登陆失效", ErrorCode.TOKEN_VERIFY_ERROR).body))
                response.status = 401
                false
            }
        } else {
            response.writer.println(gson.toJson(ResultUtil.error(timestamp, "用户登陆失效", ErrorCode.TOKEN_VERIFY_ERROR).body))
            response.status = 401
            return false
        }
    }

    override fun onAccessDenied(servletRequest: ServletRequest, servletResponse: ServletResponse): Boolean {
        val response = servletResponse as HttpServletResponse
        val timestamp = System.currentTimeMillis()
        log.debug("\tOverride Function onAccessDenied")
        // 获取请求头中的 token 及 uuid 进行验证
        val token = getAuthzHeader(servletRequest)
        val uuid = servletRequest.getParameter("X-Auth-UUID")
        // 检查缺失数据
        response.contentType = "application/json;charset=UTF-8"
        if (token.isEmpty()) {
            log.info("\t\t> token 为空或不存在")
            response.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.TOKEN_NOT_EXIST).body))
            response.status = 401
        } else if (uuid.isNullOrEmpty()) {
            log.info("\t\t> uuid 为空或不存在")
            response.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.UUID_NOT_EXIST).body))
            response.status = 401
        } else {
            response.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR).body))
            response.status = 500
        }
        return false
    }

    override fun getAuthzHeader(request: ServletRequest?): String {
        val httpRequest: HttpServletRequest = request as HttpServletRequest
        return httpRequest.getHeader("Authorization").replace("Bearer ", "")
    }
}