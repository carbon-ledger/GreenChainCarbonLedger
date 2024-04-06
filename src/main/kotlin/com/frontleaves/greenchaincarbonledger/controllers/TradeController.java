package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import com.frontleaves.greenchaincarbonledger.utils.*;
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
    private final TradeService tradeService;
    private final BusinessUtil businessUtil;

    /**
     * 创建交易
     *
     * @param tradeReleaseVO 交易信息
     * @param bindingResult  校验结果
     * @param request        请求
     * @return 响应
     */
    @PostMapping("/sell")
    public ResponseEntity<BaseResponse> releaseCarbonTrade(
            @RequestBody @Validated TradeReleaseVO tradeReleaseVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 releaseCarbonTrade 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return tradeService.releaseCarbonTrade(timestamp, request, tradeReleaseVO);
    }

    /**
     * 删除交易
     *
     * @param id      交易id
     * @param request 请求
     * @return 响应
     */
    @DeleteMapping("/delete/{id}")
    @CheckAccountPermission({"trade:deleteTrade"})
    public ResponseEntity<BaseResponse> deleteTrade(
            @PathVariable("id") String id,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 changeStatus 接口");
        long timestamp = System.currentTimeMillis();
        //进行参数校验
        if (id == null || id.isEmpty()) {
            return ResultUtil.error(timestamp, "Path 参数错误", ErrorCode.PATH_VARIABLE_ERROR);
        } else {
            return tradeService.deleteTrade(timestamp, request, id);
        }
    }

    /**
     * 获取交易列表
     *
     * @param type    类型
     * @param search  搜索
     * @param limit   限制
     * @param page    页码
     * @param order   排序
     * @param request 请求
     * @return 响应
     */
    @GetMapping("/send")
    @CheckAccountPermission({"trade:getOwnTradeList"})
    public ResponseEntity<BaseResponse> getOwnTradeList(
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String order,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getOwnTradeList 接口");
        long timestamp = System.currentTimeMillis();
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

    /**
     * 获取交易列表
     *
     * @param type    类型
     * @param search  搜索
     * @param limit   限制
     * @param page    页码
     * @param order   排序
     * @param request 请求
     * @return 响应
     */
    @GetMapping("/list")
    @CheckAccountPermission({"trade:getAllTradeList"})
    public ResponseEntity<BaseResponse> getTradeList(
            //需要Query参数
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String order,
            @NotNull HttpServletRequest request) {
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

    /**
     * 审核交易
     *
     * @param id      交易id
     * @param pass    审核结果
     * @param request 请求
     * @return 响应
     */
    @PatchMapping("/review/{tradeId}")
    public ResponseEntity<BaseResponse> reviewTradeList(
            @PathVariable("tradeId") String id,
            @RequestParam Boolean pass,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getOwnTrade 接口");
        long timestamp = System.currentTimeMillis();
        if (id != null && !id.isEmpty()) {
            return tradeService.reviewTradeList(timestamp, request, id, pass);
        } else {
            return ResultUtil.error(timestamp, "Path 参数错误", ErrorCode.PATH_VARIABLE_ERROR);
        }
    }

    /**
     * 编辑交易
     *
     * @param tradeReleaseVO 编辑交易VO
     * @param bindingResult 校验结果
     * @param id 交易id
     * @param request 请求
     * @return 响应
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<BaseResponse> editCarbonTrade(
            @RequestBody @Validated TradeReleaseVO tradeReleaseVO,
            @NotNull BindingResult bindingResult,
            @PathVariable String id,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 editCarbonTrade 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return tradeService.editCarbonTrade(timestamp, request, tradeReleaseVO, id);
    }

    /**
     * 进行碳交易
     *
     * @param id-交易id
     * @param request-请求
     * @return 是否完成交易
     */
    @PatchMapping("/buy/{id}")
    @CheckAccountPermission({"trade:buyTrade"})
    public ResponseEntity<BaseResponse> buyTrade(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 buyTrade 接口 ");
        long timestamp = System.currentTimeMillis();
        //校验参数
        if (id.isEmpty()) {
            return ResultUtil.error(timestamp, "Path参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        //返回业务操作
        return tradeService.buyTrade(timestamp, request, id);
    }

    /**
     * 获取购买碳交易的列表
     *
     * @param request-请求
     * @return 响应
     */
    @GetMapping("/buy")
    @CheckAccountPermission({"trade:buy"})
    public ResponseEntity<BaseResponse> getBuyTradeList(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getBuyTradeList 接口");
        long timestamp = System.currentTimeMillis();
        // 返回业务操作
        return tradeService.getBuyTradeList(timestamp, request);
    }

    /**
     * 获取审核碳交易的列表
     * <hr/>
     *
     * @param request-请求
     * @return 响应
     */
    @GetMapping("/review")
    @CheckAccountPermission({"trade:review"})
    public ResponseEntity<BaseResponse> getReviewTradeList(
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getReviewTradeList 接口");
        long timestamp = System.currentTimeMillis();
        // 返回业务操作
        return tradeService.getReviewTradeList(timestamp, request);
    }

    /**
     * 获取此次碳交易中对方的开户行信息
     * <hr/>
     * 获取此次碳交易中对方开户行的信息，方便对方进行转账操作
     *
     * @param request-请求
     * @return 响应
     */
    @GetMapping("/bank")
    @CheckAccountPermission({"trade:getTradeBank"})
    public ResponseEntity<BaseResponse> getTradeBank(
            @RequestParam String tradeId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getTradeBank 接口");
        long timestamp = System.currentTimeMillis();
        // 检查代码是否只是数字
        if (!tradeId.matches("^[0-9]*$")) {
            return ResultUtil.error(timestamp, "参数 tradeId 错误", ErrorCode.PARAM_VARIABLE_ERROR);
        }
        // 返回业务操作
        return tradeService.getTradeBank(timestamp, request, tradeId);
    }

    @PutMapping("/check-success")
    @CheckAccountPermission({"trade:checkTradeSuccess"})
    public ResponseEntity<BaseResponse> checkTradeSuccess(
            @RequestParam String tradeId,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 checkTradeSuccess 接口");
        long timestamp = System.currentTimeMillis();
        // 检查代码是否只是数字
        if (!tradeId.matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "参数 tradeId 错误", ErrorCode.PARAM_VARIABLE_ERROR);
        }
        // 返回业务操作
        return tradeService.checkTradeSuccess(timestamp, request, tradeId);
    }
}
