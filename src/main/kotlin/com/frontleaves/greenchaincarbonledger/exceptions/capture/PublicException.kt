package com.frontleaves.greenchaincarbonledger.exceptions.capture

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.exceptions.UserDoesNotExistException
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.thymeleaf.exceptions.TemplateInputException

/**
 * BusinessException
 *
 * 业务异常处理，用于处理业务异常，返回错误信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@RestControllerAdvice
class PublicException {
    @ExceptionHandler(value = [Exception::class])
    fun exception(e: Exception): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: ${e.message}", e)
        return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR)
    }

    @ExceptionHandler(value = [NoResourceFoundException::class])
    fun noResourceFound(e: NoResourceFoundException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 页面未找到<{}>", e.resourcePath)
        return ResultUtil.error(timestamp, "页面 /${e.resourcePath} 不存在", ErrorCode.PAGE_NOT_FOUNDED)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun httpMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 请求体参数错误")
        return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_MISSING)
    }

    @ExceptionHandler(value = [HttpRequestMethodNotSupportedException::class])
    fun httpRequestMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 请求方法不支持")
        return ResultUtil.error(
            timestamp,
            "请求方法 [${e.method}] 不支持,受支持的请求方法为 ${e.supportedMethods.contentToString()}",
            ErrorCode.REQUEST_METHOD_NOT_SUPPORTED
        )
    }

    @ExceptionHandler(value = [TemplateInputException::class])
    fun templateInputException(e: TemplateInputException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 模版不存在")
        return ResultUtil.error(timestamp, "模板 ${e.templateName ?: e.message} 不存在", ErrorCode.TEMPLATE_PARSE_ERROR)
    }

    @ExceptionHandler(value = [UserDoesNotExistException::class])
    fun mailTemplateInputException(e: UserDoesNotExistException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 用户不存在", e)
        return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
    }


}