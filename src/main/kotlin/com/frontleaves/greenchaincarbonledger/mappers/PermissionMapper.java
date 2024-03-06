package com.frontleaves.greenchaincarbonledger.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author FLASHLACK
 */
@Mapper
public interface PermissionMapper {
    @Select("SELECT name FROM fy_permission WHERE description LIKE CONCAT('%',#{search},'%')" +
            "ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<String> getNameBySearch(String search, Integer limit, Integer page, String order);
}
