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
     * @return
     */
    @GetMapping("/accounting/get")
    /*
    * 检查组织权限，是使用注解还是在服务层里面体现
    * */
   public ResponseEntity<BaseResponse> getCarbonAccounting(
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 getCarbonAccounting 接口");
        long timestamp = System.currentTimeMillis();
        if (limit != null && !limit.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "limit 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        if (page != null && !page.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "page 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("desc");
        list.add("asc");
        if (order != null && !list.contains(order)) {
            return ResultUtil.error(timestamp, "order 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
       return carbonService.getCarbonAccounting(timestamp, limit, page, order);
   }
}
