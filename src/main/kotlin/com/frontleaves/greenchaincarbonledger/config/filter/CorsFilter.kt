package com.frontleaves.greenchaincarbonledger.config.filter

import KotlinSlf4j.Companion.log
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * 跨域过滤器
 *
 * 用于处理跨域请求
 *
 * @author xiao_lfeng
 * @since v1.0.0-SNAPSHOT
 */
class CorsFilter : Filter {
    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        log.debug("[Filter] 执行 CorsFilter 方法 | 处理跨域请求")
        // 请求头处理
        val response: HttpServletResponse = res as HttpServletResponse
        val request: HttpServletRequest = req as HttpServletRequest

        val origin = request.getHeader("origin")
        response.addHeader("Access-Control-Allow-Origin", origin ?: "*")
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
        chain?.doFilter(request, response)
    }
}