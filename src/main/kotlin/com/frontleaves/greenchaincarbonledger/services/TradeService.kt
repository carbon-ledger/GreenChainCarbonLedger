package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface TradeService {
    /**
     * 强制修改账户信息
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

}