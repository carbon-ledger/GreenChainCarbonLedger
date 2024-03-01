package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * PermissionDO
 * <hr/>
 * 用于权限的数据对象, 用于存储权限的数据
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
public class PermissionDO {
    public Long pid;
    public String name;
    public String description;
}
