package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * CarbonAccountingMapper
 * <hr/>
 * 用于组织碳核算的数据访问对象, 用于Mybatis
 *
 * @author DC_DC
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Mapper
public interface CarbonAccountingMapper {
    @Select("SELECT * FROM fy_carbon_accounting ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonAccountingDO> getOrganizeCarbonAccounting(String organizeUuid, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid} ")
    List<CarbonAccountingDO> getCarbonAccountingListByUuid(String uuid);

    @Insert("""
            INSERT INTO fy_carbon_accounting(organize_uuid, report_id, emission_type, emissions_volume, emission_amount, accounting_period, data_verification_status, verifier_uuid, verification_notes, blockchain_tx_id, created_at, updated_at) 
            VALUES (#{uuid},#{reportId},#{type},0,0,#{period},#{status},0,0,0,now(),0)
            """)
    Boolean initializationCarbonAccounting(String uuid,String reportId,String type,String period,String status);
    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid} ORDER BY id desc")
    List<CarbonAccountingDO> getCarbonAccountingListByUuidDesc(String uuid);
    @Update("UPDATE fy_carbon_accounting SET emissions_volume =#{emissionsVolume} AND emission_amount=#{emissionAmount} AND updated_at =now() WHERE id=#{id}")
    Boolean updateEmissionByUuidId(String emissionsVolume,Double emissionAmount,String id);
}
