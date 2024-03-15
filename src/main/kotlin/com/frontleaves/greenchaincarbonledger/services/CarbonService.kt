package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
import lombok.Data
import org.springframework.http.ResponseEntity
interface CarbonService {
    /**
     * 获取碳排放报告
     *
     * @param timestamp 时间戳
     * @param request HTTP 请求对象
     * @param type 报告类型 [all/search/draft/pending_review/approved/rejected]
     * @param search 搜索关键字
     * @param limit 单页限制个数
     * @param page 第几页
     * @param order 排序顺序 [asc/desc]
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */

    fun getCarbonReport(
        timestamp: Long,
        request: @NotNull HttpServletRequest,
        type: String,
        search: String,
        limit: Int?,
        page: Int?,
        order: String
    ): ResponseEntity<BaseResponse>
}