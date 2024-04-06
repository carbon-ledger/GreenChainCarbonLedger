package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.DesulfurizationFactorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author DC_DC
 * Date: 2024/3/27/19:27
 */

@Mapper
public interface DesulfurizationFactorMapper {
    @Select("SELECT * FROM fy_desulfurization_factor WHERE name = #{name}")
    DesulfurizationFactorDO getDesFactorByName(String name);

    @Select("SELECT * FROM fy_desulfurization_factor")
    List<DesulfurizationFactorDO> getDesulfurizationFactorList();
}
