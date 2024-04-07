package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.doData.MaterialsDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonAddQuotaVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonConsumeVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * CarbonController
 * <hr/>
 * 用于碳交易技术
 *
 * @author FLASHLACK
 * @since 2024-03-13
 */
@Slf4j
@RestController
@RequestMapping("/carbon")
@RequiredArgsConstructor
public class CarbonController {
    private final CarbonService carbonService;
    private final Gson gson;

    private final BusinessUtil businessUtil;

    /**
     * 获取自己组织碳排放配额
     * <hr/>
     * 获取自己组织碳排放配额
     *
     * @param start   开始年份
     * @param end     结束年份
     * @param request 请求
     * @return 若匹配则进入服务，若不匹配则返回错误信息
     */
    @GetMapping("/quota/get")
    @CheckAccountPermission({"carbon:getOwnCarbonQuota"})
    public ResponseEntity<BaseResponse> getOwnCarbonQuota(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @NotNull HttpServletRequest request) {
        log.info("[Controller] 执行 getOwnCarbonQuota 方法");
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat thisYear = new SimpleDateFormat("yyyy");
        // 数据校验
        if (start != null && end != null && !start.isEmpty() && !end.isEmpty()) {
            log.debug("[Controller] start 和 end 数据存在");
            if (start.matches("^[0-4]{4}") && end.matches("^[0-4]{4}")) {
                if (Integer.parseInt(start) <= Integer.parseInt(end)) {
                    return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
                } else {
                    return ResultUtil.error(timestamp, "年份起始时间不能大于结束时间", ErrorCode.PARAM_VARIABLE_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "年份输入格式不正确", ErrorCode.PARAM_VARIABLE_ERROR);
            }
        } else {
            log.debug("[Controller] start 和 end 数据不存在，或只存在部分");
            if (start != null && !start.isEmpty()) {
                if (!start.matches("^[0-4]{4}")) {
                    return ResultUtil.error(timestamp, "年份输入格式不正确", ErrorCode.PARAM_VARIABLE_ERROR);
                }
            }
            if (end != null && !end.isEmpty()) {
                if (!end.matches("^[0-4]{4}")) {
                    return ResultUtil.error(timestamp, "年份输入格式不正确", ErrorCode.PARAM_VARIABLE_ERROR);
                }
            }
            // 对空数据进行相应
            if (end == null || end.isEmpty()) {
                end = thisYear.format(System.currentTimeMillis());
            }
            return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
        }
    }

    /**
     * 获取碳排放报告
     *
     * @param type    报告类型 [all/search/draft/pending_review/approved/rejected]
     * @param search  搜索关键字
     * @param limit   单页限制个数（默认10个）[不可超过50]
     * @param page    第几页
     * @param order   排序顺序 [asc/desc]
     * @param request HTTP 请求对象
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    @GetMapping("/report/get")
    @CheckAccountPermission({"carbon:getCarbonReport"})
    public ResponseEntity<BaseResponse> getCarbonReport(
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String order,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonReport 接口");
        long timestamp = System.currentTimeMillis();
        ResponseEntity<BaseResponse> checkResult = businessUtil.checkLimitPageAndOrder(timestamp, limit, page, order);
        if (checkResult == null) {
            if ("all".equals(type) || "search".equals(type) || "draft".equals(type) || "pending_review".equals(type) || "approved".equals(type) || "rejected".equals(type)) {
                // 返回业务操作
                if (limit == null) {
                    limit = "";
                }
                if (page == null) {
                    page = "";
                }
                return carbonService.getCarbonReport(timestamp, request, type, search, limit, page, order);
            } else {
                return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.REQUEST_BODY_ERROR);
            }
        } else {
            return checkResult;
        }
    }

    /**
     * 获取自己组织碳排放配额
     * <hr/>
     * 获取自己组织碳排放配额
     *
     * @param request 请求
     * @return carbonService
     */
    @GetMapping("/accounting/get")
    @CheckAccountPermission({"carbon:getCarbonAccounting"})
    public ResponseEntity<BaseResponse> getCarbonAccounting(
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String order,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonAccounting 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonAccounting(timestamp, request, limit, page, order);
    }

    /**
     * 为组织添加配额
     *
     * @param carbonAddQuotaVO 添加配额的值
     * @param bindingResult 结果
     * @param organizeId 组织UUID
     * @param request 请求图
     * @return 是否完成配额的添加
     */
    @PostMapping("/add/{organizeId}")
    @CheckAccountPermission({"carbon:addOrganizeIdQuota"})
    public ResponseEntity<BaseResponse> addOrganizeIdQuota(
            @RequestBody @Validated CarbonAddQuotaVO carbonAddQuotaVO,
            @org.jetbrains.annotations.NotNull BindingResult bindingResult,
            @PathVariable("organizeId") String organizeId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 执行 addOrganizeIdQuota 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        //校验组织uuid
        if (!organizeId.isEmpty()) {
            return carbonService.addOrganizeIdQuota(timestamp, request, organizeId, carbonAddQuotaVO);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
        }
    }

    /**
     * 创建碳排放报告
     *
     * @param carbonConsumeVO 碳排放报告
     * @param bindingResult 结果
     * @param request 请求
     * @return 是否完成报告的创建
     */
    @PostMapping("/report/create")
    @CheckAccountPermission({"carbon:createCarbonReport"})
    public ResponseEntity<BaseResponse> createCarbonReport(
            @RequestBody @Validated CarbonConsumeVO carbonConsumeVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        ArrayList<String> errorMessage = new ArrayList<>();
        log.info("[Controller] 请求 creatCarbonReport 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        MaterialsDO materialsDO;
        // 对原料相关参数进行解析
        try {
            materialsDO = gson.fromJson(carbonConsumeVO.getMaterials(), MaterialsDO.class);

            // 如果为空的部分需要新建内容
            if (materialsDO.getMaterials() == null) {
                materialsDO.setMaterials(new ArrayList<>());
            }
            if (materialsDO.getCourses() == null) {
                materialsDO.setCourses(new ArrayList<>());
            }
            if (materialsDO.getCarbonSequestrations() == null) {
                materialsDO.setCarbonSequestrations(new ArrayList<>());
            }
            if (materialsDO.getDesulfurization() == null) {
                materialsDO.setDesulfurization(new ArrayList<>());
            }
            if (materialsDO.getHeat() == null) {
                materialsDO.setHeat(new ArrayList<>());
            }
            log.info(materialsDO.toString());
        } catch (JsonSyntaxException e) {
            log.error("[Controller] 原料参数解析失败", e);
            errorMessage.add("原料参数解析失败，请检查原料参数格式");
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, errorMessage);
        }
        // 返回业务操作
        switch (carbonConsumeVO.getType()) {
            case "steelProduction" -> {
                return carbonService.createCarbonReport(
                        timestamp, request, carbonConsumeVO,
                        materialsDO.getMaterials(),
                        materialsDO.getCourses(),
                        materialsDO.getCarbonSequestrations(),
                        materialsDO.getHeat()
                );
            }
            case "generateElectricity" -> {
                return carbonService.createCarbonReport1(
                        timestamp, request, carbonConsumeVO,
                        materialsDO.getMaterials(),
                        materialsDO.getDesulfurization()
                );
            }
            default -> {
                errorMessage.add("type 参数错误");
                return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, errorMessage);
            }
        }
    }

