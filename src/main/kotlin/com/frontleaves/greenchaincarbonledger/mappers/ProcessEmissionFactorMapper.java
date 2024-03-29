package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.ProcessEmissionFactorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 对于排放因子表的数据库操作语句
 * @author FALSHLACK
 */
@Mapper
public interface ProcessEmissionFactorMapper {
    @Select("SELECT * FROM fy_process_emission_factor WHERE name=#{name}")
    ProcessEmissionFactorDO getFactorByName(String name);
}
