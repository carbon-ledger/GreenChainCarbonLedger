package com.frontleaves.greenchaincarbonledger.utils

import KotlinSlf4j.Companion.log
import org.springframework.http.ResponseEntity
import java.util.HashMap

/**
 * ResultUtil
 *
 * 返回结果工具类
 *
 * @author xiao_lfeng
 * @since v1.0.0-SNAPSHOT
 */
object ResultUtil {
    /**
     * Success
     *
     * 操作成功 - 不带数据
     *
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun success(timestamp: Long): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.info("<200>Success | {} - 不带数据", "操作成功")
        // 返回结果
        return ResponseEntity
            .status(200)
            .body(BaseResponse("Success", 200, "操作成功", null))
    }

    /**
     * Success
     *
     * 操作成功 - 不带数据
     *
     * @param message 消息
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun success(timestamp: Long, message: String): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.info("<200>Success | {} - 不带数据", message)
        // 返回结果
        return ResponseEntity
            .status(200)
            .body(BaseResponse("Success", 200, message, null))
    }

    /**
     * Success
     *
     * 操作成功 - 带数据
     *
     * @param data 数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun success(timestamp: Long, data: Any?): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.info("<200>Success | {} - 带数据", "操作成功")
        // 返回结果
        return ResponseEntity
            .status(200)
            .body(BaseResponse("Success", 200, "操作成功", data))
    }

    /**
     * Success
     *
     * 操作成功 - 带数据
     *
     * @param message 消息
     * @param data 数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun success(timestamp: Long, message: String, data: Any?): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.info("<200>Success | {} - 带数据", message)
        // 返回结果
        return ResponseEntity
            .status(200)
            .body(BaseResponse("Success", 200, message, data))
    }

    /**
     * Error
     *
     * 操作失败 - 不带数据
     *
     * @param errorCode 错误码
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun error(timestamp: Long, errorCode: ErrorCode): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.error("<${errorCode.code / 100}>${errorCode.output}[${errorCode.code}] | ${errorCode.message} - 不带数据")
        // 返回结果
        return ResponseEntity
            .status(errorCode.code / 100)
            .body(BaseResponse(errorCode.output, errorCode.code, errorCode.message, null))
    }

    /**
     * Error
     *
     * 操作失败 - 带数据
     *
     * @param errorCode 错误码
     * @param data 数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun error(timestamp: Long, errorCode: ErrorCode, data: Any?): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.warn("<${errorCode.code / 100}>${errorCode.output}[${errorCode.code}] | ${errorCode.message} - 带数据")
        // 返回结果
        return ResponseEntity
            .status(errorCode.code / 100)
            .body(BaseResponse(errorCode.output, errorCode.code, errorCode.message, data))
    }

    /**
     * Error
     *
     * 操作失败 - 不带数据
     *
     * @param errorMessage 错误消息
     * @param errorCode 错误码
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun error(timestamp: Long, errorMessage: String, errorCode: ErrorCode): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.warn("<${errorCode.code / 100}>${errorCode.output}[${errorCode.code}] | $errorMessage - 不带数据")
        val errorData = HashMap<String, String>()
            .also { it["errorMessage"] = errorMessage }
        // 返回结果
        return ResponseEntity
            .status(errorCode.code / 100)
            .body(BaseResponse(errorCode.output, errorCode.code, errorCode.message, errorData))
    }

    /**
     * Error
     *
     * 操作失败 - 不带数据 - 默认200返回
     *
     * @param errorCode 错误码
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun errorDefaultStatus(timestamp: Long, errorCode: ErrorCode): BaseResponse {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.warn("<200>${errorCode.output}[${errorCode.code}] | ${errorCode.message} - 不带数据 | 默认200返回")
        // 返回结果
        return BaseResponse(errorCode.output, errorCode.code, errorCode.message, null)
    }

    /**
     * Error
     *
     * 操作失败 - 带数据 - 默认200返回
     *
     * @param timestamp 开销时间
     * @param output 输出
     * @param code 错误码
     * @param message 消息
     * @param data 数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    fun custom(timestamp: Long, output: String, code: Int, message: String, data: Any?): ResponseEntity<BaseResponse> {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp)
        log.info("<$code>$output | [自定义]$message")
        // 检查 code 位数
        val responseCode = if (code.toString().length == 5) {
            code / 100
        } else {
            code
        }
        // 返回结果
        return ResponseEntity
            .status(responseCode)
            .body(BaseResponse(output, code, message, data))
    }
}