package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthOrganizeRegisterVO;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @NotNull
    @Override
    @Transactional
    public ResponseEntity<BaseResponse> adminUserRegister(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthOrganizeRegisterVO authOrganizeRegisterVO) {
        // 检查用户是否存在
        String checkUserExist = userDAO.checkUserExist(authOrganizeRegisterVO.getUsername(), authOrganizeRegisterVO.getEmail(), authOrganizeRegisterVO.getPhone(), authOrganizeRegisterVO.getRealname());
        if (checkUserExist != null) {
            return ResultUtil.error(timestamp, checkUserExist, ErrorCode.USER_NOT_EXISTED);
        }
        // 保存用户
        UserDO newUserDO = new UserDO();
        newUserDO
                .setUuid(ProcessingUtil.createUuid())
                .setUserName(authOrganizeRegisterVO.getUsername())
                .setRealName(authOrganizeRegisterVO.getRealname())
                .setEmail(authOrganizeRegisterVO.getEmail())
                .setPhone(authOrganizeRegisterVO.getPhone())
                .setPassword(ProcessingUtil.passwordEncrypt(authOrganizeRegisterVO.getPassword()))
                .setRole((short) 2);
        if (userDAO.createUser(newUserDO)) {
            return ResultUtil.success(timestamp, "管理用户注册成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }
}
