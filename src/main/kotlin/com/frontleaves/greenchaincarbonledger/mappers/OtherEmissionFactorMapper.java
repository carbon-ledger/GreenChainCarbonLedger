package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.OtherEmissionFactorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 对OtherEmissionFactor表的数据库操作
 * @author FALSHLACK
 */
@Mapper
public interface OtherEmissionFactorMapper {
    @Select("SELECT * FROM fy_other_emission_factor WHERE name=#{name}")
    OtherEmissionFactorDO getFactorByName(String name);
}
