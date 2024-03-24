package com.frontleaves.greenchaincarbonledger.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作用于CarbonReport的数据库语句
 *
 * @author FLASHLACK
 */
@Mapper
public interface CarbonReportMapper {
    @Insert("""
            INSERT INTO fy_carbon_report(organize_uuid, report_title, report_type, accounting_period, total_emission, report_status, verifier_uuid, verification_date, report_summary, blockchain_tx_id, created_at, updated_at) 
            VALUES (#{uuid},#{title},#{type},#{period},0,#{status},0,0,#{summary},0,now(),0)
            """)
    Boolean initializationReportMapper(String uuid,String title,String type,String period,String status,String summary);
}
