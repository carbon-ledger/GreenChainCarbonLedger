package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/delete/{id}")
    @CheckAccountPermission("{Trade:deleteTrade}")
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


    public ResponseEntity<BaseResponse> editCarbonTrade(
            @RequestBody @Validated EditTradeVO editTradeVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 editCarbonTrade 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return carbonService.editCarbonTrade(timestamp, request, editTradeVO);
    }
}
