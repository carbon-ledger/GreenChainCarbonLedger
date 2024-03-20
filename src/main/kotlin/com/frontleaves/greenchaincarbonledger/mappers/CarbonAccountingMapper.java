package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
