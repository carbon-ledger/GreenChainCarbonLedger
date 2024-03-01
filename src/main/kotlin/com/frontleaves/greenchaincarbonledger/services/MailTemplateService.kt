package com.frontleaves.greenchaincarbonledger.services

/**
 * MailTemplateService - 邮件模板服务
 *
 * 用于发送邮件的服务接口, 包括发送邮件, 发送验证码等
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.impl.MailTemplateServiceImpl
 * @author xiao_lfeng
 */
interface MailTemplateService {

    /**
     * ## sendMail
     * ### 发送邮件
     * 发送邮件, 用于发送邮件, 邮件发送模板
     *
     * @param email 用户邮箱
     * @param prepareData 邮件准备数据
     * @param template 邮件模板
     */
    fun sendMail(email: String, prepareData: HashMap<String, Any>, template: String)

    /**
     * ## mailSendCode
     * ### 发送验证码邮件
     * 发送验证码邮件, 用于发送验证码邮件，发送的内容是验证码类型
     *
     * @param email 用户邮箱
     * @param code 验证码
     * @param template 邮件模板
     */
    fun mailSendCode(email: String, code: String, template: String)

    /**
     * ## mailSendWithTemplate
     * ### 发送邮件
     * 发送邮件, 用于发送邮件, 根据模板进行邮件内容发送，发送的内容不是验证码类型
     *
     * @param email 用户邮箱
     * @param template 邮件模板
     */
    fun mailSendWithTemplate(email: String, template: String)

    /**
     * ## mailSend
     * ### 发送邮件
     * 发送邮件, 用于发送邮件, 根据模板进行邮件内容发送，发送的内容不是验证码类型
     *
     * @param email 用户邮箱
     * @param data 邮件数据
     * @param template 邮件模板
     */
    fun mailSend(email: String, data: HashMap<String, Any>, template: String)
}