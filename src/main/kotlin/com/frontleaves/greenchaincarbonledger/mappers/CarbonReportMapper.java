package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用于对于碳核算报告的操作
 * <hr/>
 * 作用于CarbonReport的数据库语句
 *
 * @author FLASHLACK
 */
@Mapper
public interface CarbonReportMapper {
    @Insert("""
            INSERT INTO fy_carbon_report(organize_uuid, report_title, report_type, accounting_period, total_emission, report_status, report_summary, created_at)
            VALUES (#{uuid},#{title},#{type},#{period},0,#{status},#{summary},now())
            """)
    Boolean initializationReportMapper(String uuid,String title,String type,String period,String status,String summary);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} ORDER BY id desc")
    List<CarbonReportDO> getReportListByUuid (String uuid);

    @Update("UPDATE fy_carbon_report SET total_emission=#{totalEmission} AND report_status=#{status} AND updated_at=now() WHERE id=#{id}")
    Boolean updateEmissionById(Double totalEmission,String status,String id);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid = #{organizeUserUuid} ORDER BY id DESC LIMIT 1")
    CarbonReportDO getLastReportByUuid(String organizeUserUuid);
}
