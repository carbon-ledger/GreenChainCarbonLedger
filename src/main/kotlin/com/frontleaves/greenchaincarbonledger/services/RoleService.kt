package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * @author 123
 */
interface RoleService {

    /**
     * 获取当前用户角色
     * @param timestamp 时间戳
     * @param request 请求
     * @return 角色列表
     */
    fun getUserCurrent(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    fun getRoleList(
        timestamp: Long,
        request: HttpServletRequest,
        type: String,
        search: String?,
        limit: Int?,
        page: Int?,
        order: String?
    ): ResponseEntity<BaseResponse>

}
