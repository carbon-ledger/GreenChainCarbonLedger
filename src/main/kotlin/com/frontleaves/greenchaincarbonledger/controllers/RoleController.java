package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO;
import com.frontleaves.greenchaincarbonledger.services.RoleService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
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
 * RoleController
 * <hr/>
 * 用于处理角色相关的请求, 包括获取、添加、编辑、删除等
 *
 * @author DC_DC
 * @version v1.0.0
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse> addRole(@RequestBody @Validated RoleVO roleVO, @NotNull BindingResult bindingResult, HttpServletRequest request) {
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.USER_ACCESS_ILLEGAL);
        }
        //此处是否需要对角色名、展示名字、权限进行验证
        return roleService.addRole(timestamp, request, roleVO);
    }

    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getCurrentRole(HttpServletRequest request) {
        log.info("[Controller] 请求 getCurrentRole 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return roleService.getUserCurrent(timestamp, request);
    }

    @PutMapping("/edit")
    public ResponseEntity<BaseResponse> editRole(@RequestBody @Validated RoleVO roleVO, @NotNull BindingResult bindingResult, HttpServletRequest request){
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.USER_ACCESS_ILLEGAL);
        }
        //此处是否需要对角色名、展示名字、权限进行验证
        return roleService.editRole(timestamp, request, roleVO);
    }
}
