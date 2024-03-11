package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放管理员修改用户信息后的信息返回值
 * <hr/>
 * 用于存放管理员修改用户信息后的信息返回值
 * @author FLASHLACK
 * @since 2024-03-07
 */
@Data
@Accessors(chain = true)
public class BackUserForceEditVO {
    String uuid;
    String userName;
    String nickName;
    String realName;
    String avatar;
    String email;
    String phone;
    String createdAt;
    String updatedAt;
}
