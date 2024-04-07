package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.dao.VerifyCodeDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.doData.VerifyCodeDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserAddVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserEditVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserForceEditVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.*;
import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.frontleaves.greenchaincarbonledger.utils.redis.ContactCodeRedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类，提供处理用户相关业务逻辑的方法。
 * <hr/>
 * 该服务实现类包括获取当前登录用户的个人信息等功能。用户在成功登录后，可以通过调用相应的方法来获取自己的账户详细信息，
 * 包括姓名、联系方式、电子邮件地址等。通常用于个人资料页面，允许用户查看其信息。
 *
 * @author FLASHALCK
 * @version 1.0
 * @since 2024-3-1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final VerifyCodeDAO verifyCodeDAO;
    private final ContactCodeRedis contactCodeRedis;
    private final ModelMapper modelMapper;
    private final Gson gson;

    /**
     * 获取封禁用户的响应实体
     * <hr/>
     * 用于获取封禁用户的响应实体
     *
     * @param timestamp   时间戳
     * @param banUserUuid 被封禁用户的UUID
     * @param userDAO     用户DAO
     * @return {@link ResponseEntity<BaseResponse>}
     * @since v1.0.0
     */
    @NotNull
    private static ResponseEntity<BaseResponse> getBaseResponseResponseEntity(
            long timestamp,
            @NotNull String banUserUuid,
            @NotNull UserDAO userDAO
    ) {
        UserDO getBanUser = userDAO.getUserByUuid(banUserUuid);
        if (getBanUser != null) {
            if (!getBanUser.getBan()) {
                if (userDAO.banUser(banUserUuid)) {
                    return ResultUtil.success(timestamp, "用户封禁成功");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "用户已经被封禁", ErrorCode.USER_CANNOT_BE_OPERATE);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getUserCurrent(long timestamp, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 getUserCurrent 方法");
        //用缓存的UUID与数据库UUID进行校对
        UserDO getUserDO = userDAO.getUserByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        //获取自己的账号详细信息（姓名，联系方式，电子邮件等）
        if (getUserDO != null) {
            RoleDO getRoleDO = roleDAO.getRoleByUuid(getUserDO.getRole());
            // 数据整理
            BackUserCurrentVO backUserCurrent = new BackUserCurrentVO();
            BackUserCurrentVO.UserVO newUserInfo = new BackUserCurrentVO.UserVO();
            BackUserCurrentVO.PermissionVO newPermissionInfo = new BackUserCurrentVO.PermissionVO();
            //  如果是进行了脱敏处理，就可以创建一个新的工具类进行统一处理
            newUserInfo
                    .setUserName(getUserDO.getUserName())
                    .setRealName(getUserDO.getRealName())
                    .setEmail(getUserDO.getEmail())
                    .setPhone(getUserDO.getPhone())
                    .setAvatar(getUserDO.getAvatar())
                    .setUuid(getUserDO.getUuid());
            newPermissionInfo
                    .setUserPermission(gson.fromJson(getUserDO.getPermission(), new TypeToken<ArrayList<String>>() {}.getType()))
                    .setRolePermission(gson.fromJson(getRoleDO.getPermission(), new TypeToken<ArrayList<String>>() {}.getType()));
            backUserCurrent
                    .setUser(newUserInfo)
                    .setPermission(newPermissionInfo)
                    .setRole(getRoleDO.getName());
            // 数据输出
            return ResultUtil.success(timestamp, "用户查看的信息已准备完毕", backUserCurrent);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getUserList(
            long timestamp, @NotNull HttpServletRequest request,
            @NotNull String type, String search, @NotNull String limit, @NotNull String page, String order
    ) {
        log.info("[Service] 执行 getUserList 方法");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
        page = (page.isEmpty()) ? "1" : page;
        if (order == null || order.isBlank()) {
            order = "uid ASC";
        } else {
            order = "uuid " + order;
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        // 1. 对type类型进行判断
        List<UserDO> getUserDO;
        switch (type) {
            case "search" ->
                    getUserDO = userDAO.getUserFuzzy(search, Integer.valueOf(limit), Integer.valueOf(page), order);
            case "unbanlist" ->
                    getUserDO = userDAO.getUserByUnbanlist(Integer.valueOf(limit), Integer.valueOf(page), order);
            case "banlist" ->
                    getUserDO = userDAO.getUserByBanlist(Integer.valueOf(limit), Integer.valueOf(page), order);
            case "available" ->
                    getUserDO = userDAO.getUserByAvailableList(Integer.valueOf(limit), Integer.valueOf(page), order);
            case "all" -> getUserDO = userDAO.getUserByAllList(Integer.valueOf(limit), Integer.valueOf(page), order);
            default -> {
                ArrayList<String> errorCodeReturn = new ArrayList<>();
                errorCodeReturn.add("type 参数错误");
                return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR,errorCodeReturn);
            }
        }
        List<BackDesensitizationVO> desensitizationVO = modelMapper.map(getUserDO, new TypeToken<List<BackDesensitizationVO>>() {
        }.getType());
        return ResultUtil.success(timestamp, "管理员查看的信息已准备完毕", desensitizationVO);
    }

    @NotNull
    @Override
    @Transactional
    @CheckAccountPermission({"user:editUserInformation"})
    public ResponseEntity<BaseResponse> editUser(long timestamp, @NotNull HttpServletRequest request, @NotNull UserEditVO userEditVO) {
        log.info("[Service] 执行 editUserInformation 方法");
        String getAuthorizeUserUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        UserDO getUserDO = userDAO.getUserByUuid(getAuthorizeUserUuid);
        if (getUserDO == null) {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
        // 校验码进行校验（若修改了手机或者邮箱则需要进行校验）
        VerifyCodeDO getEmailCode = verifyCodeDAO.getVerifyCodeByContact(userEditVO.getEmailCode());
        VerifyCodeDO getPhoneCode = verifyCodeDAO.getVerifyCodeByContact(userEditVO.getPhoneCode());
        if (userEditVO.getEmail() != null) {
            if (getEmailCode == null) {
                return ResultUtil.error(timestamp, "邮箱验证码错误", ErrorCode.VERIFY_CODE_NOT_EXISTED);
            } else {
                if (!getEmailCode.getCode().equals(userEditVO.getEmailCode())) {
                    return ResultUtil.error(timestamp, "邮箱验证码错误", ErrorCode.VERIFY_CODE_NOT_EXISTED);
                } else {
                    contactCodeRedis.delData(BusinessConstants.EMAIL, userEditVO.getEmailCode());
                }
            }
        }
        if (userEditVO.getPhone() != null) {
            if (getPhoneCode == null) {
                return ResultUtil.error(timestamp, "手机验证码错误", ErrorCode.VERIFY_CODE_NOT_EXISTED);
            } else {
                if (!getPhoneCode.getCode().equals(userEditVO.getPhoneCode())) {
                    return ResultUtil.error(timestamp, "手机验证码错误", ErrorCode.VERIFY_CODE_NOT_EXISTED);
                } else {
                    contactCodeRedis.delData(BusinessConstants.PHONE, userEditVO.getPhoneCode());
                }
            }
        }
        // 对数据进行修改
        if (userEditVO.getAvatar() == null) {
            userEditVO.setAvatar(getUserDO.getAvatar());
        }
        if (userEditVO.getNickName() == null) {
            userEditVO.setNickName(getUserDO.getRealName());
        }
        if (userEditVO.getPhone() == null) {
            userEditVO.setPhone(getUserDO.getPhone());
        }
        if (userEditVO.getEmail() == null) {
            userEditVO.setEmail(getUserDO.getEmail());
        }
        // 对数据库进行数据操作
        if (userDAO.updateUserByUuid(getAuthorizeUserUuid, userEditVO)) {
            return ResultUtil.success(timestamp, "用户信息修改成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    @Transactional
    public ResponseEntity<BaseResponse> putUserForceEdit(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull String userUuid,
            @NotNull UserForceEditVO userForceEditVO
    ) {
        log.info("[Service] 执行 putUserForceEdit 方法");
        UserDO getUserDO = userDAO.getUserByUuid(userUuid);
        if (getUserDO != null) {
            //校验修改的用户是否为超级管理员
            if ("console".equals(roleDAO.getRoleByUuid(getUserDO.getRole()).getName())) {
                return ResultUtil.error(timestamp, ErrorCode.CAN_T_OPERATE_ONESELF);
            } else {
                if (userDAO.updateUserForceByUuid(getUserDO.getUuid(), userForceEditVO.getUserName(), userForceEditVO.getNickName(), userForceEditVO.getRealName(), userForceEditVO.getAvatar(), userForceEditVO.getEmail(), userForceEditVO.getPhone())) {
                    BackUserForceEditVO backUserForceEditVO = new BackUserForceEditVO();
                    backUserForceEditVO.setUuid(userUuid)
                            .setUserName(getUserDO.getUserName())
                            .setNickName(getUserDO.getNickName())
                            .setRealName(getUserDO.getRealName())
                            .setEmail(getUserDO.getEmail())
                            .setPhone(getUserDO.getPhone())
                            .setCreatedAt(getUserDO.getCreatedAt().toString())
                            .setUpdatedAt(getUserDO.getUpdatedAt() != null ? getUserDO.getUpdatedAt().toString() : null);
                    return ResultUtil.success(timestamp, "用户信息修改成功", backUserForceEditVO);
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> banUser(long timestamp, @NotNull HttpServletRequest request, @NotNull String banUserUuid) {
        log.info("[Service] 执行 banUser 方法");
        if (ProcessingUtil.checkUserHasSuperConsole(ProcessingUtil.getAuthorizeUserUuid(request), userDAO, roleDAO)) {
            log.info("[Service] console_user 超级管理员");
            if (!banUserUuid.equals(ProcessingUtil.getAuthorizeUserUuid(request))) {
                return getBaseResponseResponseEntity(timestamp, banUserUuid, userDAO);
            } else {
                return ResultUtil.error(timestamp, "您不能封禁自己", ErrorCode.USER_CANNOT_BE_OPERATE);
            }
        } else {
            log.info("[Service] 普通管理员");
            if (!ProcessingUtil.checkUserHasOtherConsole(banUserUuid, userDAO, roleDAO)) {
                return getBaseResponseResponseEntity(timestamp, banUserUuid, userDAO);
            } else {
                return ResultUtil.error(timestamp, "您不能封禁自己或封禁超级管理员", ErrorCode.USER_CANNOT_BE_OPERATE);
            }
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> forceLogout(long timestamp, @NotNull HttpServletRequest request, @NotNull String userUuid) {
        log.info("[Service] 执行 forceLogout 方法");
        // 先获取自己的身份信息
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUserDO != null) {
            if (!getUserDO.getUuid().equals(userUuid)) {
                UserDO getLogoutUserDO = userDAO.getUserByUuid(userUuid);
                if (getLogoutUserDO != null) {
                    if (ProcessingUtil.checkUserHasSuperConsole(getUserDO.getUuid(), userDAO, roleDAO)) {
                        // 如果是超级管理员，则可以注销任何人的账户（除了自己）
                        if (userDAO.forceLogout(userUuid)) {
                            return ResultUtil.success(timestamp, "账户已强制注销");
                        } else {
                            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                        }
                    } else {
                        // 如果是普通管理员，则只能注销普通用户的账户
                        if (ProcessingUtil.checkUserHasOtherConsole(userUuid, userDAO, roleDAO)) {
                            if (userDAO.forceLogout(userUuid)) {
                                return ResultUtil.success(timestamp, "账户已强制注销");
                            } else {
                                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                            }
                        } else {
                            return ResultUtil.error(timestamp, ErrorCode.CAN_T_OPERATE_ONESELF);
                        }
                    }
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
                }
            } else {
                return ResultUtil.error(timestamp, ErrorCode.CAN_T_OPERATE_ONESELF);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addAccount(long timestamp, @NotNull HttpServletRequest request, @NotNull UserAddVO userAddVO) {
        log.info("[Service] 执行 addAccount 方法");
        String getUserHasExist = userDAO.checkUserExist(userAddVO.getUsername(), userAddVO.getEmail(), userAddVO.getPhone(), userAddVO.getRealname());
        if (getUserHasExist == null) {
            String addPassword = ProcessingUtil.createRandomString(10);
            String addUuid = ProcessingUtil.createUuid();
            if (userAddVO.getRole() == null) {
                userAddVO.setRole("default");
            }
            RoleDO getDefaultRole = roleDAO.getRoleByName(userAddVO.getRole());
            if (getDefaultRole == null) {
                return ResultUtil.error(timestamp, ErrorCode.ROLE_NOT_EXISTED);
            }
            // 新建用户
            UserDO newUserDO = new UserDO();
            newUserDO
                    .setUuid(addUuid)
                    .setUserName(userAddVO.getUsername())
                    .setRealName(userAddVO.getRealname())
                    .setEmail(userAddVO.getEmail())
                    .setPhone(userAddVO.getPhone())
                    .setPassword(ProcessingUtil.passwordEncrypt(addPassword))
                    .setRole(getDefaultRole.getUuid())
                    .setPermission("[]");
            if (userDAO.createUser(newUserDO)) {
                BackAddUserVO backAddUserVO = new BackAddUserVO();
                backAddUserVO
                        .setUuid(addUuid)
                        .setUserName(userAddVO.getUsername())
                        .setRealName(userAddVO.getRealname())
                        .setPassword(addPassword)
                        .setEmail(userAddVO.getEmail())
                        .setPhone(userAddVO.getPhone());
                return ResultUtil.success(timestamp, "添加用户信息成功", backAddUserVO);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, getUserHasExist, ErrorCode.USER_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getUserByUuid(long timestamp, @NotNull HttpServletRequest request, @NotNull String userUuid) {
        // 获取用户的 UUID
        UserDO getUserDO = userDAO.getUserByUuid(userUuid);
        if (getUserDO != null) {
            BackUserVO backUserVO = new BackUserVO();
            backUserVO
                    .setUuid(getUserDO.getUuid())
                    .setUserName(getUserDO.getUserName())
                    .setRealName(getUserDO.getRealName())
                    .setEmail(getUserDO.getEmail())
                    .setPhone(getUserDO.getPhone())
                    .setNickName(getUserDO.getNickName())
                    .setAvatar(getUserDO.getAvatar())
                    .setCreatedAt(getUserDO.getCreatedAt())
                    .setUpdatedAt(getUserDO.getUpdatedAt());
            return ResultUtil.success(timestamp, "获取用户信息成功", backUserVO);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }
}
