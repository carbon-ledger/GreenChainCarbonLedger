package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity


/**
 * AuthService - 角色服务
 *
 * 角色服务接口, 用于处理角色的添加、编辑、删除等操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author DC_DC
 */
interface RoleService {
    fun addRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleVO: RoleVO
    ): ResponseEntity<BaseResponse>
}