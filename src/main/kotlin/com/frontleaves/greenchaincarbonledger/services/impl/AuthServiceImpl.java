package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.AuthDAO;
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.dao.VerifyCodeDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserLoginDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackAuthLoginVO;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final VerifyCodeDAO verifyCodeDAO;
    private final AuthDAO authDAO;

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
        // 获取默认 Role
        newUserDO
                .setUuid(ProcessingUtil.createUuid())
                .setUserName(authUserRegisterVO.getUsername())
                .setRealName(authUserRegisterVO.getRealname())
                .setEmail(authUserRegisterVO.getEmail())
                .setPhone(authUserRegisterVO.getPhone())
                .setRole(roleDAO.getRoleByName("admin").getUuid())
                .setPassword(ProcessingUtil.passwordEncrypt(authUserRegisterVO.getPassword()));
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
        UserLoginDO getUserLoginDO = new UserLoginDO();
        if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", authLoginVO.getUser())) {
            getUserDO = userDAO.getUserByEmail(authLoginVO.getUser());
        } else if (Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", authLoginVO.getUser())) {
            getUserDO = userDAO.getUserByPhone(authLoginVO.getUser());
        } else {
            getUserDO = userDAO.getUserByUsername(authLoginVO.getUser());
        }
        // 检查用户是否存在
        if (getUserDO != null) {
            String newToken = new JwtUtil(userDAO).signToken(getUserDO.getUuid());
            boolean recover = false;
            if (getUserDO.getDeletedAt() != null) {
                //用户存在并且处于注销状态，再进行判断是否在7天以内
                if (System.currentTimeMillis() - getUserDO.getDeletedAt().getTime() <= 604800000L) {
                    //用户存在并且在7天以内登录,取消注销状态
                    getUserDO.setDeletedAt(null);
                    recover = userDAO.userAccountDistanceDeletion(getUserDO);
                } else {
                    //用户存在但是在7天之外登录
                    return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
                }
            }
            // 用户存在（密码检查）且不在注销状态
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
                        .setToken(newToken)
                        .setUser(newUserVO)
                        .setPermission(newPermission)
                        .setRecover(recover);
                //存入了getUserLoginDO类
                getUserLoginDO
                        .setUuid(getUserDO.getUuid())
                        .setToken(newToken)
                        .setUserAgent(request.getHeader("User-Agent"))
                        .setUserIp(request.getRemoteAddr());
                //将信息转为缓存
                authDAO.saveAuthInfo(getUserLoginDO);
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
    public ResponseEntity<BaseResponse> organizeRegister(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull AuthOrganizeRegisterVO authOrganizeRegisterVO
    ) {
        // 检索组织是否唯一存在
        String checkUserExist = userDAO.checkUserExist(authOrganizeRegisterVO.getUsername(), authOrganizeRegisterVO.getEmail(), authOrganizeRegisterVO.getPhone(), authOrganizeRegisterVO.getOrganize());
        if (checkUserExist != null) {
            return ResultUtil.error(timestamp, checkUserExist, ErrorCode.ORGANIZE_NOT_EXISTED);
        }
        String invite = authOrganizeRegisterVO.getInvite();
        // 验证组织注册填写的验证码是否有效
        if (!userDAO.getUserByInvite(invite)) {
            return ResultUtil.error(timestamp, ErrorCode.INVITE_CODE_ERROR);
        } else {
            // 密码加密
            String newPassword = ProcessingUtil.passwordEncrypt(authOrganizeRegisterVO.getPassword());
            // 保存组织
            UserDO newUserDO = new UserDO();
            newUserDO
                    .setUuid(ProcessingUtil.createUuid())
                    .setRealName(authOrganizeRegisterVO.getOrganize())
                    .setUserName(authOrganizeRegisterVO.getUsername())
                    .setPhone(authOrganizeRegisterVO.getPhone())
                    .setEmail(authOrganizeRegisterVO.getEmail())
                    .setInvite(authOrganizeRegisterVO.getInvite())
                    .setPassword(newPassword);
            if (userDAO.createUser(newUserDO)) {
                return ResultUtil.success(timestamp, "组织账户注册成功");
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
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
        if (getUserDO != null && verifyCodeDAO.getVerifyCodeByContact(getUserDO.getEmail()).getCode().equals(authDeleteVO.getCode())) {
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

    @NotNull
    @Override
    @Transactional
    public ResponseEntity<BaseResponse> forgetCode(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull AuthForgetCodeVO authForgetCodeVO) {
        // 获取邮箱数据，此处无论用户填入的Email是否真实有效，系统都会返回“密码充值邮件已发送”的信息
        String email = authForgetCodeVO.getEmail();
        // 如果校验码与缓存中的数据符合，则满足修改密码的条件
        if (verifyCodeDAO.getVerifyCodeByContact(email) != null) {
            if (authForgetCodeVO.getCode().equals(verifyCodeDAO.getVerifyCodeByContact(email).code)) {
                // 删除缓存
                verifyCodeDAO.deleteVerifyCode(authForgetCodeVO.getEmail());
                // 验证密码和确认密码是否相同
                if (authForgetCodeVO.getPassword().equals(authForgetCodeVO.getConfirmPassword())) {
                    // 先对密码进行加密再将新密码存入数据库
                    UserDO userDO = new UserDO();
                    userDO.setPassword(ProcessingUtil.passwordEncrypt(authForgetCodeVO.getPassword()));
                    userDAO.updateUserPassword(userDO);
                    return ResultUtil.success(timestamp, "密码更新完毕");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.USER_PASSWORD_INCONSISTENCY_ERROR);
                }
            } else {
                // 不满足修改密码的条件
                return ResultUtil.error(timestamp, ErrorCode.VERIFY_CODE_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.VERIFY_CODE_NOT_EXISTED);
        }

    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userLogout(long timestamp, @NotNull HttpServletRequest request) {
        // 首先获取此时用户的 UUID
        String getUuid = request.getHeader("X-Auth-UUID");
        // 获取用户本地的 Token
        String getToken = request.getHeader("Authorization");
        // 需要删除 Token 里面的 "Bearer " 前缀
        if (getToken != null && getToken.startsWith("Bearer ")) {
            getToken = getToken.substring(7);
        }
        //转到AuthDAO操作
        authDAO.userLogout(getUuid, getToken);
        return ResultUtil.success(timestamp, "用户成功登出");
    }
}

