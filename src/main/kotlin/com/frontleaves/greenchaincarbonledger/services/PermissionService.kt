package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import org.springframework.http.ResponseEntity

interface PermissionService {
    fun getPermissionList(
        timestamp: Long,
        limit: Int?,
        page: Int?,
        order: String
    ): ResponseEntity<BaseResponse>
}