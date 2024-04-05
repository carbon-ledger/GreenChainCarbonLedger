package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface TradeService {
    /**
     * 删除碳交易发布（软删除）
     * <hr/>
     * 强制修改账户信息
     * @param timestamp 时间戳
     * @param request 请求
     * @param id 交易id
     * @return 是否成功
     */
    fun deleteTrade(
        timestamp: Long,
        request: HttpServletRequest,
        id: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取自己组织碳交易发布信息列表
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param type 类型参数
     * @param search 搜索参数
     * @param limit 限制参数
     * @param page 分页参数
     * @param order 排序参数
     * @return 返回一个响应实体，包含碳交易发布信息列表
     */
    fun getOwnTradeList(
        timestamp: Long,
        request: HttpServletRequest,
        type: String,
        search: String?,
        limit: String?,
        page: String?,
        order: String?
    ): ResponseEntity<BaseResponse>

    /**
     * 进行碳交易
     * @param timestamp-时间戳
     * @param request-请求
     * @param id-交易id
     * @return 是否交易完成
     */
    fun buyTrade(
        timestamp: Long,
        request: HttpServletRequest,
        id: String
    ): ResponseEntity<BaseResponse>


    /**
     * 获取碳交易发布信息列表
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param type 类型参数
     * @param search 搜索参数
     * @param limit 限制参数
     * @param page 分页参数
     * @param order 排序参数
     * @return 返回一个响应实体，包含碳交易发布信息列表
     */
    fun getTradeList(
        timestamp: Long,
        request: HttpServletRequest,
        type: String,
        search: String?,
        limit: String,
        page: String,
        order: String
    ): ResponseEntity<BaseResponse>

    /**
     * 修改碳交易发布信息
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param editTradeVO 修改碳交易发布信息对象
     * @param id 交易id
     * @return 返回一个响应实体，包含修改后的碳交易发布信息
     */
    fun editCarbonTrade(
        timestamp: Long,
        request: HttpServletRequest,
        editTradeVO: EditTradeVO,
        id: String
    ): ResponseEntity<BaseResponse>

    /**
     * 审核碳交易发布信息
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param id 交易id
     * @return 返回一个响应实体，包含审核后的碳交易发布信息
     */
    fun reviewTradeList(
        timestamp: Long,
        request: HttpServletRequest,
        id: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取自己购买的碳交易发布信息列表
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @return 返回一个响应实体，包含自己购买的碳交易发布信息列表
     */
    fun getBuyTradeList(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取审核中的碳交易发布信息列表
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @return 返回一个响应实体，包含审核中的碳交易发布信息列表
     */
    fun getReviewTradeList(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳交易发布信息详情获取对方账户的开户信息作为交易使用
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param tradeId 交易id
     * @return 返回一个响应实体，包含碳交易发布信息详情
     */
    fun getTradeBank(
        timestamp: Long,
        request: HttpServletRequest,
        tradeId: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳交易发布信息详情，是否交易成功
     *
     * @param timestamp 时间戳
     * @param request HTTP请求对象
     * @param tradeId 交易id
     * @return 返回一个响应实体，包含碳交易发布信息详情
     */
    fun checkTradeSuccess(
        timestamp: Long,
        request: HttpServletRequest,
        tradeId: String
    ): ResponseEntity<BaseResponse>
}