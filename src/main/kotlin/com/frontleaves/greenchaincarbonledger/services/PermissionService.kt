package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * PermissionService - 权限服务
 *
 * 权限服务接口, 用于定义权限服务的方法, 用于权限服务的实现
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.impl.PermissionServiceImpl
 * @author xiao_lfeng
 */
interface PermissionService {

    /**
     * getPermissionList
     *
     * 获取权限列表, 用于获取权限列表
     *
     * @param timestamp 时间戳
     * @param request ServiceRequest请求
     * @return 响应实体
     */
    fun getPermissionList(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>
}