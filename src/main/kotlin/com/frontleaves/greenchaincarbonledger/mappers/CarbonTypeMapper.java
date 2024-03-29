package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 对于CarbonType表数据库操作语句
 * @author FALSHLACK
 */
@Mapper
public interface CarbonTypeMapper {
    @Select("SELECT * FROM fy_carbon_type WHERE name=#{name}")
    CarbonTypeDO getTypeByName(String name);
}
