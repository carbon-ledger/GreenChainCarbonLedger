package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthChangeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthLoginVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthUserRegisterVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackAuthLoginVO;
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

import java.util.ArrayList;
import java.util.regex.Pattern;

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
    @Transactional
    public ResponseEntity<BaseResponse> adminUserRegister(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthUserRegisterVO authUserRegisterVO) {
        // 检查用户是否存在
        String checkUserExist = userDAO.checkUserExist(authUserRegisterVO.getUsername(), authUserRegisterVO.getEmail(), authUserRegisterVO.getPhone(), authUserRegisterVO.getRealname());
        if (checkUserExist != null) {
            return ResultUtil.error(timestamp, checkUserExist, ErrorCode.USER_NOT_EXISTED);
        }
        // 保存用户
        UserDO newUserDO = new UserDO();
        newUserDO
                .setUuid(ProcessingUtil.createUuid())
                .setUserName(authUserRegisterVO.getUsername())
                .setRealName(authUserRegisterVO.getRealname())
                .setEmail(authUserRegisterVO.getEmail())
                .setPhone(authUserRegisterVO.getPhone())
                .setPassword(ProcessingUtil.passwordEncrypt(authUserRegisterVO.getPassword()))
                .setRole((short) 2);
        if (userDAO.createUser(newUserDO)) {
            return ResultUtil.success(timestamp, "管理用户注册成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userLogin(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthLoginVO authLoginVO) {
        // 检索用户
        UserDO getUserDO;
        if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", authLoginVO.getUser())) {
            getUserDO = userDAO.getUserByEmail(authLoginVO.getUser());
        } else if (Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", authLoginVO.getUser())) {
            getUserDO = userDAO.getUserByPhone(authLoginVO.getUser());
        } else {
            getUserDO = userDAO.getUserByUsername(authLoginVO.getUser());
        }
        // 检查用户是否存在
        if (getUserDO != null) {
            // 用户存在（密码检查）
            if (ProcessingUtil.passwordCheck(authLoginVO.getPassword(), getUserDO.getPassword())) {
                BackAuthLoginVO newBackAuthLoginVO = new BackAuthLoginVO();
                BackAuthLoginVO.UserVO newUserVO = new BackAuthLoginVO.UserVO();
                BackAuthLoginVO.PermissionVO newPermission = new BackAuthLoginVO.PermissionVO();
                newUserVO
                        .setUuid(getUserDO.getUuid())
                        .setUserName(getUserDO.getUuid())
                        .setPhone(getUserDO.getPhone())
                        .setEmail(newUserVO.getEmail())
                        .setRealName(getUserDO.getRealName());
                newPermission
                        .setUserPermission(new ArrayList<>())
                        .setRolePermission(new ArrayList<>());
                newBackAuthLoginVO
                        .setUser(newUserVO)
                        .setPermission(newPermission);
                return ResultUtil.success(timestamp, "登录成功", newBackAuthLoginVO);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userChange(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthChangeVO authChangeVO) {
        //首先进行用户定义新密码的格式判断
        if (Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", authChangeVO.getNewPassword())) {
            //获取用户的UUID再将用户的UUID与数据库中的UUID进行校验，取出数据库的实例
            String getUuid = request.getHeader("X-Auth-UUID");
            UserDO getUserDO = userDAO.getUserByUuid(getUuid);
            //用户输入的当前密码和数据库中密码进行校验
            if (ProcessingUtil.passwordCheck(authChangeVO.getCurrentPassword(), getUserDO.getPassword())) {
                //当前密码与新密码进行检查，重复则提示报错，不重复则继续进行,下一步在else里面继续进行验证
                if (authChangeVO.getCurrentPassword().equals(authChangeVO.getNewPassword())) {
                    return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_REPEAT_ERROR);
                } else {
                    //将用户输入的重复新密码进行检查
                    if (authChangeVO.getNewPassword().equals(authChangeVO.getNewPasswordConfirm())) {
                        //新密码更新到数据库中
                        getUserDO.setPassword(ProcessingUtil.passwordEncrypt(authChangeVO.getNewPassword()));
                        userDAO.updateUser(getUserDO);
                        return ResultUtil.success(timestamp, "密码更新完毕");
                    } else {
                        return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_INCONSISTENCY_ERROR);
                    }
                }

            } else {
                return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_CURRENT_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_DEFINITION_ERROR);
        }
    }
}
