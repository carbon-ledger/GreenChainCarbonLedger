package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用于碳交易发布的数据表，作用与MySql
 * @author FLASHLACK
 */
@Mapper
public interface CarbonTradeMapper {
    @Select("SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}")
    List<CarbonTradeDO> getTradeListByUuid(String uuid);

}
