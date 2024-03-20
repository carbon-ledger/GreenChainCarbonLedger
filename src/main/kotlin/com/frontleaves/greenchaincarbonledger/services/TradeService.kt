package com.frontleaves.greenchaincarbonledger.services

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
     * @param id 用户id
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

}