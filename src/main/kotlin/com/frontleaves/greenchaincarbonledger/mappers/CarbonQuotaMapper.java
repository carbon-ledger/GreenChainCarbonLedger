package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * CarbonQuota表的数据库语句
 *
 * @author FLASHLACK
 */
@Mapper
public interface CarbonQuotaMapper {
    @Select("SELECT * FROM fy_carbon_quota WHERE quota_year=#{year} AND organize_uuid=#{uuid}")
    CarbonQuotaDO getCarbonQuota(Integer year, String uuid);

    @Update("UPDATE fy_carbon_quota SET total_quota=#{totalQuota}, updated_at=now() WHERE organize_uuid=#{uuid} AND quota_year=#{year}")
    Boolean finishCarbonTrade(Double totalQuota, String uuid, Integer year);

    @Insert("""
            INSERT INTO fy_carbon_quota (uuid, organize_uuid, quota_year, total_quota, allocated_quota, used_quota, compliance_status, allocation_date, audit_log, created_at, updated_at)
                VALUES ( #{uuid}, #{organizeUuid}, #{quotaYear}, #{totalQuota}, #{allocatedQuota},#{usedQuota}, #{complianceStatus}, #{allocationDate}, #{auditLog},now(),#{updatedAt})
            """)
    boolean createCarbonQuota(CarbonQuotaDO carbonQuotaDO);

    @Update("UPDATE fy_carbon_quota SET total_quota=#{totalQuota}, compliance_status=#{complianceStatus}, audit_log=#{auditLog}, updated_at=now() WHERE uuid = #{uuid}")
    Boolean editCarbonQuota(CarbonQuotaDO carbonQuotaDO);
}
