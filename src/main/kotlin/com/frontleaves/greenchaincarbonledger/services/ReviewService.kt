package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewAdminVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewCheckVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewOrganizeVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

/**
 * ReviewService
 *
 * 用于实现审核服务, 用于组织账户与监管账户的实名认证审核, 以及组织账户的组织审核
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
interface ReviewService {

    /**
     * addReviewFromOrganize
     *
     * 用于组织账户的实名认证审核
     *
     * @param timestamp 时间戳
     * @param reviewOrganizeVO ReviewOrganizeVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun addReviewFromOrganize(
        timestamp: Long,
        reviewOrganizeVO: ReviewOrganizeVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * addReviewFromAdmin
     *
     * 用于组织账户的组织审核
     *
     * @param timestamp 时间戳
     * @param reviewAdminVO ReviewOrganizeVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun addReviewFromAdmin(
        timestamp: Long,
        reviewAdminVO: ReviewAdminVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * checkReviewFormOrganize
     *
     * 用于组织账户的实名认证审核, 由组织账户进行审核
     *
     * @param timestamp 时间戳
     * @param checkId 审核ID
     * @param reviewCheckVO ReviewCheckVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun checkReviewFormOrganize(
        timestamp: Long,
        checkId: String,
        reviewCheckVO: ReviewCheckVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * checkReviewFormAdmin
     *
     * 用于组织账户的组织审核, 以及监管账户的实名认证审核
     *
     * @param timestamp 时间戳
     * @param checkId 审核ID
     * @param reviewCheckVO ReviewCheckVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun checkReviewFormAdmin(
        timestamp: Long,
        checkId: String,
        reviewCheckVO: ReviewCheckVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * reSendReviewFormOrganize
     *
     * 用于组织账户的实名认证审核, 重新申请审核，由超级管理账户进行审核
     *
     * @param timestamp 时间戳
     * @param checkId 审核ID
     * @param reviewOrganizeVO ReviewOrganizeVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun reSendReviewFormOrganize(
        timestamp: Long,
        checkId: String,
        reviewOrganizeVO: ReviewOrganizeVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * reSendReviewFormAdmin
     *
     * 用于组织账户的组织审核, 重新申请审核，由超级管理账户进行审核
     *
     * @param timestamp 时间戳
     * @param checkId 审核ID
     * @param reviewAdminVO ReviewAdminVO
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun reSendReviewFormAdmin(
        timestamp: Long,
        checkId: String,
        reviewAdminVO: ReviewAdminVO,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * getReviewList
     *
     * 用于获取审核列表
     *
     * @param timestamp 时间戳
     * @param page 页码
     * @param limit 每页数量
     * @param order 排序
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun getReviewList(
        timestamp: Long,
        page: String,
        limit: String,
        order: String,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * getReview
     *
     * 用于获取审核详情
     *
     * @param timestamp 时间戳
     * @param id 审核ID
     * @param request HttpServletRequest
     * @return ResponseEntity<BaseResponse>
     */
    fun getReview(
        timestamp: Long,
        type: String,
        id: String,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>
}