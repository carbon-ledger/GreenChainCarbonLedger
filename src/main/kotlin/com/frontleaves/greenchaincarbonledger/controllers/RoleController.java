package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO;
import com.frontleaves.greenchaincarbonledger.services.RoleService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * RoleController
 * <hr/>
 * 用于处理用户角色相关的请求, 包括获取当前用户角色等
 *
 * @author xiao_lfeng AND DC_DC AND FLASHLACK
 * @version v1.0.0-SNAPSHOT
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
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
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
        if ("all".equals(type) || "search".equals(type) || "user".equals(type) || "permission".equals(type)) {
            //业务操作
            return roleService.getRoleList(timestamp, request, type, search, limit, page, order);
        } else {
            return ResultUtil.error(timestamp, "type 参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }

    }

    @PutMapping("/edit/{uuid}")
    public ResponseEntity<BaseResponse> editRole(
            @RequestBody @Validated RoleVO roleVO,
            @NotNull BindingResult bindingResult,
            @PathVariable("uuid") String roleUuid,
            @NotNull HttpServletRequest request) {
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
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
