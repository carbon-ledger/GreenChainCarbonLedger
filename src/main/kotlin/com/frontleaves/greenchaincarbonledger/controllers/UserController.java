package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 获取当前登录用户的个人信息。
     * <p>
     * 该接口提供当前登录用户的个人信息查询。用户在成功登录后，可以请求此接口来获取自己的账户详细信息，
     * 包括姓名、联系方式、电子邮件地址等。这通常用于个人资料页面，允许用户查看其信息。
     *
     * @param request HTTP 请求对象
     * @return 包含用户信息的响应实体
     */
    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getUserCurrent(HttpServletRequest request) {
        {
            log.info("[Controller] 请求 getUserCurrent 接口");
            long timestamp = System.currentTimeMillis();
            request.getHeader("X-Auth-UUID");
            // 业务操作
            return userService.getUserCurrent(timestamp, request);
        }
    }
}

