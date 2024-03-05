package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 用于获取用户登录信息（
 * <hr/>
 * 用于获取用户登录信息如（查询当前用户信息）
 * @author FLASHLACK AND DC_DC
 */
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
        log.info("[Controller] 请求 getUserCurrent 接口");
        long timestamp = System.currentTimeMillis();
        request.getHeader("X-Auth-UUID");
        // 业务操作
        return userService.getUserCurrent(timestamp, request);
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getUserList(
            // 此处根据用户的操作需求自动传入对应参数，除了type都是可选项
            @RequestParam String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            HttpServletRequest request
    ){
        log.info("[Controller] 请求getUserList 接口");
        long timestamp = System.currentTimeMillis();
        if (limit != null && !limit.toString().matches("^[0-9]+$")) {
            return ResultUtil.error(timestamp, "limit 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        if (page != null && page.toString().matches("^[0-9]+$")){
            return ResultUtil.error(timestamp, "page 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("DESC");
        list.add("desc");
        list.add("ASC");
        list.add("asd");
        if (order != null && list.contains(order)){
            return ResultUtil.error(timestamp, "order 参数错误", ErrorCode.QUERY_PARAM_ERROR);
        }
        // 业务操作
        return userService.getUserList(timestamp, request, type, search , limit, page, order);
    }
}

