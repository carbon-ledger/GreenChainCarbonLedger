package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 用于存放管理员添加用户信息后的信息返回值
 * <hr/>
 * 用于存放管理员添加用户信息后的信息返回值
 * @author DC_DC
 * @since 2024-03-13
 */
@Data
@Accessors(chain = true)
public class BackAddUserVO {
    String uuid;
    String userName;
    String nickName;
    String realName;
    String password;
    String email;
    String phone;
}

