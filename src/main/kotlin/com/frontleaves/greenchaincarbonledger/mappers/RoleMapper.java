package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.google.gson.Gson;
import org.apache.ibatis.annotations.*;

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
            WHERE name LIKE CONCAT('%',#{search},'%')
            OR display_name LIKE CONCAT('%',#{search},'%')
            OR permission LIKE CONCAT('%',#{search},'%')
            OR created_at LIKE CONCAT('%',#{search},'%')
            OR updated_at LIKE CONCAT('%',#{search},'%')
            OR created_user LIKE CONCAT('%',#{search},'%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<RoleDO> getRoleByFuzzy(String search, Integer limit, Integer page, String order);

    @Select(" SELECT * FROM fy_role WHERE permission LIKE CONCAT('%',#{otherSearch}, '%') ")
    List<RoleDO> getRoleByPermissionName(String name);

    @Insert("INSERT INTO fy_role (uuid, name, display_name, permission, created_user) VALUES (#{uuid}, #{name}, #{displayName}, #{permission}, #{userUuid})")
    Boolean insertRole(String uuid, String name, String displayName, String permission, String userUuid);

    @Update("Update fy_role SET name = #{name}, display_name = #{displayName}, permission = #{permission} WHERE uuid = #{roleUuid}")
    Boolean updateRole(String name, String displayName, String permission, String roleUuid);

    @Delete("DELETE FROM fy_role WHERE uuid = #{uuid}")
    Boolean deleteRole(String uuid);

    @Select("SELECT name FROM fy_role WHERE uuid = #{role}")
    String getDisplayNameByRole(String role);
}
