package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.common.constants.RedisExpiration;
import com.frontleaves.greenchaincarbonledger.mappers.UserMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserEditVO;
import com.frontleaves.greenchaincarbonledger.utils.redis.UserRedis;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户DAO
 * <hr/>
 * 用于用户的数据访问对象
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
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
        log.info("[DAO] 执行 getUserByUuid 方法");
        log.info("\t> Redis 读取");
        String getRedisUserDO = userRedis.getData(BusinessConstants.NONE, uuid);
        if (getRedisUserDO != null && !getRedisUserDO.isEmpty()) {
            return gson.fromJson(getRedisUserDO, UserDO.class);
        }
        log.info("\t> Mysql 读取");
        UserDO getUserDO = userMapper.getUserByUuid(uuid);
        log.info("\t> Redis 写入");
        userRedis.setData(BusinessConstants.NONE, uuid, gson.toJson(getUserDO), RedisExpiration.HOUR);
        return getUserDO;
    }

    /**
     * 检查用户是否存在
     * <hr/>
     * 检查用户是否存在, 如果存在则返回错误信息, 否则返回null, 表示用户不存在, 可以注册
     *
     * @param username 用户名
     * @param email    邮箱
     * @param phone    手机号
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

    /**
     * 根据邮箱获取用户
     * <hr/>
     * 根据邮箱获取用户
     *
     * @param user Email
     * @return {@link UserDO}
     */
    public UserDO getUserByEmail(String user) {
        log.info("[DAO] 执行 getUserByEmail 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByEmail(user);
    }

    /**
     * 根据手机号获取用户
     * <hr/>
     * 根据手机号获取用户
     *
     * @param user Phone
     * @return {@link UserDO}
     */
    public UserDO getUserByPhone(String user) {
        log.info("[DAO] 执行 getUserByPhone 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByPhone(user);
    }

    /**
     * 根据用户名获取用户
     * <hr/>
     * 根据用户名获取用户
     *
     * @param user Username
     * @return {@link UserDO}
     */
    public UserDO getUserByUsername(String user) {
        log.info("[DAO] 执行 getUserByUsername 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByUsername(user);
    }

    /**
     * 数据库用户密码更新
     * <hr/>
     * 数据库用户密码更新，如果用户密码修改成功返回ture,失败则返回false
     *
     * @param getUserDO 用户
     * @return 更新操作是否成功，成功返回 true，失败返回 false
     */
    public boolean updateUserPassword(UserDO getUserDO) {
        log.info("[DAO] 执行 updateUserPassword 方法");
        log.info("\t> Mysql 更新");
        return userMapper.updateUserPassword(getUserDO);
    }

    /**
     * 数据库用户账号软删除（只设置了时间戳）
     * </hr>
     * 数据库用户账号注销，只设置了时间戳
     *
     * @param getUserDO 用户
     * @return 注销操作成功返回ture，失败则返回false
     */
    public boolean userAccountDeletion(@NotNull UserDO getUserDO) {
        log.info("[DAO] 执行 deleteUserAccount 方法");
        log.info("\t> Mysql  软删除");
        return userMapper.userAccountDeletion(getUserDO.getUuid());
    }

    /**
     * 数据库用户账号软删除
     * </hr>
     * 数据库用户账号注销
     *
     * @param getUserDO 用户
     * @return 删除操作成功返回ture，失败则返回false
     */
    public boolean userAccountDistanceDeletion(@NotNull UserDO getUserDO) {
        log.info("[DAO] 执行 userAccountDistanceDeletion 方法");
        log.info("\t> Mysql 更新");
        return userMapper.userAccountDistanceDeletion(getUserDO.getUuid());
    }

    /**
     * 获取用户的邀请码
     * <hr/>
     * 获取用户的邀请码, 如果用户存在则返回邀请码, 否则返回null
     *
     * @param invite 邀请码
     */
    public Boolean getUserByInvite(String invite) {
        log.info("[DAO] 执行 getUserByInvite 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByInvite(invite);
    }

    /**
     * 获取用户列表
     * <hr/>
     * 获取用户列表
     *
     * @param search 关键字查询
     * @param limit  限制
     * @param page   页数
     * @param order  顺序
     * @return 用户列表
     */
    public List<UserDO> getUserFuzzy(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserFuzzy 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserFuzzy(search, limit, page, order);
    }

    /**
     * 获取用户列表
     * <hr/>
     * 获取用户列表
     *
     * @param limit 限制
     * @param page  页数
     * @param order 顺序
     * @return 用户列表
     */
    public List<UserDO> getUserByUnbanlist(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserByUnbanlist 方法");
        log.info("\t> Mysql 读取");

        return userMapper.getUserByUnbanlist(limit, page, order);
    }

    /**
     * 获取用户列表
     * <hr/>
     * 获取用户列表
     *
     * @param limit 限制
     * @param page  页数
     * @param order 顺序
     * @return 用户列表
     */
    public List<UserDO> getUserByBanlist(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserByBanlist 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByBanlist(limit, page, order);

    }

    /**
     * 获取用户列表
     * <hr/>
     * 获取用户列表
     *
     * @param limit 限制
     * @param page  页数
     * @param order 顺序
     * @return 用户列表
     */
    public List<UserDO> getUserByAvailablelist(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserByAvailablelist 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByAvailablelist(limit, page, order);
    }

    /**
     * 获取用户列表
     * <hr/>
     * 获取用户列表
     *
     * @param limit 限制
     * @param page  页数
     * @param order 顺序
     * @return 用户列表
     */
    public List<UserDO> getUserByAlllist(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserByAlllist 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getUserByAlllist(limit, page, order);
    }

    /**
     * 更新用户信息
     * <hr/>
     * 更新用户信息
     *
     * @param getAuthorizeUserUuid 用户UUID
     * @param userEditVO           用户编辑信息
     * @return 更新操作是否成功，成功返回 true，失败返回 false
     */
    public boolean updateUserByUuid(String getAuthorizeUserUuid, UserEditVO userEditVO) {
        log.info("[DAO] 执行 updateUserByUuid 方法");
        log.info("\t> Mysql 更新");
        return userMapper.updateUserByUuid(getAuthorizeUserUuid, userEditVO);
    }

    /**
     * 通过search在user表中查询出role的链表
     * <hr/>
     * 通过search在user表中查询出role的链表
     *
     * @param search 关键字查询
     * @param limit  限制
     * @param page   页数
     * @param order  顺序
     * @return role链表
     */
    public List<String> getRoleByAllList(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getRoleByAllList 方法");
        log.info("\t> Mysql 读取");
        return userMapper.getRoleByAllList(search, limit, page, order);
    }

    /**
     * 更新用户信息
     * <hr/>
     * 更新用户信息, 如果用户信息更新成功返回ture,失败则返回false
     *
     * @param userUuid 用户UUID
     * @param userName 用户名
     * @param nickName 昵称
     * @param realName 真实姓名
     * @param avatar   头像
     * @param email    邮箱
     * @param phone    手机号
     * @return 返回更新的结果
     */
    public boolean updateUserForceByUuid(String userUuid, String userName, String nickName, String realName, String avatar, String email, String phone) {
        log.info("[Dao] 执行 updateUserForceByUuid 方法");
        log.info("\t> Redis 删除 ");
        userRedis.delData(BusinessConstants.NONE, userUuid);
        log.info("\t> Mysql 更新");
        return userMapper.updateUserForceByUuid(userUuid, userName, nickName, realName, avatar, email, phone);
    }
}