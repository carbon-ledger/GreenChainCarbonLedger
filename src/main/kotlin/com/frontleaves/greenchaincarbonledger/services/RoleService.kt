package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO
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

    /**
     * AuthService - 角色服务
     *
     * 角色服务接口, 用于处理角色的添加、编辑、删除等操作
     *
     * @since v1.0.0-SNAPSHOT
     * @version v1.0.0-SNAPSHOT
     * @author DC_DC
     */
    fun addRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleVO: RoleVO
    ): ResponseEntity<BaseResponse>

    fun editRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleVO: RoleVO,
        roleUuid: String
    ): ResponseEntity<BaseResponse>

    fun deleteRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleUuid: String
    ): ResponseEntity<BaseResponse>
}