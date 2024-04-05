package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApproveMapper {

    /**
     * 获取通过审核的所有组织
     *
     * @return List<ApproveOrganizeDO>
     */
    @Select("SELECT * FROM fy_approve_organize WHERE certification_status = 1")
    List<ApproveOrganizeDO> getApproveOrganizeList();

    /**
     * 获取组织账户信息
     *
     * @param organizeUuid 组织uuid
     * @return ApproveOrganizeDO
     */
    @Select("SELECT * FROM fy_approve_organize WHERE account_uuid = #{organizeUuid}")
    ApproveOrganizeDO getOrganizeAccountByUuid(String organizeUuid);
}
