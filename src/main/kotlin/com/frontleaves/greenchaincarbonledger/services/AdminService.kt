package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.voData.getData.AdminUserChangeVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface AdminService {
    fun resetUserPassword(
        timestamp: Long,
        request: HttpServletRequest,
        adminUserChangeVO: AdminUserChangeVO
    ): ResponseEntity<BaseResponse>
}