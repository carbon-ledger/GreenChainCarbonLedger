package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.AuthDAO;
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.dao.VerifyCodeDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserLoginDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackAuthLoginVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackLoginInfoVO;
import com.frontleaves.greenchaincarbonledger.services.AuthService;
import com.frontleaves.greenchaincarbonledger.services.MailService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final Gson gson;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final VerifyCodeDAO verifyCodeDAO;
    private final AuthDAO authDAO;
    private final MailService mailService;

    /**
     * 分析 UserAgent
     * <hr/>
     * 分析 UserAgent 获取设备类型，对设备进行区分展示给用户
     */
    private static String analyzeUserAgent(String userAgent) {
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        return ua.getOperatingSystem().getName();
    }

    @NotNull
    @Override
    @Transactional
    public ResponseEntity<BaseResponse> adminUserRegister(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthUserRegisterVO authUserRegisterVO) {
        log.info("[Service] 执行 adminUserRegister 方法");
        // 检查用户是否存在
        String checkUserExist = userDAO.checkUserExist(authUserRegisterVO.getUsername(), authUserRegisterVO.getEmail(), authUserRegisterVO.getPhone(), authUserRegisterVO.getRealname());
        if (checkUserExist != null) {
            return ResultUtil.error(timestamp, checkUserExist, ErrorCode.USER_EXISTED);
        }
        // 校验邮箱验证码
        if (mailService.checkMailCode(authUserRegisterVO.getEmail())) {
            // 保存用户
            UserDO newUserDO = new UserDO();
            // 获取默认 Role
            newUserDO.setUuid(ProcessingUtil.createUuid()).setUserName(authUserRegisterVO.getUsername()).setRealName(authUserRegisterVO.getRealname()).setEmail(authUserRegisterVO.getEmail()).setPhone(authUserRegisterVO.getPhone()).setRole(roleDAO.getRoleByName("admin").getUuid()).setPermission("[]").setPassword(ProcessingUtil.passwordEncrypt(authUserRegisterVO.getPassword()));
            if (userDAO.createUser(newUserDO)) {
                return ResultUtil.success(timestamp, "管理用户注册成功");
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.VERIFY_CODE_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userLogin(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthLoginVO authLoginVO) {
        log.info("[Service] 执行 userLogin 方法");
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
            // 检查用户数是否被封禁
            if (getUserDO.getBan()) {
                return ResultUtil.error(timestamp, ErrorCode.ACCOUNT_HAS_BEEN_BANNED);
            }
            // 检查用户是否已注销
            boolean recover = false;
            if (getUserDO.getDeletedAt() != null) {
                //用户存在并且处于注销状态，再进行判断是否在7天以内
                if (System.currentTimeMillis() - getUserDO.getDeletedAt().getTime() <= 604800000L) {
                    //用户存在并且在7天以内登录,取消注销状态
                    getUserDO.setDeletedAt(null);
                    recover = userDAO.accountDeleteCancel(getUserDO);
                } else {
                    //用户存在但是在7天之外登录
                    return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
                }
            }
            // 用户存在（密码检查）且不在注销状态
            if (ProcessingUtil.passwordCheck(authLoginVO.getPassword(), getUserDO.getPassword())) {
                String newToken = new JwtUtil(userDAO).signToken(getUserDO.getUuid());
                RoleDO getUserRole = roleDAO.getRoleByUuid(getUserDO.getRole());

                BackAuthLoginVO newBackAuthLoginVO = new BackAuthLoginVO();
                BackAuthLoginVO.UserVO newUserVO = new BackAuthLoginVO.UserVO();
                BackAuthLoginVO.PermissionVO newPermission = new BackAuthLoginVO.PermissionVO();
                BackAuthLoginVO.RoleVO newRole = new BackAuthLoginVO.RoleVO();
                newUserVO.setUuid(getUserDO.getUuid()).setUserName(getUserDO.getUserName()).setPhone(getUserDO.getPhone()).setEmail(getUserDO.getEmail()).setRealName(getUserDO.getRealName());
                newPermission.setUserPermission(gson.fromJson(getUserDO.getPermission(), new TypeToken<ArrayList<String>>() {
                }.getType())).setRolePermission(gson.fromJson(getUserRole.getPermission(), new TypeToken<ArrayList<String>>() {
                }.getType()));
                newRole.setName(getUserRole.getName()).setDisplayName(getUserRole.getDisplayName());
                newBackAuthLoginVO.setToken(newToken).setUser(newUserVO).setRole(newRole).setPermission(newPermission).setRecover(recover);
                //存入了getUserLoginDO类
                getUserLoginDO.setUuid(getUserDO.getUuid()).setToken(newToken).setUserAgent(request.getHeader("User-Agent")).setUserIp(request.getRemoteAddr());
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
    public ResponseEntity<BaseResponse> organizeRegister(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthOrganizeRegisterVO authOrganizeRegisterVO) {
        log.info("[Service] 执行 organizeRegister 方法");
        // 检索组织是否唯一存在
        String checkUserExist = userDAO.checkUserExist(authOrganizeRegisterVO.getUsername(), authOrganizeRegisterVO.getEmail(), authOrganizeRegisterVO.getPhone(), authOrganizeRegisterVO.getOrganize());
        if (checkUserExist != null) {
            return ResultUtil.error(timestamp, checkUserExist, ErrorCode.ORGANIZE_NOT_EXISTED);
        }
        // 验证邮箱验证码
        if (mailService.checkMailCode(authOrganizeRegisterVO.getEmail())) {
            String invite = authOrganizeRegisterVO.getInvite();
            // 验证组织注册填写的验证码是否有效
            if (invite != null && !invite.isEmpty()) {
                if (!userDAO.getUserByInvite(invite)) {
                    return ResultUtil.error(timestamp, ErrorCode.INVITE_CODE_ERROR);
                }
            }
            // 保存组织
            UserDO newUserDO = new UserDO();
            newUserDO.setUuid(ProcessingUtil.createUuid()).setRealName(authOrganizeRegisterVO.getOrganize()).setUserName(authOrganizeRegisterVO.getUsername()).setPhone(authOrganizeRegisterVO.getPhone()).setEmail(authOrganizeRegisterVO.getEmail()).setInvite(authOrganizeRegisterVO.getInvite()).setRole(roleDAO.getRoleByName("organize").getUuid()).setPermission("[]").setPassword(ProcessingUtil.passwordEncrypt(authOrganizeRegisterVO.getPassword()));
            if (userDAO.createUser(newUserDO)) {
                return ResultUtil.success(timestamp, "组织账户注册成功");
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.VERIFY_CODE_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> userChange(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthChangeVO authChangeVO) {
        log.info("[Service] 执行 userChange 方法");
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
        log.info("[Service] 执行 userDelete 方法");
        //获取用户UUID再将用户的UUID与数据库中的UUID进行校验，取出数据库中的实例
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        //进行邮箱验证码的判断，成功进行密码的校验，不成功则返回错误信息
        if (getUserDO != null) {
            if (!"console_user".equals(getUserDO.getUserName())) {
                if (verifyCodeDAO.getVerifyCodeByContact(getUserDO.getEmail()).getCode().equals(authDeleteVO.getCode())) {
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
            } else {
                return ResultUtil.error(timestamp, "超级管理员不可被操作", ErrorCode.USER_CANNOT_BE_OPERATE);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    @Transactional
    public ResponseEntity<BaseResponse> forgetCode(long timestamp, @NotNull HttpServletRequest request, @NotNull AuthForgetCodeVO authForgetCodeVO) {
        log.info("[Service] 执行 forgetCode 方法");
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
        log.info("[Service] 执行 userLogout 方法");
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

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getLoginIngo(long timestamp, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 getLoginIngo 方法");
        String getUserUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        ArrayList<UserLoginDO> getUserAuthorizeList = authDAO.getAuthorize(getUserUuid);
        log.debug(getUserAuthorizeList.toString());
        List<BackLoginInfoVO> backLoginInfoVOList = new ArrayList<>();
        // 去除为 null 部分
        getUserAuthorizeList.removeIf(Objects::isNull);
        getUserAuthorizeList.forEach(userLoginDO -> {
            // 获取登陆过期时间
            long getExpireTime = authDAO.getAuthorizeExpireTime(userLoginDO.getUuid());
            log.debug("[Service] 过期时间: {}", getExpireTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            // 对 UserAgent 进行分析出电脑设备以及其他浏览器信息
            BackLoginInfoVO backLoginInfoVO = new BackLoginInfoVO();
            backLoginInfoVO
                    .setUserIp(userLoginDO.getUserIp())
                    .setDeviceType(analyzeUserAgent(userLoginDO.getUserAgent()))
                    .setBrowserType(UserAgent.parseUserAgentString(userLoginDO.getUserAgent()).getBrowser().getName())
                    .setLoginTime(sdf.format(System.currentTimeMillis() + (getExpireTime * 1000L) - 3600000L))
                    .setExpireTime(sdf.format(System.currentTimeMillis() + (getExpireTime * 1000L)));
            backLoginInfoVOList.add(backLoginInfoVO);
        });
        return ResultUtil.success(timestamp, "用户登陆信息", backLoginInfoVOList);
    }
}

