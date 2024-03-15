package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/carbon")
@RequiredArgsConstructor
public class CarbonController {
    private final CarbonService carbonService;
    /**
     * 获取碳排放报告
     * @param type 报告类型 [all/search/draft/pending_review/approved/rejected]
     * @param search 搜索关键字
     * @param limit 单页限制个数（默认10个）[不可超过50]
     * @param page 第几页
     * @param order 排序顺序 [asc/desc]
     * @param request HTTP 请求对象
     * @return ResponseEntity<BaseResponse> 响应实体
     */
    @GetMapping("/report/get")
    public ResponseEntity<BaseResponse> getCarbonReport(
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getCarbonReport 接口");
        long timestamp = System.currentTimeMillis();
        if (limit != null && !limit.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "limit 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        if (page != null && !page.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "page 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        request.getHeader("X-Auth-UUID");
        ArrayList<String> list = new ArrayList<>();
        list.add("desc");
        list.add("asc");
        if (order != null && !list.contains(order)) {
            return ResultUtil.error(timestamp, "order 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        if ("all".equals(type) || "search".equals(type) || "draft".equals(type) || "pending_review".equals(type) || "approved".equals(type) || "rejected".equals(type)) {
            // 返回业务操作
            return carbonService.getCarbonReport(timestamp, request, type, search, limit, page, order);
        } else {
            return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
    }

}
