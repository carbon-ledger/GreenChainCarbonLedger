package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * 用于碳交易的查询和更改
 * <hr/>
 * 用于碳交易的查询和更改
 * @author FLAHSLACK
 */
@Mapper
public interface CarbonMapper {
    @Select("SELECT * FROM fy_carbon_quota WHERE organize_uuid = #{uuid} AND quota_year = #{year}")
    CarbonQuotaDO[] getQuotaByUuidYear(String uuid, Integer year);
}
