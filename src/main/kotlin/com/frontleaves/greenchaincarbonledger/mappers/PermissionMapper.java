package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("""
            SELECT name FROM fy_permission WHERE description LIKE CONCAT('%',#{search},'%')
                ORDER BY ${order}
            """)
    List<String> getNameBySearch(String search, String order);
    @Select("SELECT * FROM fy_permission ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<PermissionDO> getPermissionListByAll(Integer limit,Integer page,String order);
}
