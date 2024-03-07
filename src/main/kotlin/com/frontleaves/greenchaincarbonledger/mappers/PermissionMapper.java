package com.frontleaves.greenchaincarbonledger.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * PermissionMapper
 * <hr/>
 * 用于权限信息的读取
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author DC_DC
 */
@Mapper
public interface PermissionMapper {
    @Select("SELECT name from fy_permission")
    List<String> getPermissionByName();
}
