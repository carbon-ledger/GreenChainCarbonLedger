package com.frontleaves.greenchaincarbonledger.controllers;


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

    /**
     *  进行碳交易
     * @param id-交易id
     * @param request-请求
     * @return 是否完成交易
     */
    @PatchMapping("/buy/{id}")
    @CheckAccountPermission("{trade:buyTrade}")
    public ResponseEntity<BaseResponse> buyTrade(
            @PathVariable String id,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求 buyTrade 接口 ");
        long timestamp =System.currentTimeMillis();
        //校验参数
        if (id.isEmpty()){
            return ResultUtil.error(timestamp,"Path参数错误",ErrorCode.REQUEST_BODY_ERROR);
        }
        //返回业务操作
        return tradeService.buyTrade(timestamp,request,id);
    }
}