    /**
     * 编辑组织配额
     *
     * @param carbonAddQuotaVO 配额值
     * @param bindingResult 结果
     * @param organizeId 组织UUID
     * @param request 请求
     * @return 是否完成配额的编辑
     */
    @PatchMapping("/edit/{organizeId}")
    @CheckAccountPermission({"carbon:editCarbonQuota"})
    public ResponseEntity<BaseResponse> editCarbonQuota(
            @RequestBody @Validated CarbonAddQuotaVO carbonAddQuotaVO,
            @NotNull BindingResult bindingResult,
            @PathVariable String organizeId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求  editCarbonQuota 接口 ");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        if (organizeId.isEmpty()) {
            return ResultUtil.error(timestamp, ErrorCode.PATH_VARIABLE_ERROR);
        } else {
            //进入业务操作
            return carbonService.editCarbonQuota(timestamp, request, organizeId, carbonAddQuotaVO);
        }
    }

    /**
     * 获取组织操作记录
     *
     * @param request 请求
     * @return 操作记录
     */
    @GetMapping("/operate/list")
    @CheckAccountPermission({"carbon:getCarbonOperateList"})
    public ResponseEntity<BaseResponse> getCarbonOperateList(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonOperateList 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonOperateList(timestamp, request);
    }

    /**
     * 获取碳项类型
     *
     * @param request 请求
     * @return 类型
     */
    @GetMapping("/item/type")
    @CheckAccountPermission({"carbon:getCarbonItemType"})
    public ResponseEntity<BaseResponse> getCarbonItemType(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonItemType 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonItemType(timestamp, request);
    }

    /**
     * 获取碳过程排放因子
     *
     * @param request 请求
     * @return 系数
     */
    @GetMapping("/factor/process")
    @CheckAccountPermission({"carbon:getCarbonFactorProcess"})
    public ResponseEntity<BaseResponse> getCarbonFactorProcess(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonFactorProcess 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonFactorProcess(timestamp, request);
    }

    /**
     * 获取碳脱排放因子
     *
     * @param request 请求
     * @return 系数
     */
    @GetMapping("/factor/desulfurization")
    @CheckAccountPermission({"carbon:getCarbonFactorDesulfurization"})
    public ResponseEntity<BaseResponse> getCarbonFactorDesulfurization(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonFactorDesulfurization 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonFactorDesulfurization(timestamp, request);
    }

    /**
     * 获取碳其他排放因子
     *
     * @param request 请求
     * @return 系数
     */
    @GetMapping("/factor/other")
    @CheckAccountPermission({"carbon:getCarbonFactorOther"})
    public ResponseEntity<BaseResponse> getCarbonFactorOther(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonFactorOther 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonFactorOther(timestamp, request);
    }

    @GetMapping("/report/get/{reportId}")
    @CheckAccountPermission({"carbon:getCarbonReport"})
    public ResponseEntity<BaseResponse> getCarbonReportSingle(
            @PathVariable String reportId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonReportSingle 接口");
        long timestamp = System.currentTimeMillis();
        // 对 reportId 进行检查是否是数字
        if (!reportId.matches("^[0-9]*$")) {
            return ResultUtil.error(timestamp, "参数 reportId 错误", ErrorCode.PATH_VARIABLE_ERROR);
        }
        return carbonService.getCarbonReportSingle(timestamp, request, Long.parseLong(reportId));
    }

    @GetMapping("/accounting/get/{reportId}")
    @CheckAccountPermission({"carbon:getCarbonAccounting"})
    public ResponseEntity<BaseResponse> getCarbonAccountingSingle(
            @PathVariable String reportId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonAccountingSingle 接口");
        long timestamp = System.currentTimeMillis();
        // 对 reportId 进行检查是否是数字
        if (!reportId.matches("^[0-9]*$")) {
            return ResultUtil.error(timestamp, "参数 reportId 错误", ErrorCode.PATH_VARIABLE_ERROR);
        }
        return carbonService.getCarbonAccountingSingle(timestamp, request, Long.parseLong(reportId));
    }
}
