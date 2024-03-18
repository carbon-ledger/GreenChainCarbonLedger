package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewAdminVO
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
}