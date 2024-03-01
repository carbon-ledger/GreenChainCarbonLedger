package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.dao.VerifyCodeDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthChangeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthDeleteVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthLoginVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AuthUserRegisterVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackAuthLoginVO;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil;
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
    private final VerifyCodeDAO verifyCodeDAO;

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
                        .setToken(new JwtUtil(userDAO).signToken(getUserDO.getUuid()))
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
        //获取用户的UUID再将用户的UUID与数据库中的UUID进行校验，取出数据库的实例
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        //用户输入的当前密码和数据库中密码进行校验
        if (ProcessingUtil.passwordCheck(authChangeVO.getCurrentPassword(), getUserDO.getPassword())) {
            //当前密码与新密码进行检查，重复则提示报错，不重复则继续进行,下一步在else里面继续进行验证
            if (authChangeVO.getCurrentPassword().equals(authChangeVO.getNewPassword())) {
                return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_REPEAT_ERROR);
            } else {
                //新密码更新到数据库中
                getUserDO.setPassword(ProcessingUtil.passwordEncrypt(authChangeVO.getNewPassword()));
                if (userDAO.updateUserPassword(getUserDO)) {
                    // TODO: 还没有加上邮箱发送提醒
                    return ResultUtil.success(timestamp, "密码更新完毕");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_CURRENT_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userDelete(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthDeleteVO authDeleteVO) {
        //获取用户UUID再将用户的UUID与数据库中的UUID进行校验，取出数据库中的实例
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        //进行邮箱验证码的判断，成功进行密码的校验，不成功则返回错误信息
        if (getUserDO != null &&verifyCodeDAO.getVerifyCodeByContact(getUserDO.getEmail()).getCode().equals(authDeleteVO.getCode())) {
            //进行密码的校验,成功进行软删除
            if (ProcessingUtil.passwordCheck(authDeleteVO.getPassword(), getUserDO.getPassword())) {
                // 邮箱验证码和密码验证成功，进行软删除
                boolean deletionResult = userDAO.userAccountDeletion(getUserDO);
                if (deletionResult) {
                    return ResultUtil.success(timestamp, "账号注销成功（账号注销缓冲期为7天）");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }

            } else {
                return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_CURRENT_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.VERIFY_CODE_ERROR);
        }

    }
}

