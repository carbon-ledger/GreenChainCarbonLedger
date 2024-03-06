package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * RoleMapper
 * <hr/>
 * 用于角色的数据访问对象, 用于角色的数据访问
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Mapper
public interface RoleMapper {
    @Select("SELECT * FROM fy_role WHERE uuid = #{uuid}")
    RoleDO getRoleByUuid(String uuid);

    @Select("SELECT * FROM fy_role WHERE name = #{name}")
    RoleDO getRoleByName(String name);

    @Select("SELECT * FROM fy_role ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
        // 直接返回所有的角色信息
        // order by 排序
        // limit 代表查询结果的最大返回记录数
        // offset 表示跳过开始的N条记录
    List<RoleDO> getRoleByAllList(Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_role
            WHERE uuid LIKE CONCAT('%',#{search},'%')
            OR name LIKE CONCAT('%',#{search},'%')
            OR display_name LIKE CONCAT('%',#{search},'%')
            OR permission LIKE CONCAT('%',#{search},'%')
            OR created_at LIKE CONCAT('%',#{search},'%')
            OR updated_at LIKE CONCAT('%',#{search},'%')
            OR created_user LIKE CONCAT('%',#{search},'%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<RoleDO> getRoleByFuzzy(String search, Integer limit, Integer page, String order);

    @Select(" SELECT * FROM fy_role WHERE permission LIKE CONCAT('%',#{otherSearch}, '%') ")
    RoleDO getRoleByPermissionName(String name);
}
