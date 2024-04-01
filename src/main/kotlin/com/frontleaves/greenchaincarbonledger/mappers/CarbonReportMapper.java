package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
            INSERT INTO fy_carbon_report(organize_uuid, report_title, report_type, accounting_period, total_emission, report_status, report_summary)
            VALUES (#{organizeUuid},#{reportTitle},#{reportType},#{accountingPeriod},0,#{reportStatus},#{reportSummary})
            """)
    Boolean insertReportMapper(CarbonReportDO carbonReportDO);

    @Update("UPDATE fy_carbon_report SET total_emission=#{totalEmission} , report_status=#{status},list_of_reports=#{listOfReports} , updated_at=now() WHERE id=#{id}")
    Boolean updateEmissionById(Double totalEmission, String status, String id, String listOfReports);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid = #{organizeUserUuid} ORDER BY id DESC LIMIT 1")
    CarbonReportDO getLastReportByUuid(String organizeUserUuid);
}
