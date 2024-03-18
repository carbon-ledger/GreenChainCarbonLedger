package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;


/**
 * 用于碳交易的查询和更改
 * <hr/>
 * 用于碳交易的查询和更改
 * @author FLAHSLACK
 */
@Mapper
public interface CarbonMapper {
    @Select("SELECT * FROM fy_carbon_quota WHERE organize_uuid = #{uuid} AND quota_year >= #{start} AND quota_year <= #{end}")
    ArrayList<CarbonQuotaDO> getQuotaListByOrganizeUuid(String uuid, String start, String end);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid = #{uuid}  ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByUuid(String uuid, String limit, String page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_status =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByStatus(String uuid, String search, String limit, String page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_summary =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportBySearch(String uuid, String search, String limit, String page, String order);
    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid}")
    List<CarbonAccountingDO> getAccountByUuid(String uuid);
}
