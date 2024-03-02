package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

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
@Accessors(chain = true)
public class BackUserCurrentVO {
    UserVO user;
    String role;
    PermissionVO permission;

    @Data
    @Accessors(chain = true)
    public static class UserVO {
        String uuid;
        String userName;
        String realName;
        String email;
        String phone;
    }

    @Data
    @Accessors(chain = true)
    public static class PermissionVO {
        public ArrayList<String> rolePermission;
        public ArrayList<String> userPermission;
    }
}
