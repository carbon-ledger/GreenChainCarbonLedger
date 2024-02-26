package com.frontleaves.greenchaincarbonledger.config.filter

import KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import com.google.gson.Gson

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * TimestampFilter
 *
 * 用于处理时间戳检查
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
class TimestampFilter : Filter {
    private val gson = Gson()

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain?) {
        val response = res as HttpServletResponse
        val timestamp = System.currentTimeMillis()
        log.info("[Filter] 执行 TimestampFilter 方法 | 时间戳时间检查")
        val getTimestamp = (req as HttpServletRequest).getHeaders("X-Timestamp")
        response.contentType = "application/json;charset=UTF-8"
        if (getTimestamp != null) {
            if (getTimestamp.hasMoreElements()) {
                val time = getTimestamp.nextElement()
                if (time.isNotBlank()) {// 误差在正负2秒内
                    log.debug("\t> 时间戳: {} | 当前时间: {}", time, System.currentTimeMillis())
                    if (System.currentTimeMillis() - time.toLong() < 2000 || time.toLong() - System.currentTimeMillis() > 2000) {
                        log.info("\t> 时间戳检查通过")
                        chain?.doFilter(req, res)
                        return
                    } else {
                        log.info("\t> 时间戳检查未通过")
                        response.also {
                            it.status = 400
                            it.writer.write(
                                gson.toJson(
                                    ResultUtil.error(
                                        timestamp,
                                        ErrorCode.TIMESTAMP_INVALID
                                    ).body
                                )
                            )
                        }
                        return
                    }
                }
            }
        }
        log.warn("\t> 未检测到时间戳")
        res.also {
            it.status = 400
            it.writer.write(
                gson.toJson(
                    ResultUtil.error(
                        timestamp,
                        ErrorCode.TIMESTAMP_NOT_EXIST
                    ).body
                )
            )
        }
    }
}