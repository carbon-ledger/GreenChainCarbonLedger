package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface CarbonService {
    /**
     *获取自己组织碳排放配额
     * <hr/>
     * 获取自己组织碳排放配额
     * @param timestamp 时间戳
     * @param request 请求
     * @param start 开始年份
     * @param end 结束年份
     * @return 碳排放额
     * @since 2024-03-13
     */
    fun getOwnCarbonQuota(
        timestamp: Long,
        request: HttpServletRequest,
        start: String?,
        end: String
    ): ResponseEntity<BaseResponse>

}