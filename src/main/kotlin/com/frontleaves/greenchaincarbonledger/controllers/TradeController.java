package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.*;
import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * TradeController
 * <hr/>
 * 交易控制器，用于处理交易相关的请求
 *
 * @author FLASHLACK
 */
@Slf4j
@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {
    private final CarbonService carbonService;
    private final TradeService tradeService;
    private final BusinessUtil businessUtil;

    @PostMapping("/sell")
    public ResponseEntity<BaseResponse> releaseCarbonTrade(
            @RequestBody @Validated TradeReleaseVO tradeReleaseVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 releaseCarbonTrade 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return carbonService.releaseCarbonTrade(timestamp, request, tradeReleaseVO);
    }

    @CheckAccountPermission("{Trade:deleteTrade}")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteTrade(
            @PathVariable("id")String id,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 deleteTrade 接口");
        long timestamp =System.currentTimeMillis();
        //进行参数校验
        if(id == null || id.isEmpty()){
            return ResultUtil.error(timestamp, "Path 参数错误",ErrorCode.PATH_VARIABLE_ERROR);
        }else {
            return tradeService.deleteTrade(timestamp,request,id);
        }
    }
    @GetMapping("/send")
    @CheckAccountPermission("{Trade:getOwnTradeList}")
    public ResponseEntity<BaseResponse> getOwnTradeList (
            @RequestParam String type,
            @RequestParam (required = false)String search,
            @RequestParam (required = false)String limit,
            @RequestParam (required = false)String page,
            @RequestParam (required = false)String order,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 getOwnTrade 接口");
        long timestamp =System.currentTimeMillis();
        //校验参数
        ResponseEntity<BaseResponse> checkResult = businessUtil.checkLimitPageAndOrder(timestamp, limit, page, order);
        if (checkResult != null) {
            return checkResult;
        } else {
            if (!"all".equals(type) && !"search".equals(type) && !"draft".equals(type) && !"completed".equals(type) && !"cancelled".equals(type)) {
                return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.REQUEST_BODY_ERROR);
            }
            if (limit == null) {
                limit = "";
            }
            if (page == null) {
                page = "";
            }
            return tradeService.getOwnTradeList(timestamp, request, type, search, limit, page, order);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getTradeList(
            //需要Query参数
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String order,
            @NotNull HttpServletRequest request){
        log.info("[Controller] 请求 getTradeList 接口");
        long timestamp = System.currentTimeMillis();
        ResponseEntity<BaseResponse> checkResult = businessUtil.checkLimitPageAndOrder(timestamp, limit, page, order);
        if (checkResult != null) {
            return checkResult;
        }
        if ("all".equals(type) || "search".equals(type) || "active".equals(type) || "completed".equals(type)) {
            //业务操作
            if (limit == null) {
                limit = "";
            }
            if (page == null) {
                page = "";
            }
            return tradeService.getTradeList(timestamp, request, type, search, limit, page, order);
        } else {
            return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.PARAM_VARIABLE_ERROR);
        }
    }
}
