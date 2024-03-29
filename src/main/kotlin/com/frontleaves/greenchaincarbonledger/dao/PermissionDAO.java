package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.PermissionMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存放Permission的数据
 * <hr/>
 * 用于存放Permission的数据
 * @author FLASHLACK
 * @since 2024-03-06 V1.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PermissionDAO {
    private final PermissionMapper permissionMapper;

    /**
     * 通过模糊查询获取Permission表里的name
     * <hr/>
     * 通过模糊查询来获取Permission表里的name
     *
     * @param search 关键字查询
     * @return getNameBySearch String
     */
    public List<String> getNameBySearch(String search, String order) {
        log.info("[DAO] 执行 getNameBy");
        log.info("\t> Mysql 读取");
        return permissionMapper.getNameBySearch(search, order);
    }

    /**
     * 获取权限列表
     * <hr/>
     * 获取权限列表
     *
     * @return 权限列表
     */
    public  List<PermissionDO> getAllPermissionList(){
        log.info("[DAO] 执行getPermissionListByAll");
        log.info("\t>Mysql 读取 ");
        return permissionMapper.getAllPermissionList();
    }
}
