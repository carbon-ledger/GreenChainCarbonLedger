package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

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
public class UserDO {
    public Long uid;
    public String uuid;
    public String userName;
    public String email;
    public String phone;
    public String nickName;
    public String realName;
    public String password;
    public String oldPassword;
    public String avatar;
    public Short status;
    public Boolean emailVerify;
    public Boolean phoneVerify;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
