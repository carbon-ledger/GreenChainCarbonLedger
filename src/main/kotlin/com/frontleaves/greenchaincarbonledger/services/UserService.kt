package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserForceEditVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserEditVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * 用户服务
 *
 * 用户服务接口, 用于用户相关操作
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng AND DC_DC AND FLASHLACK
 */
interface UserService {

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
     * 获取用户列表
     * @param timestamp 时间戳
     * @param request 请求
     * @param type 类型
     * @param search 搜索
     * @param limit 限制
     * @param page 页码
     * @param order 排序
     * @return 用户列表
     */
    fun getUserList(
        timestamp: Long,
        request: HttpServletRequest,
        type: String,
        search: String?,
        limit: Int?,
        page: Int?,
        order: String?
    ): ResponseEntity<BaseResponse>

    /**
     * 编辑用户
     *
     * 编辑用户, 通过用户编辑视图对象
     *
     * @param timestamp 时间戳
     * @param request   请求
     * @param userEditVO 用户编辑视图对象
     * @return 返回响应实体
     */
    fun editUser(
        timestamp: Long,
        request: HttpServletRequest,
        userEditVO: UserEditVO
    ): ResponseEntity<BaseResponse>

    /**
     * 强制修改账户信息
     * <hr/>
     * 强制修改账户信息
     * @param timestamp 时间戳
     * @param request 请求
     * @param userUuid 用户UUID
     * @param UserForceEditvo 用户账户信息
     * @return 返回响应实体
     */
    fun putUserForceEdit(
        timestamp: Long,
        request: HttpServletRequest,
        userUuid:String,
        userForceEditVO: UserForceEditVO
    ): ResponseEntity<BaseResponse>

}