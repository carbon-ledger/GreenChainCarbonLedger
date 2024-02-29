package com.frontleaves.greenchaincarbonledger.utils;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import jakarta.servlet.http.HttpServletRequest;
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
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
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
     * @param hash 密码hash
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
    public static String createRandomNumbers(int size) {
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
        return stringBuilder.toString();
    }

    /**
     * 通过Cookie获取用户信息
     * <hr/>
     * 用于通过Cookie获取用户信息
     *
     * @param request 请求对象
     * @param userDAO 用户DAO
     * @return {@link UserDO}
     * @since v1.0.0
     */
    @NotNull
    public static UserDO getUserByCookie(@NotNull HttpServletRequest request, @NotNull UserDAO userDAO) {
        String token = request.getHeader("X-Auth-UUID");
        return userDAO.getUserByUuid(token);
    }
}
