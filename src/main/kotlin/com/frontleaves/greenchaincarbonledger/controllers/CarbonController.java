package com.frontleaves.greenchaincarbonledger.controllers;

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

import java.util.ArrayList;

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
     * @param request 请求
     * @return carbonService
     */
    @GetMapping("/accounting/get")
    /*
    * 检查组织权限，是使用注解还是在服务层里面体现
    * */
   public ResponseEntity<BaseResponse> getCarbonAccounting(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 getCarbonAccounting 接口");
        long timestamp = System.currentTimeMillis();
        return carbonService.getCarbonAccounting(timestamp,request, limit, page, order);
   }
}
