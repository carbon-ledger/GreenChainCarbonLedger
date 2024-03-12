package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserEditVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
    @Select("SELECT * FROM fy_user WHERE uuid = #{uuid} LIMIT 1")
    UserDO getUserByUuid(String uuid);

    @Select("SELECT * FROM fy_user WHERE user_name = #{username}")
    UserDO getUserByUsername(String username);

    @Select("SELECT * FROM fy_user WHERE email = #{email}")
    UserDO getUserByEmail(String email);

    @Select("SELECT * FROM fy_user WHERE phone = #{phone}")
    UserDO getUserByPhone(String phone);

    @Select("SELECT * FROM fy_user WHERE real_name = #{realname}")
    UserDO getUserByRealname(String realname);

    @Select("SELECT * FROM fy_user WHERE invite = #{invite}")
    Boolean getUserByInvite(String invite);

    @Insert("""
            INSERT INTO fy_user (uuid, user_name, real_name, email, phone, password, role)
                VALUES (#{uuid}, #{userName}, #{realName}, #{email}, #{phone}, #{password}, #{role})
            """)
    boolean createUser(UserDO newUserDO);

    @Update("UPDATE fy_user SET password = #{password}, updated_at = NOW() WHERE uuid = #{uuid}")
    boolean updateUserPassword(UserDO getUserDO);

    //软删除加上时间戳
    @Update("UPDATE fy_user SET deleted_at = NOW() WHERE uuid = #{uuid}")
    boolean userAccountDeletion(String uuid);

    @Update("UPDATE fy_user SET deleted_at = null WHERE uuid = #{uuid}")
    boolean userAccountDistanceDeletion(String uuid);


    @Delete("DELETE FROM fy_user WHERE uuid = #{uuid}")
    boolean deleteUserAccount(String uuid);

    @Select("""
            SELECT * FROM fy_user
            WHERE user_name LIKE CONCAT('%', #{search}, '%')
            OR nick_name LIKE CONCAT('%', #{search}, '%')
            OR real_name LIKE CONCAT('%', #{search}, '%')
            OR email LIKE CONCAT('%', #{search}, '%')
            OR phone LIKE CONCAT('%', #{search}, '%')
            OR role LIKE CONCAT('%', #{search}, '%')
            OR avatar LIKE CONCAT('%', #{search}, '%')
            OR invite LIKE CONCAT('%', #{search}, '%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<UserDO> getUserFuzzy(String search, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_user WHERE ban = 0 ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
        // 取出没有被ban的用户（ban=0）
    List<UserDO> getUserByUnbanlist(Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_user WHERE ban = 1 ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<UserDO> getUserByBanlist(Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_user WHERE deleted_at IS NULL ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
        // 如果删除时间不为空，则该用户存在
    List<UserDO> getUserByAvailablelist(Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_user ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
        // 直接返回所有的用户信息
        // order by 排序
        // limit 代表查询结果的最大返回记录数
        // offset 表示跳过开始的N条记录
    List<UserDO> getUserByAlllist(Integer limit, Integer page, String order);

    @Update("""
            UPDATE fy_user
            SET nick_name = #{nickName}, avatar = #{avatar}, email = #{email}, phone = #{phone}, updated_at = NOW()
            WHERE uuid = #{getAuthorizeUserUuid}
            """)
    boolean updateUserByUuid(String getAuthorizeUserUuid, UserEditVO userEditVO);

    @Select("""
            SELECT role FROM fy_user
            WHERE user_name LIKE CONCAT('%', #{search}, '%')
            OR nick_name LIKE CONCAT('%', #{search}, '%')
            OR real_name LIKE CONCAT('%', #{search}, '%')
            OR email LIKE CONCAT('%', #{search}, '%')
            OR phone LIKE CONCAT('%', #{search}, '%')
            OR role LIKE CONCAT('%', #{search}, '%')
            OR avatar LIKE CONCAT('%', #{search}, '%')
            OR invite LIKE CONCAT('%', #{search}, '%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
                        """)
    List<String> getRoleByAllList(String search, Integer limit, Integer page, String order);

    @Update("""
            UPDATE fy_user
            SET user_name = #{userName},nick_name = #{nickName}, real_name = #{realName}, avatar = #{avatar}, email = #{email}, phone = #{phone}, updated_at = NOW()
            WHERE uuid = #{userUuid}
            """)
    boolean updateUserForceByUuid(String userUuid, String userName,String realName,String nickName, String avatar,String email, String phone);

    @Delete("DELETE FROM fy_user WHERE uuid = #{uuid}")
    Boolean forceLogout(String uuid);
}
