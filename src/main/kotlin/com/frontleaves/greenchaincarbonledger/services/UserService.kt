package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface UserService {
    fun getUserCurrent(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>
}