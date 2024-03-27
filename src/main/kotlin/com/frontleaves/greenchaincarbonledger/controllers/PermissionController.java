package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.services.PermissionService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionController
 * <hr/>
 * 用于处理权限相关的请求, 包括获取权限列表等
 *
 * @author xiao_lfeng AND DC_DC AND FLASHLACK
 * @version v1.0.0
 * @see com.frontleaves.greenchaincarbonledger.services.PermissionService
 * @since v1.0.0-SNAPSHOT
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
     * @param request HTTP 请求对象
     * @return 包含权限列表的响应实体
     */
    @GetMapping("/list")
    @CheckAccountPermission({"permission:getPermissionList"})
    public ResponseEntity<BaseResponse> getPermissionList(
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getPermissionList 接口");
        long timestamp = System.currentTimeMillis();
        //返回业务操作
        return permissionService.getPermissionList(timestamp, request);
    }
}
