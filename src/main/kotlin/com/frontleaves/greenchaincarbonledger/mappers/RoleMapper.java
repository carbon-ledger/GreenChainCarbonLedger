package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.google.gson.Gson;
import org.apache.ibatis.annotations.*;

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

    @Insert("INSERT INTO fy_role (uuid, name, display_name, permission, created_user) VALUES (#{uuid}, #{name}, #{displayName}, #{permission}, #{userUuid})")
    Boolean insertRole(String uuid, String name, String displayName, String permission, String userUuid);

    @Update("Update fy_role SET name = #{name}, display_name = #{displayName}, permission = #{permission} WHERE uuid = #{roleUuid}")
    Boolean updateRole(String name, String displayName, String permission, String roleUuid);

    @Delete("DELETE FROM fy_role WHERE uuid = #{uuid}")
    Boolean deleteRole(String uuid);
}
