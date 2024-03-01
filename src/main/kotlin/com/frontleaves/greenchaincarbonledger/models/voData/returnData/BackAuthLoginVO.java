package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
@Data
@Accessors(chain = true)
public class BackAuthLoginVO {
    UserVO user;
    String role;
    String token;
    PermissionVO permission;
    Boolean recover;

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
        ArrayList<String> rolePermission;
        ArrayList<String> userPermission;
    }
}