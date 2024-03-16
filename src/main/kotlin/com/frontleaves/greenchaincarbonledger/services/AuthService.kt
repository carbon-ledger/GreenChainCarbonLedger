package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.*
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * AuthService - 授权服务
 *
 * 授权服务接口, 用于定义授权服务的方法, 用于授权服务的实现
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
interface AuthService {
    /**
     * ## adminUserRegister
     * ### 管理员用户注册
     * 管理员用户注册, 用于管理员用户注册操作
     *
     * @param timestamp 时间戳
     * @param request 请求
     * @param authUserRegisterVO 注册信息
     * @return 注册结果
     */
    fun adminUserRegister(
        timestamp: Long,
        request: HttpServletRequest,
        authUserRegisterVO: AuthUserRegisterVO
    ): ResponseEntity<BaseResponse>

    /**
     * 用户登录
     * @param timestamp 时间戳
     * @param request 请求
     * @param  authLoginVO 登录信息
     * @return 登录结果
     */
    fun userLogin(
        timestamp: Long,
        request: HttpServletRequest,
        authLoginVO: AuthLoginVO
    ): ResponseEntity<BaseResponse>

    /**
     * 用户注册
     * @param timestamp 时间戳
     * @param request 请求
     * @param  authUserRegisterVO 用户注册所提供的信息
     * @return 注册结果
     */
    fun organizeRegister(
        timestamp: Long,
        request: HttpServletRequest,
        // 为了区分用户注册里面使用的形参名，此处加上了NEW
        authOrganizeRegisterVO: AuthOrganizeRegisterVO
    ): ResponseEntity<BaseResponse>

    /**
     * 密码修改
     * @param timestamp 时间戳
     * @param request 请求
     * @param  authChangeVO 用户修改密码所提供的信息
     * @return 修改结果
     */
    fun userChange(
        timestamp: Long,
        request: HttpServletRequest,
        authChangeVO: AuthChangeVO
    ): ResponseEntity<BaseResponse>

    /**
     * 账号注销
     * @param timestamp
     * @param request HttpServletRequest,
     * @param authDeleteVO 用户注销账号所提供的消息
     * @return 注销结果
     */
    fun userDelete(
        timestamp: Long,
        request: HttpServletRequest,
        authDeleteVO: AuthDeleteVO
    ): ResponseEntity<BaseResponse>

    /**
     * 忘记密码
     * @param timestamp 时间戳
     * @param request 请求
     * @param  authForgetCodeVO 用户忘记密码所提供的信息
     * @return 忘记密码结果
     */
    fun forgetCode(
        timestamp: Long,
        request: HttpServletRequest,
        authForgetCodeVO: AuthForgetCodeVO
    ): ResponseEntity<BaseResponse>

    /**
     * 用户注销
     * @param timestamp 时间戳
     * @param request 请求
     * @return 注销结果
     */
    fun userLogout(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>
}