package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonConsumeVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonAddQuotaVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonAddQuotaVO
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
        editTradeVO: EditTradeVO,
        id: String
    ): ResponseEntity<BaseResponse>

    /**
     * 创建碳核算报告
     * @param timestamp-时间戳
     * @param request-请求体
     * @param carbonConsumeVO-创建报告所需要的信息体
     * @return 是否完成报告
     */
    fun createCarbonReport(
        timestamp: Long,
        request: HttpServletRequest,
        carbonConsumeVO: CarbonConsumeVO
    ): ResponseEntity<BaseResponse>

    /**
     * 为组织添加配额
     * @param timestamp-时间戳
     * @param request-请求体
     * @param organizeId-组织UUID
     * @param carbonAddQuotaVO-添加的配额值
     * @return 是否完成配额增加
     */
    fun addOrganizeIdQuota(
         timestamp: Long,
         request: HttpServletRequest,
         organizeId: String,
         carbonAddQuotaVO: CarbonAddQuotaVO
    ): ResponseEntity<BaseResponse>



    /**
     * 为组织修改碳配额
     * @param timestamp-时间戳
     * @param request-请求体
     * @param organizeId-组织uuid
     * @param carbonAddQuotaVO-修改内容
     * @return 是否完成
     *
     */
    fun editCarbonQuota(
        timestamp: Long,
        request: HttpServletRequest,
        organizeId: String,
        carbonAddQuotaVO: CarbonAddQuotaVO
    ): ResponseEntity<BaseResponse>
}