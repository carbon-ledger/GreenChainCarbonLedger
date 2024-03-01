package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * RoleMapper
 * <hr/>
 * 用于角色的数据访问对象, 用于角色的数据访问
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Mapper
public interface RoleMapper {
    @Select("SELECT * FROM fy_role WHERE uuid = #{uuid}")
    RoleDO getRoleByUuid(String uuid);

    @Select("SELECT * FROM fy_role WHERE name = #{name}")
    RoleDO getRoleByName(String name);
}
