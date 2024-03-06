package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.services.RoleService;
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
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getCurrentRole (HttpServletRequest request){
        log.info("[Controller] 请求 getCurrentRole 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return roleService.getUserCurrent(timestamp, request);
    }
}
