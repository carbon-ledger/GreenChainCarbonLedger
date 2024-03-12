package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * MailService - 邮件服务
 *
 * 用于发送邮件的服务接口, 包括发送邮件, 发送验证码等
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.impl.MailServiceImpl
 * @author xiao_lfeng
 */
interface MailService {

    /**
     * ## sendMailByCode
     * ### 发送验证码邮件
     * 发送验证码邮件, 用于发送验证码邮件
     *
     * @param request 请求
     * @param email 用户邮箱
     * @param template 邮件模板
     * @return 发送结果
     */
    fun sendMailByCode(timestamp: Long, request: HttpServletRequest, email: String, template: String): ResponseEntity<BaseResponse>

    /**
     * ## sendMail
     * ### 发送邮件
     * 根据模板发送邮件，不包含验证码，仅发送对应邮件
     *
     * @param request 请求
     * @param email 用户邮箱
     * @param template 邮件模板
     * @return 发送结果
     */
    fun sendMail(timestamp: Long, email: String, template: String): ResponseEntity<BaseResponse>

    /**
     * ## checkMailCode
     * ### 验证邮件验证码
     * 验证邮件验证码
     *
     * @param timestamp 时间戳
     * @param email 用户邮箱
     * @param code 验证码
     * @return 验证结果
     */
    fun checkMailCode(email: String): Boolean
}