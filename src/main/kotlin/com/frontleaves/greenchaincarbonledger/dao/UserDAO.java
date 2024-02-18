package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mapper.UserMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用户DAO
 * <hr/>
 * 用于用户的数据访问对象
 *
 * @version v1.1.0
 * @since v1.1.0
 * @author xiao_lfeng
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;

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
        log.info("\t> Mysql 读取");
        return userMapper.getUserByUuid(uuid);
    }
}
