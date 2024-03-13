package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.AdminUserChangeVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * 管理员服务
 *
 * 管理员服务, 用于管理员操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.impl.AdminServiceImpl
 */
interface AdminService {

    /**
     * resetUserPassword
     *
     * 重置用户密码, 用于重置用户密码
     *
     * @param timestamp 时间戳
     * @param request 请求
     * @param adminUserChangeVO 管理员用户变更值对象
     * @return 响应实体
     */
    fun resetUserPassword(
        timestamp: Long,
        request: HttpServletRequest,
        adminUserChangeVO: AdminUserChangeVO
    ): ResponseEntity<BaseResponse>
}