package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.services.RoleService;
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
 * TODO：请加上作者注释
 */

@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getCurrentRole(HttpServletRequest request) {
        log.info("[Controller] 请求 getCurrentRole 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return roleService.getUserCurrent(timestamp, request);
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getRoleList(
            //需要Query参数
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            HttpServletRequest request) {
        log.info("[Controller] 请求 getRoleList 接口");
        long timestamp = System.currentTimeMillis();
        if (limit != null && !limit.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "limit 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        if (page != null && !page.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "page 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        request.getDateHeader("X-Auth-UUID");
        ArrayList<String> list = new ArrayList<>();
        list.add("DESC");
        list.add("desc");
        list.add("ASC");
        list.add("asd");
        if (order != null && list.contains(order)) {
            return ResultUtil.error(timestamp, "order 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        if ("all".equals(type) || "search".equals(type) || "user".equals(type) || "permission".equals(type)) {
            //业务操作
            return roleService.getRoleList(timestamp, request, type, search, limit, page, order);
        } else {
            return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }

    }
}
