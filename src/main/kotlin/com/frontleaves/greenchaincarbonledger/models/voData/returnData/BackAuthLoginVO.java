package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

/**
 * BackAuthLoginVO
 * <hr/>
 * 用于返回用户登录的数据对象, 用于返回用户登录的数据
 *
 * @author FLASHLACK
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@Accessors(chain = true)
public class BackAuthLoginVO {
    public UserVO user;
    public RoleVO role;
    public String token;
    public PermissionVO permission;
    public Boolean recover;

    @Data
    @Accessors(chain = true)
    public static class UserVO {
        public String uuid;
        public String userName;
        public String realName;
        public String email;
        public String phone;
    }

    @Data
    @Accessors(chain = true)
    public static class RoleVO {
        public String name;
        public String displayName;
    }

    @Data
    @Accessors(chain = true)
    public static class PermissionVO {
        public ArrayList<String> rolePermission;
        public ArrayList<String> userPermission;
    }

}