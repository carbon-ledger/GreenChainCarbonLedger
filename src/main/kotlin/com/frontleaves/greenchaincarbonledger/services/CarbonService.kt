package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
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

    fun getCarbonAccounting(
        timestamp: Long,
        request: HttpServletRequest,
        limit: String?,
        page: String?,
        order: String
    ): ResponseEntity<BaseResponse>

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
        limit: String,
        page: String,
        order: String?
    ): ResponseEntity<BaseResponse>

    fun releaseCarbonTrade(
        timestamp: Long,
        request: HttpServletRequest,
        tradeReleaseVO: TradeReleaseVO
    ): ResponseEntity<BaseResponse>

    fun editCarbonTrade(
        timestamp: Long,
        request: HttpServletRequest,
        editTradeVO: EditTradeVO
    ): ResponseEntity<BaseResponse>
}