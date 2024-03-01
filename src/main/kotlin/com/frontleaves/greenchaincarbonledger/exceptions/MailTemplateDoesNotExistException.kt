package com.frontleaves.greenchaincarbonledger.exceptions

/**
 * 模板不存在异常
 *
 * 当模板不存在时抛出的异常
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @param message 异常信息
 * @author xiao_lfeng
 */
class MailTemplateDoesNotExistException(message: String): RuntimeException(message)