package com.frontleaves.greenchaincarbonledger.services

import com.frontleaves.greenchaincarbonledger.models.doData.MaterialsDO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonAddQuotaVO
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonConsumeVO
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity

interface CarbonService {
    /**
     *获取自己组织碳排放配额
     * <hr/>
     * 获取自己组织碳排放配额
     * @param timestamp 时间戳
     * @param request 请求
     * @param start 开始年份
     * @param end 结束年份
     * @return 碳排放额
     * @since 2024-03-13
     */
    fun getOwnCarbonQuota(
        timestamp: Long,
        request: HttpServletRequest,
        start: String?,
        end: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳排放记录
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @param limit 单页限制个数
     * @param page 第几页
     * @param order 排序顺序 [asc/desc]
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonAccounting(
        timestamp: Long,
        request: HttpServletRequest,
        limit: String?,
        page: String?,
        order: String
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳排放报告
     *
     * @param timestamp 时间戳
     * @param request HTTP 请求对象
     * @param type 报告类型 [all/search/draft/pending_review/approved/rejected]
     * @param search 搜索关键字
     * @param limit 单页限制个数
     * @param page 第几页
     * @param order 排序顺序 [asc/desc]
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */

    fun getCarbonReport(
        timestamp: Long,
        request: @NotNull HttpServletRequest,
        type: String,
        search: String,
        limit: String,
        page: String,
        order: String?
    ): ResponseEntity<BaseResponse>

    /**
     * 创建碳核算报告
     * @param timestamp 时间戳
     * @param request 请求体
     * @param carbonConsumeVO 创建报告所需要的信息体
     * @return 是否完成报告
     */
    fun createCarbonReport(
        timestamp: Long,
        request: HttpServletRequest,
        carbonConsumeVO: CarbonConsumeVO,
        materials: MutableList<MaterialsDO.Materials>,
        courses: MutableList<MaterialsDO.Materials>,
        carbonSequestrations: MutableList<MaterialsDO.Materials>,
        heats: MutableList<MaterialsDO.Material>
    ): ResponseEntity<BaseResponse>

    /**
     * 创建碳核算报告
     * @param timestamp 时间戳
     * @param request 请求体
     * @param carbonConsumeVO 创建报告所需要的信息体
     * @return 是否完成报告
     */
    fun createCarbonReport1(
        timestamp: Long,
        request: HttpServletRequest,
        carbonConsumeVO: CarbonConsumeVO,
        materials: MutableList<MaterialsDO.Materials>,
        desulfurization: MutableList<MaterialsDO.Desulfurization>
    ): ResponseEntity<BaseResponse>

    /**
     * 为组织添加配额
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @param organizeId 组织UUID
     * @param carbonAddQuotaVO 添加的配额值
     * @return 是否完成配额增加
     */
    fun addOrganizeIdQuota(
        timestamp: Long,
        request: HttpServletRequest,
        organizeId: String,
        carbonAddQuotaVO: CarbonAddQuotaVO
    ): ResponseEntity<BaseResponse>

    /**
     * 为组织修改碳配额
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @param organizeId 组织uuid
     * @param carbonAddQuotaVO 修改内容
     * @return 是否完成
     *
     */
    fun editCarbonQuota(
        timestamp: Long,
        request: HttpServletRequest,
        organizeId: String,
        carbonAddQuotaVO: CarbonAddQuotaVO
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算报告操作列表
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @return 操作列表
     */
    fun getCarbonOperateList(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算报告操作列表
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @return 操作列表
     */
    fun getCarbonItemType(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算过程因子
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @return 操作列表
     */
    fun getCarbonFactorProcess(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算过程因子
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @return 操作列表
     */
    fun getCarbonFactorDesulfurization(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算过程因子
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @return 操作列表
     */
    fun getCarbonFactorOther(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算报告
     *
     * @param timestamp 时间戳
     * @param request HTTP 请求对象
     * @param reportId 报告ID
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonReportSingle(
        timestamp: Long,
        request: HttpServletRequest,
        reportId: Long
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳排放记录
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @param reportId 报告ID
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonAccountingSingle(
        timestamp: Long,
        request: HttpServletRequest,
        reportId: Long
    ): ResponseEntity<BaseResponse>

    /**
     * 获取审核报告
     *
     * @param timestamp 时间戳
     * @param request 请求体
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonReviewReport(
        timestamp: Long,
        request: HttpServletRequest
    ): ResponseEntity<BaseResponse>

    /**
     * 获取碳核算报告
     *
     * @param timestamp 时间戳
     * @param request HTTP 请求对象
     * @param reportId 报告ID
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonMaterial(
        timestamp: Long,
        request: HttpServletRequest,
        reportId: Long
    ): ResponseEntity<BaseResponse>

    /**
     * 审核报告
     *
     * @param timestamp 时间戳
     * @param request 请求体
     * @param reportId 报告ID
     * @param pass 是否通过
     *
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    fun getCarbonReviewCheck(
        timestamp: Long,
        request: HttpServletRequest,
        reportId: Long,
        pass: String
    ): ResponseEntity<BaseResponse>
}