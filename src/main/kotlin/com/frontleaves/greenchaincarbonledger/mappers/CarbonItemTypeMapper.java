package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonItemTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对于CarbonItemType表的数据库语句
 * @author FLASHLACK
 */
@Mapper
public interface CarbonItemTypeMapper {
    @Select("SELECT * FROM fy_carbon_item_type WHERE name=#{Name}")
    CarbonItemTypeDO getCarbonItemTypeByName(String name);

    @Select("SELECT * FROM fy_carbon_item_type")
    List<CarbonItemTypeDO> getCarbonItemTypeList();
}
