package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonCompensationMaterialDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作用于CarbonCompensationMaterial表的数据库操作语句
 *
 * @author FLASHLACK
 */
@Mapper
public interface CarbonCompensationMaterialMapper {
    @Insert("""
            INSERT INTO fy_carbon_compensation_material(accounting_id, raw_material, created_at)
            VALUES (#{accountId},#{rawMaterial},now())
            """)
    Boolean initializationCarbonCompensationMaterial(String accountId,String rawMaterial);

    @Insert("""
            INSERT INTO fy_carbon_compensation_material(accounting_id, raw_material, electric_material, created_at)
            VALUES (#{accountId},#{rawMaterial},#{electricMaterial},now())
            """)
    Boolean insertCarbonCompensationMaterial(CarbonCompensationMaterialDO carbonCompensationMaterialDO);

}
