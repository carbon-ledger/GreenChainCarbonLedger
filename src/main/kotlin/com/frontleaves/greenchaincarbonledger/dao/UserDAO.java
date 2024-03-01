package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.mappers.UserMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.utils.redis.UserRedis;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用户DAO
 * <hr/>
 * 用于用户的数据访问对象
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;
    private final UserRedis userRedis;
    private final Gson gson;

    /**
     * 根据UUID获取用户
     * <hr/>
     * 根据UUID获取用户
     *
     * @param uuid UUID
     * @return {@link UserDO}
     */
    public UserDO getUserByUuid(String uuid) {
        log.info("[DAO] 执行 getUserByUUID 方法");
        if (userRedis.getData(BusinessConstants.NONE, uuid) != null) {
            log.info("\t> Redis 读取");
            return gson.fromJson(userRedis.getData(BusinessConstants.NONE, uuid), UserDO.class);
        } else {
            log.info("\t> Mysql 读取");
            return userMapper.getUserByUuid(uuid);
        }
    }

    /**
     * 检查用户是否存在
     * <hr/>
     * 检查用户是否存在, 如果存在则返回错误信息, 否则返回null, 表示用户不存在, 可以注册
     *
     * @param username 用户名
     * @param email 邮箱
     * @param phone 手机号
     * @param realname 真实姓名
     * @return {@link String}
     */
    public String checkUserExist(String username, String email, String phone, String realname) {
        log.info("[DAO] 执行 checkUserExist 方法");
        UserDO userDO = this.getUserByUsername(username);
        if (userDO != null) {
            return "用户名已存在";
        }
        userDO = this.getUserByEmail(email);
        if (userDO != null) {
            return "邮箱已存在";
        }
        userDO = this.getUserByPhone(phone);
        if (userDO != null) {
            return "手机号已存在";
        }
        userDO = userMapper.getUserByRealname(realname);
        if (userDO != null) {
            return "真实姓名已存在";
        }
        return null;
    }

    /**
     * 创建用户
     * <hr/>
     * 创建用户, 将用户信息写入数据库
     *
     * @param newUserDO 新用户
     */
    public boolean createUser(UserDO newUserDO) {
        log.info("[DAO] 执行 createUser 方法");
        log.info("\t> Mysql 写入");
        if (userMapper.createUser(newUserDO)) {
            log.debug("\t> 创建用户成功");
            return true;
        } else {
            log.debug("\t> 创建用户失败");
            return false;
        }
    }

    public UserDO getUserByEmail(String user) {
        log.info("[DAO] 执行 getUserByEmail 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByEmail(user);
    }

    public UserDO getUserByPhone(String user) {
        log.info("[DAO] 执行 getUserByPhone 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByPhone(user);
    }

    public UserDO getUserByUsername(String user) {
        log.info("[DAO] 执行 getUserByUsername 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByUsername(user);
    }

    public Boolean getUserByInvite(String invite){
        log.info("[DAO] 执行 getUserByInvite 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByInvite(invite);
    }
}
