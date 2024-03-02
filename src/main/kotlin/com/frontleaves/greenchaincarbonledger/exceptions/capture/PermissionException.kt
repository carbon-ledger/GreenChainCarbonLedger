package com.frontleaves.greenchaincarbonledger.exceptions.capture

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.exceptions.RoleNotFoundException
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class PermissionException {

    @ExceptionHandler(value = [RoleNotFoundException::class])
    fun roleNotFoundException(e: RoleNotFoundException): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        log.error("[Exception] 权限异常: {}", e.message)
        return ResultUtil.error(timestamp, e.message, ErrorCode.SERVER_INTERNAL_ERROR)
    }
}