package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

import java.sql.Timestamp;

/**
 * RoleDO
 * <hr/>
 * 角色数据对象, 用于存储角色信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
public class RoleDO {
    public Short id;
    public String uuid;
    public String name;
    public String displayName;
    public String permission;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public String createdUser;
}
