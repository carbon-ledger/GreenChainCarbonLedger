package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  implements UserService {
    @Override
    public ResponseEntity<BaseResponse> getUserCurrent(long timestamp, HttpServletRequest request) {
        //用缓存的UUID与数据库UUID进行校对
        return null;
    }
}
