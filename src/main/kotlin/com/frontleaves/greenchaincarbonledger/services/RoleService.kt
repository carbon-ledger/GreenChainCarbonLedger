package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * RoleService - 角色服务
 *
 * 角色服务接口, 用于处理角色的添加、编辑、删除等操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
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

    /**
     * 编辑角色
     * @param timestamp 时间戳
     * @param request 请求
     * @param roleVO 角色信息
     * @param roleUuid 角色uuid
     * @return 编辑结果
     */
    fun editRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleVO: RoleVO,
        roleUuid: String
    ): ResponseEntity<BaseResponse>

    /**
     * 删除角色
     * @param timestamp 时间戳
     * @param request 请求
     * @param roleUuid 角色uuid
     * @return 删除结果
     */
    fun deleteRole(
        timestamp: Long,
        request: HttpServletRequest,
        roleUuid: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取角色列表
     * @param timestamp 时间戳
     * @param request 请求
     * @param type 类型
     * @param search 搜索
     * @param limit 限制
     * @param page 页码
     * @param order 排序
     * @return 角色列表
     */
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
