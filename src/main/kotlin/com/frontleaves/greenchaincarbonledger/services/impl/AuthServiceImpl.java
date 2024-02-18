package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 * <hr/>
 * 用于用户的认证服务
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.AuthService
 * @since v1.0.0-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserDAO userDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userLogin(@NotNull HttpServletRequest request, @NotNull String user, @NotNull String password) {
        Long timestamp = System.currentTimeMillis();
        return ResultUtil.success(timestamp);
    }
}
