package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.PermissionService;
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
 * PermissionController
 * <hr/>
 * 用于处理权限相关的请求, 包括获取权限列表等
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0
 * @see com.frontleaves.greenchaincarbonledger.services.PermissionService
 * @author xiao_lfeng AND DC_DC AND FLASHLACK
 */
@Slf4j
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 获取权限列表
     * <hr/>
     * 该接口提供权限列表的查询功能。用户在成功登录后，可以请求此接口来获取权限列表，
     * 包括权限名称、权限描述、权限代码等。这通常用于权限管理页面，允许用户查看权限列表。
     *
     * @param limit 限制返回的权限数量
     * @param page 页码
     * @param order 排序方式
     * @param request HTTP 请求对象
     * @return 包含权限列表的响应实体
     */
    @GetMapping("/list")
    @CheckAccountPermission({"permission:getPermissionList"})
    public ResponseEntity<BaseResponse> getPermissionList(
            //Query参数
            @RequestParam(required = false)Integer limit,
            @RequestParam(required = false)Integer page,
            @RequestParam(required = false)String order,
            HttpServletRequest request) {
        log.info("[Controller] 请求 getPermissionList 接口");
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
        //返回业务操作
        return permissionService.getPermissionList(timestamp, limit, page, order);
    }
}
