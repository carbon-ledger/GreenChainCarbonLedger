package com.frontleaves.greenchaincarbonledger.exceptions.capture

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.exceptions.MailTemplateDoesNotExistException
import com.frontleaves.greenchaincarbonledger.exceptions.NotEnoughPermissionException
import com.frontleaves.greenchaincarbonledger.exceptions.NotLoginException
import com.frontleaves.greenchaincarbonledger.exceptions.RoleNotFoundException
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
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

    @ExceptionHandler(value = [MailTemplateDoesNotExistException::class])
    fun mailTemplateInputException(e: MailTemplateDoesNotExistException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: {}", e.message, e)
        return ResultUtil.error(timestamp, ErrorCode.TEMPLATE_PARSE_ERROR)
    }

    @ExceptionHandler(value = [RoleNotFoundException::class])
    fun roleNotFoundException(e: RoleNotFoundException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 权限异常: {}", e.message)
        return ResultUtil.error(timestamp, e.message, ErrorCode.SERVER_INTERNAL_ERROR)
    }

    @ExceptionHandler(value = [NotEnoughPermissionException::class])
    fun notEnoughPermissionException(e: NotEnoughPermissionException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 权限异常: {}", e.message)
        val returnData = HashMap<String, Any>().apply {
            put("errorMessage", e.message!!)
            put("permission", e.permission)
        }
        return ResultUtil.error(timestamp, ErrorCode.NO_PERMISSION_ERROR, returnData)
    }

    @ExceptionHandler(value = [NotLoginException::class])
    fun notLoginException(e: NotLoginException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 权限异常: {}", e.message)
        return ResultUtil.error(timestamp, "您还未登陆账户", ErrorCode.NO_LOGIN)
    }

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    fun missingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 业务异常: 请求参数错误, 参数名: ${e.parameterName}")
        return ResultUtil.error(timestamp, "参数 ${e.parameterName} 错误",ErrorCode.PARAM_VARIABLE_ERROR)
    }
}