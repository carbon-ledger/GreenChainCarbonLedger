package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.common.constants.RedisExpiration;
import com.frontleaves.greenchaincarbonledger.mappers.RoleMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.utils.redis.RoleRedis;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * RoleDAO
 * <hr/>
 * 用于角色的数据访问对象, 用于角色的数据访问
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleDAO {
    private final RoleRedis roleRedis;
    private final RoleMapper roleMapper;
    private final Gson gson;

    /**
     * 根据uuid获取角色信息
     * <hr/>
     * 根据uuid获取角色信息, 用于获取角色信息
     *
     * @param roleUuid 角色uuid
     * @return 角色信息
     */
    public RoleDO getRoleUuid(@NotNull String roleUuid) {
        log.info("[DAO] 执行 getRoleUuid 方法");
        log.info("\t> Redis 读取");
        String getRedisRoleDO = roleRedis.getData(BusinessConstants.NONE, roleUuid);
        RoleDO getRoleDO;
        if (getRedisRoleDO != null && !getRedisRoleDO.isEmpty()) {
            getRoleDO = gson.fromJson(getRedisRoleDO, RoleDO.class);
        } else {
            log.info("\t> Mysql 读取");
            getRoleDO = roleMapper.getRoleByUuid(roleUuid);
            log.info("\t> Redis 写入");
            roleRedis.setData(BusinessConstants.NONE, roleUuid, gson.toJson(getRoleDO), RedisExpiration.DAY);
        }
        return getRoleDO;
    }

    /**
     * 根据角色名获取角色信息
     * <hr/>
     * 根据角色名获取角色信息, 用于获取角色信息
     *
     * @param roleName 角色名
     * @return 角色信息
     */
    public RoleDO getRoleByName(@NotNull String roleName) {
        log.info("[DAO] 执行 getRoleByName 方法");
        log.info("\t> Redis 读取");
        ArrayList<String> getRedisRolesDO = (ArrayList<String>) roleRedis.getList(BusinessConstants.ALL);
        if (getRedisRolesDO != null && !getRedisRolesDO.isEmpty()) {
            // 遍历获取角色信息
            for (String getRedisData : getRedisRolesDO) {
                RoleDO getRedisRoleDO = gson.fromJson(getRedisData, RoleDO.class);
                if (getRedisRoleDO.getName().equals(roleName)) {
                    return getRedisRoleDO;
                }
            }
        }
        log.info("\t> Mysql 读取");
        RoleDO getRoleDO = roleMapper.getRoleByName(roleName);
        if (getRoleDO != null) {
            log.info("\t> Redis 写入");
            roleRedis.setData(BusinessConstants.ALL, getRoleDO.getUuid(), gson.toJson(getRoleDO), RedisExpiration.DAY);
        }
        return getRoleDO;
    }

    /**
     * 获取所有角色信息
     * <hr/>
     * 获取所有角色信息
     *
     * @param limit 限制
     * @param page  页数
     * @param order 顺序
     * @return 角色信息
     */
    public List<RoleDO> getRoleByAllList(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getUserByAllList 方法");
        log.info("\t> Mysql 读取");
        return roleMapper.getRoleByAllList(limit, page, order);
    }

    /**
     * 模糊查询角色信息
     * <hr/>
     * 模糊查询角色信息
     *
     * @param search 关键字查询
     * @param limit  限制
     * @param page   页数
     * @param order  顺序
     * @return 角色信息
     */
    public List<RoleDO> getRoleByFuzzy(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getRoleByFuzzy 方法");
        log.info("\t> Mysql 读取");
        return roleMapper.getRoleByFuzzy(search, limit, page, order);
    }

    /**
     * 从Permission表中获取otherSearch来模糊查询角色信息
     * <hr/>
     * 从Permission表中获取otherSearch来模糊查询角色信息
     *
     * @param name Permission表中权限的名字
     * @return 角色信息
     */
    public List<RoleDO> getRoleByPermissionName(String name) {
        log.info("[DAO] 执行 getRoleByPermissionName 方法");
        log.info("\t> Mysql 读取");
        return roleMapper.getRoleByPermissionName(name);
    }
}
