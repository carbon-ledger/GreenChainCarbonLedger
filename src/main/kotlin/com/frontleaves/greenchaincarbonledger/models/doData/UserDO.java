package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * UserDO - 用户数据对象
 * <hr/>
 * 用于用户的数据对象, 用于存储用户的数据
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class UserDO {
    public Long uid;
    public String uuid;
    public String userName;
    public String nickName;
    public String realName;
    public String email;
    public String phone;
    public String avatar;
    public String password;
    public String role;
    public String permission;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public Boolean ban;
    public String invite;
    public Timestamp deletedAt;
}
