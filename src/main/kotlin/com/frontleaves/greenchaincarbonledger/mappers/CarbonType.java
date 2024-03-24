package com.frontleaves.greenchaincarbonledger.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 对于CarbonType的操作
 * @author FLASHLACK
 */
@Mapper
public interface CarbonType {
    @Select("SELECT * FROM fy_carbon_type WHERE name=#{}")

}
