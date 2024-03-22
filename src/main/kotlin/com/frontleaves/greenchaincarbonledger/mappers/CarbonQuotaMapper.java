package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * CarbonQuota表的数据库语句
 * @author FLASHLACK
 */
@Mapper
public interface CarbonQuotaMapper {
    @Select("SELECT * FROM fy_carbon_quota WHERE quota_year=#{year} AND organize_uuid=#{uuid}")
    CarbonQuotaDO getCarbonQuota(Integer year,String uuid);
    @Update("UPDATE fy_carbon_quota SET total_quota=#{totalQuota} AND updated_at=now() WHERE organize_uuid=#{uuid} AND quota_year=#{year}")
    Boolean finishCarbonTrade(Double totalQuota,String uuid,Integer year);
    @Update("UPDATE fy_carbon_quota SET total_quota=#{totalQuota} AND compliance_status=#{status} AND audit_log=#{auditLog} AND updated_at=now() WHERE organize_uuid=#{uuid} AND quota_year=#{year}")
    Boolean editCarbonQuota(String uuid,Integer year,Double totalQuota,boolean status,String auditLog);
}
