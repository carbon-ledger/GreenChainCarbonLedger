package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
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
 * 用于处理用户角色相关的请求, 包括获取当前用户角色等
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng AND DC_DC AND FLASHLACK
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


    /**
     * 获取当前登录用户的角色信息。
     * <p>
     * 该接口提供当前登录用户的角色信息查询。用户在成功登录后，可以请求此接口来获取自己的角色信息，
     * 包括角色名称、角色权限等。这通常用于个人资料页面，允许用户查看其角色信息。
     *
     * @param request HTTP 请求对象
     * @return 包含角色信息的响应实体
     */
    @GetMapping("/current")
    @CheckAccountPermission({"role:getCurrentRole"})
    public ResponseEntity<BaseResponse> getCurrentRole (@NotNull HttpServletRequest request){
        log.info("[Controller] 请求 getCurrentRole 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return roleService.getUserCurrent(timestamp, request);
    }

    @PutMapping("/edit/{uuid}")
    public ResponseEntity<BaseResponse> editRole(@RequestBody @Validated RoleVO roleVO, @NotNull BindingResult bindingResult, @PathVariable("uuid") String roleUuid, HttpServletRequest request) {
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.USER_ACCESS_ILLEGAL);
        }
        //此处是否需要对角色名、展示名字、权限进行验证
        return roleService.editRole(timestamp, request, roleVO, roleUuid);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<BaseResponse> deleteRole(@PathVariable("uuid") String roleUuid, HttpServletRequest request){
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        return roleService.deleteRole(timestamp, request, roleUuid);
    }
}
