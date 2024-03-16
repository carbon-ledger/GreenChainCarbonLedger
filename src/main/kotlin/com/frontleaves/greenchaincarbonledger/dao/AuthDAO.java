package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.common.constants.RedisExpiration;
import com.frontleaves.greenchaincarbonledger.models.doData.UserLoginDO;
import com.frontleaves.greenchaincarbonledger.utils.redis.AuthorizeRedis;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * 用于存放控制器层面的持久性数据
 * <hr/>
 * 用于存放控制器层面的持久性数据
 *
 * @author FLASHLACK
 * @since 2024-03-04
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthDAO {
    private final AuthorizeRedis authorizeRedis;
    private final Gson gson;

    /**
     * 对用户状态登录的信息进行操作
     * <hr/>
     * 对用户状态登录的信息进行读取和存储
     *
     * @author FLASHLACK
     * @since 2024-03-04
     */
    public void saveAuthInfo(@NotNull UserLoginDO userLoginDO) {
        log.info("[DAO] 执行 saveAuthInfo 方法");
        log.info("\t> Redis 读取");
        BusinessConstants[] devices = {BusinessConstants.ONE, BusinessConstants.TWO, BusinessConstants.THREE};
        long minTime = Long.MAX_VALUE;
        BusinessConstants deviceToSet = BusinessConstants.ONE;
        boolean isSet = false;

        // 首先尝试找到一个空闲的设备
        for (BusinessConstants device : devices) {
            if (authorizeRedis.getData(device, userLoginDO.getUuid()) == null) {
                log.info("\t> Redis 存储");
                authorizeRedis.setData(device, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.HOUR);
                isSet = true;
                break; // 找到空闲设备，跳出循环
            }
        }

        // 如果没有空闲设备，找到最近过期的设备
        if (!isSet) {
            for (BusinessConstants device : devices) {
                long expiredAt = authorizeRedis.getExpiredAt(device, userLoginDO.getUuid());
                if (expiredAt < minTime) {
                    minTime = expiredAt;
                    deviceToSet = device;
                }
                log.info("\t> Redis 存储");
                authorizeRedis.setData(deviceToSet, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.HOUR);
            }
        }
    }


    /**
     * 删除用户缓存
     * <hr/>
     * 删除用户缓存
     *
     * @author FLASHLACK
     * @since 2024-03-04
     */
    public void userLogout(String getUuid, String getToken) {
        log.info("[DAO] 执行 userLogout 方法");
        log.info("\t> Redis 读取");
        // 先去服务器读取出 1，2，3 的 Redis
        String userRedisOne = authorizeRedis.getData(BusinessConstants.ONE, getUuid);
        String userRedisTwo = authorizeRedis.getData(BusinessConstants.TWO, getUuid);
        String userRedisThree = authorizeRedis.getData(BusinessConstants.THREE, getUuid);

        // 处理 ONE
        if (userRedisOne != null) {
            //创建实例存放Redis
            UserLoginDO userRedisDO = gson.fromJson(userRedisOne, UserLoginDO.class);
            // 再对用户本地缓存是否和服务器中缓存进行对比
            if (userRedisDO.getToken().equals(getToken)) {
                log.info("\t> Redis 删除");
                authorizeRedis.delData(BusinessConstants.ONE, getUuid);
            }
        }

        // 处理 TWO
        if (userRedisTwo != null) {
            UserLoginDO userRedisDO = gson.fromJson(userRedisTwo, UserLoginDO.class);
            if (userRedisDO.getToken().equals(getToken)) {
                log.info("\t> Redis 删除");
                authorizeRedis.delData(BusinessConstants.TWO, getUuid);
            }
        }

        // 处理 THREE
        if (userRedisThree != null) {
            UserLoginDO userRedisDO = gson.fromJson(userRedisThree, UserLoginDO.class);
            if (userRedisDO.getToken().equals(getToken)) {
                log.info("\t Redis 删除");
                authorizeRedis.delData(BusinessConstants.THREE, getUuid);
            }
        }
    }

    /**
     * 获取用户授权信息
     * <hr/>
     * 获取用户授权信息
     *
     * @param authorizeUserUuid 用户UUID
     * @return {@link ArrayList<UserLoginDO>}
     */
    public ArrayList<UserLoginDO> getAuthorize(String authorizeUserUuid) {
        log.info("[DAO] 执行 getAuthorize 方法");
        // Redis 读取数据
        log.info("\t> Redis 读取");
        ArrayList<UserLoginDO> getUserLoginList = new ArrayList<>();
        getUserLoginList.add(gson.fromJson(authorizeRedis.getData(BusinessConstants.ONE, authorizeUserUuid), UserLoginDO.class));
        getUserLoginList.add(gson.fromJson(authorizeRedis.getData(BusinessConstants.TWO, authorizeUserUuid), UserLoginDO.class));
        getUserLoginList.add(gson.fromJson(authorizeRedis.getData(BusinessConstants.THREE, authorizeUserUuid), UserLoginDO.class));
        return getUserLoginList;
    }
}


