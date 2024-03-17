package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

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
}
