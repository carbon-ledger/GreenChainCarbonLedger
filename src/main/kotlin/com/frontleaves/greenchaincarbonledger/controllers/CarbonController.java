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

/**
 * CarbonController
 * <hr/>
 * 用于碳交易技术
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
     * @param start 开始年份
     * @param end 结束年份
     * @param request 请求
     * @return
     */
    @GetMapping("/quota/get")
    @CheckAccountPermission({"Carbon: gteOwnCarbonQuota"})
    public ResponseEntity<BaseResponse> getOwnCarbonQuota (
            @RequestParam(required = false)Integer start,
            @RequestParam(required = false)Integer end,
            HttpServletRequest request){
        log.info("[Control] 请求getOwnCarbonQuota接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        //校验输入的是否为正确的年份
        if (start.toString().matches("^(\\\\d{4})$")|| end.toString().matches("^(\\\\d{4})$")){
            return ResultUtil.error(timestamp, ErrorCode.PARAM_VARIABLE_ERROR);
        }else {
            //校验开始年份和结束年份
            if (start<end){
            //返回业务层操作
                return carbonService.getOwnCarbonQuota(timestamp,request,start,end);
            }else {
                return ResultUtil.error(timestamp,ErrorCode.PARAM_VARIABLE_ERROR);
            }
        }
        }
}
