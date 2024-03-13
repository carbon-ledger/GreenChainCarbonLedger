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
 * @author FLASHLACK
 * @since 2024-03-04
 *
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
        log.info("[DAO] 执行用户登录信息状态存储");
        log.info("\t> Redis 读取");
        //首先判断是否有1，若没有1则进行存储，若有1则返回假不进行if里面内容
        if (authorizeRedis.getData(BusinessConstants.ONE, userLoginDO.getUuid()) == null) {
            log.info("\t Redis 存储");
            authorizeRedis.setData(BusinessConstants.ONE, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
        } else if (authorizeRedis.getData(BusinessConstants.TWO, userLoginDO.getUuid()) == null) {
            log.info("\t Redis 存储");
            authorizeRedis.setData(BusinessConstants.TWO, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
        } else if (authorizeRedis.getData(BusinessConstants.THREE, userLoginDO.getUuid()) == null) {
            log.info("\t Redis 存储");
            authorizeRedis.setData(BusinessConstants.THREE, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
        } else {
            // 现在3个设备同时在线，我需要进行时间判断，找到时间最小的序号
            long t1 = authorizeRedis.getExpiredAt(BusinessConstants.ONE, userLoginDO.getUuid());
            long t2 = authorizeRedis.getExpiredAt(BusinessConstants.TWO, userLoginDO.getUuid());
            long t3 = authorizeRedis.getExpiredAt(BusinessConstants.THREE, userLoginDO.getUuid());
            if (t1 < t2 && t1 < t3) {
                log.info("\t Redis 存储");
                authorizeRedis.setData(BusinessConstants.ONE, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
            }
            if (t2 < t1 && t2 < t3) {
                log.info("\t Redis 存储");
                authorizeRedis.setData(BusinessConstants.TWO, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
            }
            if (t3 < t1 && t3 < t2) {
                log.info("\t Redis 存储");
                authorizeRedis.setData(BusinessConstants.THREE, userLoginDO.getUuid(), gson.toJson(userLoginDO), RedisExpiration.DAY);
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
        log.info("[DAO] 执行用户缓存删除");
        log.info("\t Redis 读取");
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
                log.info("\t Redis 删除");
                authorizeRedis.delData(BusinessConstants.ONE, getUuid);
            }
        }

        // 处理 TWO
        if (userRedisTwo != null) {
            UserLoginDO userRedisDO = gson.fromJson(userRedisTwo, UserLoginDO.class);
            if (userRedisDO.getToken().equals(getToken)) {
                log.info("\t Redis 删除");
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

    public ArrayList<UserLoginDO> getAuthorize(String authorizeUserUuid) {
        // Redis 读取数据
        String getRedisData = authorizeRedis.getData(BusinessConstants.ONE, authorizeUserUuid);
        ArrayList<UserLoginDO> getUserLoginList = new ArrayList<>();
        getUserLoginList.add(gson.fromJson(getRedisData, UserLoginDO.class));
        getRedisData = authorizeRedis.getData(BusinessConstants.TWO, authorizeUserUuid);
        getUserLoginList.add(gson.fromJson(getRedisData, UserLoginDO.class));
        getRedisData = authorizeRedis.getData(BusinessConstants.THREE, authorizeUserUuid);
        getUserLoginList.add(gson.fromJson(getRedisData, UserLoginDO.class));
        return getUserLoginList;
    }
}


