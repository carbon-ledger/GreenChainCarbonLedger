package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AdminUserChangeVO;
import com.frontleaves.greenchaincarbonledger.services.AdminService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminController
 * <hr/>
 * 用于处理管理员相关的请求, 包括管理员注册, 管理员登录, 管理员信息修改等
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.AdminService
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    /**
     * resetUserPassword
     * <hr/>
     * 用于重置用户密码，管理员可以通过该接口重置用户的密码
     *
     * @param adminUserChangeVO 重置用户密码的请求参数
     * @param bindingResult     请求参数的校验结果
     * @param request           请求对象
     * @return 返回重置用户密码的结果
     */
    @PatchMapping("/user/reset/password")
    @CheckAccountPermission({"admin:resetUserPassword"})
    public ResponseEntity<BaseResponse> resetUserPassword(@RequestBody @Validated AdminUserChangeVO adminUserChangeVO, @NotNull BindingResult bindingResult, HttpServletRequest request) {
        log.info("[Controller] 请求 resetUserPassword 接口");
        long timestamp = System.currentTimeMillis();
        // 对请求参数进行校验
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务操作
        return adminService.resetUserPassword(timestamp, request, adminUserChangeVO);
    }
}
