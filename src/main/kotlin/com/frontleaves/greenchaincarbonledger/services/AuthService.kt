package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * AuthService - 授权服务
 *
 * 授权服务接口, 用于定义授权服务的方法, 用于授权服务的实现
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
interface AuthService {

    /**
     * userLogin
     *
     * 用户登录
     *
     * 用户登录, 用于用户登录操作
     *
     * @param request 请求
     * @param user 用户名
     * @param password 密码
     * @return 登录结果
     */
    fun userLogin(request: HttpServletRequest, user: String, password: String): ResponseEntity<BaseResponse>
}