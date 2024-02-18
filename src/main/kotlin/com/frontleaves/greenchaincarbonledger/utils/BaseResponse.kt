package com.frontleaves.greenchaincarbonledger.utils

import KotlinSlf4j.Companion.log
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * BaseResponse
 *
 * 自定义返回结果
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class BaseResponse(val output: String, val code: Int, val message: String, val data: Any?) {
    init {
        log.info("============================================================")
    }
}