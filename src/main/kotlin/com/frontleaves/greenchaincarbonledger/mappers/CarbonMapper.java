package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarbonMapper {
    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid = #{uuid}  ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByUuid(String uuid, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_status =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByStatus(String uuid, String search, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_summary =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportBySearch(String uuid, String search, Integer limit, Integer page, String order);
    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid}")
    List<CarbonAccountingDO> getAccountByUuid(String uuid);
}
