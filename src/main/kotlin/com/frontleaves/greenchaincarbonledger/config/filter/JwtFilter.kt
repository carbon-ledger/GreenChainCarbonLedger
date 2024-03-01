package com.frontleaves.greenchaincarbonledger.config.filter

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil
import com.google.gson.Gson
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
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
) : BasicHttpAuthenticationFilter() {
    private val gson = Gson()

    override fun onPreHandle(request: ServletRequest, response: ServletResponse, mappedValue: Any?): Boolean {
        val req = request as HttpServletRequest
        log.debug("[Filter] 执行 JwtFilter 方法 | 处理登陆权限认证")
        // 获取请求头中的 token 及 uuid 进行验证
        var token = getAuthzHeader(request)
        val uuid = req.getHeader("X-Auth-UUID")
        log.debug("\tUUID: $uuid")
        // 从数据库获取uuid进行解密检查
        if (uuid != null && token != null) {
            token = token.replace("Bearer ", "")
            if (uuid.isNotBlank() && token.isNotBlank()) {
                return this.isAccessAllowed(request, response, mappedValue)
            }
        }
        return this.onAccessDenied(request, response)
    }

    override fun isAccessAllowed(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        mappedValue: Any?
    ): Boolean {
        val request = servletRequest as HttpServletRequest
        log.debug("\tOverride Function isAccessAllowed")
        val timestamp = System.currentTimeMillis()
        // 获取请求头中的 token 及 uuid 进行验证
        val token = getAuthzHeader(servletRequest)
        val uuid = request.getHeader("X-Auth-UUID")
        // token 解密
        if (jwtUtil.verifyToken(uuid!!, token!!.replace("Bearer ", ""))) {
            return true
        } else {
            servletResponse.contentType = "application/json;charset=UTF-8"
            servletResponse.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.TOKEN_VERIFY_ERROR).body))
            return false
        }
    }

    override fun onAccessDenied(servletRequest: ServletRequest, servletResponse: ServletResponse): Boolean {
        val timestamp = System.currentTimeMillis()
        log.debug("\tOverride Function onAccessDenied")
        // 获取请求头中的 token 及 uuid 进行验证
        val token = getAuthzHeader(servletRequest)
        val uuid = servletRequest.getParameter("X-Auth-UUID")
        // 检查缺失数据
        servletResponse.contentType = "application/json;charset=UTF-8"
        if (token.isNullOrEmpty()) {
            log.info("\t\t> token 为空或不存在")
            servletResponse.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.TOKEN_NOT_EXIST).body))
        } else if (uuid.isNullOrEmpty()) {
            log.info("\t\t> uuid 为空或不存在")
            servletResponse.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.UUID_NOT_EXIST).body))
        } else {
            servletResponse.writer.println(gson.toJson(ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR).body))
        }
        return false
    }

    override fun getAuthzHeader(request: ServletRequest?): String? {
        val httpRequest: HttpServletRequest = request as HttpServletRequest
        return httpRequest.getHeader("Authorization")
    }
}