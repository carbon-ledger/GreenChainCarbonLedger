package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthLoginVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthOrganizeRegisterVO;
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
 * @since v1.0.0
 * @version v1.0.0
 * @author xiao_lfeng
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/admin/register")
    public ResponseEntity<BaseResponse> adminUserRegister(
            @RequestBody @Validated AuthOrganizeRegisterVO authOrganizeRegisterVO,
            HttpServletRequest request,
            @NotNull BindingResult bindingResult
    ) {
        log.info("[Controller] 请求 adminUserRegister 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.adminUserRegister(timestamp, request, authOrganizeRegisterVO);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> userLogin(
        @RequestBody @Validated AuthLoginVO authLoginVO,
        HttpServletRequest request,
        @NotNull BindingResult bindingResult
    ) {
        log.info("[Controller] 请求 adminUserRegister 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return authService.userLogin(timestamp, request, authLoginVO);
    }
}
