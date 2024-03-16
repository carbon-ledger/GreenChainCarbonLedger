package com.frontleaves.greenchaincarbonledger.utils;

import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * ProcessingUtil
 * <hr/>
 * 用于处理请求的工具类, 对通用内容进行工具化处理
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
public class ProcessingUtil {

    /**
     * 获取参数校验错误信息
     * <hr/>
     * 用于获取参数校验错误信息
     *
     * @param bindingResult 参数校验结果
     * @return {@link ArrayList <String>}
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ArrayList<String> getValidatedErrorList(@NotNull BindingResult bindingResult) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            arrayList.add(objectError.getDefaultMessage());
        }
        return arrayList;
    }

    /**
     * 密码加密
     * <hr/>
     * 用于对密码进行加密
     *
     * @param password 密码
     * @return {@link String}
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static String passwordEncrypt(@NotNull String password) {
        return BCrypt.hashpw(DigestUtils.sha256Hex(password), BCrypt.gensalt());
    }

    /**
     * 密码校验
     * <hr/>
     * 用于对密码进行校验
     *
     * @param password 密码
     * @param hash     密码hash
     * @return {@link Boolean}
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static Boolean passwordCheck(@NotNull String password, @NotNull String hash) {
        return BCrypt.checkpw(DigestUtils.sha256Hex(password), hash);
    }

    /**
     * 创建UUID
     * <hr/>
     * 用于创建UUID
     *
     * @return {@link String}
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static String createUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 创建随机字符串
     * <hr/>
     * 用于创建随机字符串
     *
     * @param size 字符串长度
     * @return {@link String}
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static String createRandomString(int size) {
        log.info("[Util] 执行 createRandomString 工具");
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            switch (random.nextInt(3)) {
                case 0 -> code = String.valueOf(random.nextInt(10));
                case 1 -> code = String.valueOf((char) (random.nextInt(26) + 65));
                default -> code = String.valueOf((char) (random.nextInt(26) + 97));
            }
            stringBuilder.append(code);
        }
        String getRandomString = stringBuilder.toString();
        log.debug("\t> 获取随机字符串: {}", getRandomString);

        return getRandomString;
    }

    /**
     * 获取用户UUID
     * <hr/>
     * 用于获取用户UUID, 从请求头 X-Auth-UUID 获取信息, 一般用于获取用户信息（即通过UUID获取当前登陆用户）
     *
     * @param request 请求对象
     * @return {@link String}
     * @since v1.0.0
     */
    @NotNull
    public static UserDO getUserByHeaderUuid(@NotNull HttpServletRequest request, @NotNull UserDAO userDAO) {
        log.info("[Util] 执行 getUserByHeaderUuid 工具");
        String authUuid = getAuthorizeUserUuid(request);
        UserDO getUserDO = userDAO.getUserByUuid(authUuid);
        log.debug("\t> 获取用户: {}", getUserDO.getUserName());

        return getUserDO;
    }

    /**
     * 获取用户Token
     * <hr/>
     * 用于获取用户Token, 从请求头 Authorization 获取信息, 一般用于获取用户信息（即通过Token获取当前登陆用户）
     *
     * @param request 请求对象
     * @return {@link String}
     * @since v1.0.0
     */
    @NotNull
    public static String getAuthorizeUserUuid(@NotNull HttpServletRequest request) {
        log.info("[Util] 执行 getAuthorizeUserUuid 工具");
        String getAuthUuid = request.getHeader("X-Auth-UUID");
        log.debug("\t> 获取UUID: {}", getAuthUuid);

        return getAuthUuid;
    }

    /**
     * 检查用户是否有权限
     * <hr/>
     * 用于检查用户是否有权限
     *
     * @param userUuid 用户UUID
     * @param userDAO 用户DAO
     * @param roleDAO 角色DAO
     * @return {@link Boolean}
     * @since v1.0.0
     */
    public static boolean checkUserHasOtherConsole(String userUuid, @NotNull UserDAO userDAO, @NotNull RoleDAO roleDAO) {
        log.info("[Util] 执行 checkUserHasOtherConsole 工具");
        // 获取用户信息
        UserDO getUserDO = userDAO.getUserByUuid(userUuid);
        if (getUserDO != null) {
            log.debug("\t> 获取用户信息: {}", getUserDO.getUserName());
            RoleDO getRoleDO = roleDAO.getRoleByUuid(getUserDO.getRole());
            log.debug("\t> 获取角色信息: {}", getRoleDO.getName());
            boolean checkUserHasConsole = "console".equals(getRoleDO.getName());
            log.debug("\t> 检查用户是超级管理员: {}", checkUserHasConsole);
            return checkUserHasConsole;
        } else {
            return false;
        }
    }

    /**
     * 检查用户是否有超级管理员权限
     * <hr/>
     * 用于检查用户是否有超级管理员权限
     *
     * @param userUuid 用户UUID
     * @param userDAO 用户DAO
     * @param roleDAO 角色DAO
     * @return {@link Boolean}
     * @since v1.0.0
     */
    public static boolean checkUserHasSuperConsole(String userUuid, @NotNull UserDAO userDAO, @NotNull RoleDAO roleDAO) {
        log.info("[Util] 执行 checkUserHasSuperConsole 工具");
        // 获取用户信息
        UserDO getUserDO = userDAO.getUserByUuid(userUuid);
        if (getUserDO != null) {
            log.debug("\t> 获取用户信息: {}", getUserDO.getUserName());
            if ("console_user".equals(getUserDO.getUserName())) {
                RoleDO getRoleDO = roleDAO.getRoleByUuid(getUserDO.getRole());
                return "console".equals(getRoleDO.getName());
            }
        }
        return false;
    }

    /**
     * 获取Token
     * <hr/>
     * 用于获取Token, 从请求头 Authorization 获取信息
     *
     * @param request 请求对象
     * @return {@link String}
     * @since v1.0.0
     */
    public static String getAuthorizeToken(@NotNull HttpServletRequest request) {
        log.info("[Util] 执行 getAuthorizeToken 工具");
        String getAuthUuid = request.getHeader("Authorization").replace("Bearer ", "");
        log.debug("\t> 获取Token: {}", getAuthUuid);

        return getAuthUuid;
    }
}
