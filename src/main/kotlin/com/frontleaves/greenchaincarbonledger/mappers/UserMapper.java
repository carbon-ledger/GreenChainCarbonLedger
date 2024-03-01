package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import org.apache.ibatis.annotations.*;

/**
 * UserMapper
 * <hr/>
 * 用于用户的数据访问对象, 用于Mybatis
 *
 * @author xiao_feng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM fy_user WHERE uuid = #{uuid}")
    UserDO getUserByUuid(String uuid);

    @Select("SELECT * FROM fy_user WHERE user_name = #{username}")
    UserDO getUserByUsername(String username);

    @Select("SELECT * FROM fy_user WHERE email = #{email}")
    UserDO getUserByEmail(String email);

    @Select("SELECT * FROM fy_user WHERE phone = #{phone}")
    UserDO getUserByPhone(String phone);

    @Select("SELECT * FROM fy_user WHERE real_name = #{realname}")
    UserDO getUserByRealname(String realname);

    @Insert("""
            INSERT INTO fy_user (uuid, user_name, real_name, email, phone, password)
                VALUES (#{uuid}, #{userName}, #{realName}, #{email}, #{phone}, #{password})
            """)
    boolean createUser(UserDO newUserDO);

    @Update("UPDATE fy_user SET password = #{password} WHERE uuid = #{uuid}")
    boolean updateUserPassword(UserDO getUserDO);

    //软删除加上时间戳
    @Update("UPDATE fy_user SET deleted_at = NOW() WHERE uuid = #{uuid}")
    boolean userAccountDeletion (String uuid);

    @Update("UPDATE fy_user SET deleted_at = null WHERE uuid = #{uuid}")
    boolean userAccountDistanceDeletion(String uuid);


    @Delete("DELETE FROM fy_user WHERE uuid = #{uuid}")
    boolean deleteUserAccount(String uuid);


}
