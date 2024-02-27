package com.frontleaves.greenchaincarbonledger.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
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
}
