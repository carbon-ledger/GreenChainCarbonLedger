package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * UserMapper
 * <hr/>
 * 用于用户的数据访问对象, 用于Mybatis
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_feng
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM dc_carbon.fy_user WHERE uuid = #{uuid}")
    UserDO getUserByUuid(String uuid);

    @Select("SELECT * FROM dc_carbon.fy_user WHERE user_name = #{username}")
    UserDO getUserByUsername(String username);

    @Select("SELECT * FROM dc_carbon.fy_user WHERE email = #{email}")
    UserDO getUserByEmail(String email);

    @Select("SELECT * FROM dc_carbon.fy_user WHERE phone = #{phone}")
    UserDO getUserByPhone(String phone);

    @Select("SELECT * FROM dc_carbon.fy_user WHERE real_name = #{realname}")
    UserDO getUserByRealname(String realname);

    @Insert("""
        INSERT INTO dc_carbon.fy_user (uuid, user_name, real_name, email, phone, password)
            VALUES (#{uuid}, #{userName}, #{realName}, #{email}, #{phone}, #{password})
        """)
    boolean createUser(UserDO newUserDO);
}
