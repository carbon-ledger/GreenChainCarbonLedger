package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.UserEditVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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


    /**
     * 获取用户可用列表
     * <hr/>
     * 获取用户可用列表，即没有被删除的用户，没有被封禁的用户
     * 如果删除时间不为空，则该用户存在或者没有被封禁
     *
     * @param limit 限制
     * @param page  页数
     * @param order 排序
     * @return {@link List<UserDO>}
     */
    @Select("SELECT * FROM fy_user WHERE deleted_at IS NULL and ban = 0 ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
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
    boolean updateUserForceByUuid(String userUuid, String userName, String nickName, String realName, String avatar, String email, String phone);

    @Update("UPDATE fy_user SET ban = 1 WHERE uuid = #{uuid}")
    Boolean banUser(String banUuid);

    @Update("UPDATE fy_user SET deleted_at = NOW() WHERE uuid = #{userUuid}")
    Boolean forceLogout(String userUuid);

    @Insert("INSERT INTO fy_user(uuid, user_name, `real_name`, email, phone, password, role) VALUES (#{addUuid}, #{addUsername}, #{addRealname}, #{addEmail}, #{addPhone}, #{addPassword}, #{defaultUuid})")
    boolean addAccount(String addUuid, String addUsername, String addRealname, String addEmail, String addPhone, String addPassword, String defaultUuid);
}
