package com.frontleaves.greenchaincarbonledger.utils;

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
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
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
     * 获取用户UUID
     * <hr/>
     * 用于获取用户UUID, 从请求头 X-Auth-UUID 获取信息, 一般用于获取用户信息（即通过UUID获取当前登陆用户）
     *
     * @param request 请求对象
     * @return {@link String}
     * @since v1.0.0
     */
    @NotNull
    public static String getAuthorizeUserUuid(@NotNull HttpServletRequest request) {
        return request.getHeader("X-Auth-UUID");
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
    public static String getAuthorizeToken(@NotNull HttpServletRequest request) {
        return request.getHeader("Authorization").replace("Bearer ", "");
    }
}
