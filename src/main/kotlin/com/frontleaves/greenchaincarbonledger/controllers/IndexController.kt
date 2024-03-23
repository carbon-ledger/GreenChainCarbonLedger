package com.frontleaves.greenchaincarbonledger.controllers

import com.frontleaves.greenchaincarbonledger.common.constants.ProjectConstants
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 首页控制器
 *
 * 首页控制器, 用于返回系统信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@RestController
class IndexController(
    private val getConfig: ProjectConstants,
) {

    /**
     * index
     *
     * 首页, 返回系统信息, 用于检查系统是否正常运行
     *
     * @return 系统信息
     */
    @RequestMapping("/info")
    fun index(): ResponseEntity<BaseResponse> {
        val timestamp = System.currentTimeMillis()
        return ResultUtil.custom(
            timestamp,
            "Success",
            200,
            "欢迎使用 GreenChainCarbonLedger 系统，当您看到此状态时系统正常运行中",
            getConfig.getProjectInfoMap
        )
    }
}