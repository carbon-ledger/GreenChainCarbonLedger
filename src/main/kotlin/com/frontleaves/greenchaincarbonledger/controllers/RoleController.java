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

    /**
     * addRole
     * <hr/>
     * 用于添加角色，管理员可以通过该接口添加角色
     *
     * @param roleVO        添加角色的请求参数
     * @param bindingResult 请求参数的校验结果
     * @param request       请求对象
     * @return 返回添加角色的结果
     */
    @PostMapping("/add")
    @CheckAccountPermission({"role:addRole"})
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
     * <hr/>
     * 该接口提供当前登录用户的角色信息查询。用户在成功登录后，可以请求此接口来获取自己的角色信息，
     * 包括角色名称、角色权限等。这通常用于个人资料页面，允许用户查看其角色信息。
     *
     * @param request HTTP 请求对象
     * @return 包含角色信息的响应实体
     */
    @GetMapping("/current")
    @CheckAccountPermission({"role:getCurrentRole"})
    public ResponseEntity<BaseResponse> getCurrentRole(@NotNull HttpServletRequest request) {
        log.info("[Controller] 请求 getCurrentRole 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return roleService.getUserCurrent(timestamp, request);
    }

    /**
     * 获取角色列表
     * <hr/>
     * 该接口提供角色列表的查询功能。用户在成功登录后，可以请求此接口来获取角色列表，
     * 包括角色名称、角色描述、角色代码等。这通常用于角色管理页面，允许用户查看角色列表。
     *
     * @param type    查询类型
     * @param search  查询关键字
     * @param limit   限制返回的角色数量
     * @param page    页码
     * @param order   排序方式
     * @param request HTTP 请求对象
     * @return 包含角色列表的响应实体
     */
    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getRoleList(
            //需要Query参数
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            @NotNull HttpServletRequest request) {
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

    /**
     * 编辑角色
     * <hr/>
     * 用于编辑角色，管理员可以通过该接口编辑角色
     *
     * @param roleVO        编辑角色的请求参数
     * @param bindingResult 请求参数的校验结果
     * @param roleUuid      角色的uuid
     * @param request       请求对象
     * @return 返回编辑角色的结果
     */
    @PutMapping("/edit/{uuid}")
    @CheckAccountPermission({"role:editRole"})
    public ResponseEntity<BaseResponse> editRole(
            @RequestBody @Validated RoleVO roleVO,
            @NotNull BindingResult bindingResult,
            @PathVariable("uuid") String roleUuid,
            @NotNull HttpServletRequest request
    ) {
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

    /**
     * 删除角色
     * <hr/>
     * 用于删除角色，管理员可以通过该接口删除角色
     *
     * @param roleUuid 角色的uuid
     * @param request  请求对象
     * @return 返回删除角色的结果
     */
    @DeleteMapping("/delete/{uuid}")
    @CheckAccountPermission({"role:deleteRole"})
    public ResponseEntity<BaseResponse> deleteRole(@PathVariable("uuid") String roleUuid, HttpServletRequest request) {
        request.getHeader("X-Auth-UUID");
        log.info("[Controller] 请求 roleService 接口");
        long timestamp = System.currentTimeMillis();
        return roleService.deleteRole(timestamp, request, roleUuid);
    }
}
