package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.*;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
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

/**
 * AuthController
 * <hr/>
 * 用于处理用户认证相关的请求, 包括登录, 注册, 验证码等
 *
 * @author xiao_lfeng-SNAPSHOT
 * @version v1.0.0
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 管理用户注册
     * <hr/>
     * 用于管理员注册用户, 管理员可以通过该接口进行注册，注册用户权限为管理用户（但是授予管理员用户权限需要进行审核）
     *
     * @param authUserRegisterVO 注册获取的信息
     * @param bindingResult      结果
     * @param request            请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @PostMapping("/admin/register")
    public ResponseEntity<BaseResponse> adminUserRegister(
            @RequestBody @Validated AuthUserRegisterVO authUserRegisterVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 adminUserRegister 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.adminUserRegister(timestamp, request, authUserRegisterVO);
    }

    /**
     * 用户登录
     * <hr/>
     * 用于用户登录, 用户可以通过该接口进行登录
     *
     * @param authLoginVO   登录获取的信息
     * @param bindingResult 结果
     * @param request       请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> userLogin(
            @RequestBody @Validated AuthLoginVO authLoginVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 userLogin 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.userLogin(timestamp, request, authLoginVO);
    }

    /**
     * 用户注册
     * <hr/>
     * 用于用户注册, 用户可以通过该接口进行注册
     *
     * @param authChangeVO  注册获取的信息
     * @param bindingResult 结果
     * @param request       请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @PatchMapping("/change")
    @CheckAccountPermission({"auth:userChangePassword"})
    public ResponseEntity<BaseResponse> userChangePassword(
            @RequestBody @Validated AuthChangeVO authChangeVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request

    ) {
        log.info("[Controller] 请求 userChangePassword 接口");
        long timestamp = System.currentTimeMillis();
        //请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        //业务操作
        return authService.userChange(timestamp, request, authChangeVO);
    }

    /**
     * 用户删除
     * <hr/>
     * 用于用户删除, 用户可以通过该接口进行删除
     *
     * @param authDeleteVO  删除获取的信息
     * @param bindingResult 结果
     * @param request       请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @DeleteMapping("/delete")
    @CheckAccountPermission({"auth:userDelete"})
    public ResponseEntity<BaseResponse> userDelete(
            @RequestBody @Validated AuthDeleteVO authDeleteVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 userDelete 接口");
        long timestamp = System.currentTimeMillis();
        //请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        //业务操作
        return authService.userDelete(timestamp, request, authDeleteVO);
    }


    /**
     * 用户注册
     * <hr/>
     * 用于用户注册, 用户可以通过该接口进行注册
     *
     * @param authOrganizeRegisterVO 注册获取的信息
     * @param bindingResult          结果
     * @param request                请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @PostMapping("/organize/register")
    public ResponseEntity<BaseResponse> organizeRegister(
            @RequestBody @Validated AuthOrganizeRegisterVO authOrganizeRegisterVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 organizeRegister 接口");
        long timestamp = System.currentTimeMillis();
        // 对传入参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.organizeRegister(timestamp, request, authOrganizeRegisterVO);
    }

    /**
     * 忘记密码
     * <hr/>
     * 用于用户忘记密码, 用户可以通过该接口进行忘记密码
     *
     * @param authForgetCodeVO 忘记密码获取的信息
     * @param bindingResult    结果
     * @param request          请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @PutMapping("/forget")
    public ResponseEntity<BaseResponse> forgetCode(
            @RequestBody @Validated AuthForgetCodeVO authForgetCodeVO,
            @NotNull BindingResult bindingResult,
            HttpServletRequest request
    ) {
        log.info("[Controller] 请求 forgetCode 接口");
        long timestamp = System.currentTimeMillis();
        // 对传入参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.forgetCode(timestamp, request, authForgetCodeVO);
    }

    /**
     * 用户登出
     * <hr/>
     * 用于用户登出, 用户可以通过该接口进行登出
     *
     * @param request 请求
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @GetMapping("/logout")
    @CheckAccountPermission({"auth:userLogout"})
    public ResponseEntity<BaseResponse> userLogout(@NotNull HttpServletRequest request) {
        log.info("[Controller] 请求 userLogout接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return authService.userLogout(timestamp, request);
    }

    /**
     * 获取登录信息
     * <hr/>
     * 用于获取登录信息, 用户可以通过该接口获取登录信息, 登陆多少设备
     *
     * @return 成功则去业务层操作，失败则返回错误信息
     */
    @GetMapping("/login-info")
    public ResponseEntity<BaseResponse> getLoginInfo(
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 请求 getLoginInfo 接口");
        long timestamp = System.currentTimeMillis();
        // 业务操作
        return authService.getLoginIngo(timestamp, request);
    }
}
