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

    @Insert("""
            INSERT INTO fy_carbon_accounting(organize_uuid, report_id, emission_type, emission_amount, accounting_period, data_verification_status, created_at)
            VALUES (#{organizeUuid},#{reportId},#{emissionType},0,#{accountingPeriod},#{dataVerificationStatus},now())
            """)
    Boolean insertCarbonAccounting(CarbonAccountingDO carbonAccountingDO);

    @Update("UPDATE fy_carbon_accounting SET emission_volume =#{emissionsVolume} , emission_amount=#{emissionAmount} , updated_at =now() WHERE id=#{id}")
    Boolean updateEmissionByUuidId(String emissionsVolume,Double emissionAmount,Long id);

    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid} ORDER BY id DESC LIMIT 1")
    CarbonAccountingDO getLastCarbonAccountingByUuid(String uuid);
}
