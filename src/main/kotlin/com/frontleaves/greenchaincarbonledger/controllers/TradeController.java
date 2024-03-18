package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TradeController
 * 交易控制器，用于处理交易相关的请求
 * @author FLASHLACK
 */
@Slf4j
@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;
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
}
