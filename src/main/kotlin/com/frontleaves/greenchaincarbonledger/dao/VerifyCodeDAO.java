package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.common.constants.RedisExpiration;
import com.frontleaves.greenchaincarbonledger.models.doData.VerifyCodeDO;
import com.frontleaves.greenchaincarbonledger.utils.redis.ContactCodeRedis;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

/**
 * VerifyCodeDAO
 * <hr/>
 * 用于验证码的数据访问对象
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class VerifyCodeDAO {
    private final ContactCodeRedis contactCodeRedis;
    private final Gson gson;

    /**
     * 获取验证码
     * <hr/>
     * 获取验证码；根据联系方式，获取验证码；如果联系方式不合法，则返回null；如果联系方式合法，但是没有验证码，则返回null；如果联系方式合法，
     * 且有验证码，则返回验证码
     *
     * @param content 验证码联系方式
     */
    public VerifyCodeDO getVerifyCodeByContact(String content) {
        log.info("[DAO] 执行 getVerifyCodeByContact 方法");
        log.info("\t> Redis 读取");
        String redisData;
        if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", content)) {
            redisData = contactCodeRedis.getData(BusinessConstants.EMAIL, content);
        } else if (Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", content)) {
            redisData = contactCodeRedis.getData(BusinessConstants.PHONE, content);
        } else {
            return null;
        }
        // 如果redisData不为空，则返回
        if (redisData != null) {
            return gson.fromJson(redisData, VerifyCodeDO.class);
        }
        return null;
    }

    /**
     * 删除验证码
     * <hr/>
     * 删除验证码；根据联系方式，删除验证码；如果联系方式不合法，则返回false；如果联系方式合法，但是删除失败，则返回false；如果联系方式合法，
     * 且删除成功，则返回true
     *
     * @param content 验证码联系方式
     */
    public boolean deleteVerifyCode(String content) {
        log.info("[DAO] 执行 deleteVerifyCode 方法");
        log.info("\t> Redis 删除");
        boolean hasDelete = false;
        if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", content)) {
            hasDelete = contactCodeRedis.delData(BusinessConstants.EMAIL, content);
        } else if (Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", content)) {
            hasDelete = contactCodeRedis.delData(BusinessConstants.PHONE, content);
        }
        if (hasDelete) {
            return true;
        } else {
            log.error("\t> 删除失败");
            return false;
        }
    }

    /**
     * 插入验证码
     * <hr/>
     * 插入验证码；根据联系方式，插入验证码；如果联系方式不合法，则返回false；如果联系方式合法，但是插入失败，则返回false；如果联系方式合法，
     * 且插入成功，则返回true
     *
     * @param newVerifyCodeDO 新验证码数据对象
     */
    public boolean insertVerifyCodeByEmail(@NotNull VerifyCodeDO newVerifyCodeDO) {
        log.info("[DAO] 执行 insertVerifyCodeByEmail 方法");
        log.info("\t> Redis 保存");
        return contactCodeRedis.setData(BusinessConstants.EMAIL, newVerifyCodeDO.getContent(), gson.toJson(newVerifyCodeDO), RedisExpiration.MINUTE_15);
    }
}
