package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

/**
 * 用于存放返回用户当前角色信息
 * <hr/>
 * 用于存放返回用户当前角色信息
 * @author FLASHLACK
 * @since 2024-03-05
 */
@Data
@Accessors(chain = true)
public class BackRoleCurrentVO {
    String uuid;
    String name;
    String displayName;
    Permission permission;

    @Data
    @Accessors (chain = true)
    public static class Permission{
        public ArrayList<String> rolePermission;
    }


}
