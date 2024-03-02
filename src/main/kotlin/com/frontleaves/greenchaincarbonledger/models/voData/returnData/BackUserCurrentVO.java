package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;

/**
 * 返回当前登录用户的详细信息的值对象。
 * 该值对象包括用户的用户名、真实姓名、电子邮件地址、联系电话、角色和权限信息。
 * 通常用于个人资料页面，允许用户查看其个人信息。
 *
 * @author FLASHLACK
 * @version V1.0
 * @since  V1.0
 */

@Data
public class BackUserCurrentVO {
    String userName;
    String realName;
    String email;
    String phone;
    String role;
    String permission;
}
