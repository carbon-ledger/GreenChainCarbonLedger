package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthLoginVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthOrganizeRegisterVO
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
     * adminUserRegister
     *
     * 管理员用户注册
     *
     * 管理员用户注册, 用于管理员用户注册操作
     *
     * @param timestamp 时间戳
     * @param request 请求
     * @param authOrganizeRegisterVO 注册信息
     * @return 注册结果
     */
    fun adminUserRegister(
        timestamp: Long,
        request: HttpServletRequest,
        authOrganizeRegisterVO: AuthOrganizeRegisterVO
    ): ResponseEntity<BaseResponse>

    fun userLogin(
        timestamp: Long,
        request: HttpServletRequest,
        authLoginVO: AuthLoginVO
    ): ResponseEntity<BaseResponse>
}