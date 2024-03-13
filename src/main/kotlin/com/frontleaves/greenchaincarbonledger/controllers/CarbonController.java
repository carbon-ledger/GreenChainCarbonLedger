package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @CheckAccountPermission({"Carbon: gteOwnCarbonQuota"})
    public ResponseEntity<BaseResponse> getOwnCarbonQuota(
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer end,
            HttpServletRequest request) {
        log.info("[Control] 请求getOwnCarbonQuota接口");
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat thisYear = new SimpleDateFormat("yyyy");
        request.getHeader("X-Auth-UUID");
        //校验是否为空
        if (start != null && end != null) {
            //校验输入的是否为正确的年份
            if (start.toString().matches("^(\\d{4})?$") || end.toString().matches("^(\\d{4})?$")) {
                //检查年份顺序
                if (start <= end) {
                    //检查结束年份
                    if (end - Integer.parseInt(thisYear.format(System.currentTimeMillis())) < 0) {
                        //返回业务操作
                        return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
                    } else {
                        return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
                    }
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, ErrorCode.PARAM_SEQUENCE_ERROR);
            }
        } else if (start != null && end == null)  {
            //检查输入是否为正确年份
            if (start.toString().matches("^(\\d{4})?$")) {
                //检查开始年份
                if (start - Integer.parseInt(thisYear.format(System.currentTimeMillis())) < 0) {
                    //返回业务操作
                    return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
            }
        }else if (start == null && end != null){
            //检查输入是否为正确年份
            if (end.toString().matches("^(\\d{4})?$")){
                //检查结束年份
                if (end-Integer.parseInt(thisYear.format(System.currentTimeMillis())) < 0){
                    //返回业务操作
                    return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
                }else {
                    return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
                }
            }else {
                return ResultUtil.error(timestamp,ErrorCode.PARAM_VARIABLE_ERROR);
            }
        }else if (start == null && end == null){
            //返回业务操作
            return carbonService.getOwnCarbonQuota(timestamp, request, start, end);
        }
        return ResultUtil.error(timestamp,ErrorCode.PARAM_VARIABLE_ERROR);
    }
}
