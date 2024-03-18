package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.ApproveManageDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ReviewMapper
 * <hr/>
 * 用于审核的数据访问对象, 用于Mybatis
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Mapper
public interface ReviewMapper {

    /**
     * 获取组织审核信息‘
     *
     * @param organizeName 组织名称
     * @param creditCode 组织信用代码
     * @return ApproveOrganizeDO
     */
    @Select("SELECT * FROM fy_approve_organize WHERE organize_name = #{organizeName} OR organize_credit_code = #{creditCode} LIMIT 1")
    ApproveOrganizeDO getOrganizeApprove(String organizeName, String creditCode);

    /**
     * 设置组织审核信息
     *
     * @param newApproveOrganizeDO ApproveOrganizeDO
     */
    @Insert("""
    INSERT INTO fy_approve_organize (account_uuid, type, organize_name, organize_license_url, organize_credit_code, organize_registered_capital, organize_establishment_date, legal_representative_name, legal_representative_id, legal_id_card_front_url, legal_id_card_back_url, remarks)
    VALUES (#{accountUuid}, #{type}, #{organizeName}, #{organizeLicenseUrl}, #{organizeCreditCode}, #{organizeRegisteredCapital}, #{organizeEstablishmentDate}, #{legalRepresentativeName}, #{legalRepresentativeId}, #{legalIdCardFrontUrl}, #{legalIdCardBackUrl}, #{remarks})
    """)
    void setApproveOrganize(ApproveOrganizeDO newApproveOrganizeDO);

    /**
     * 获取管理员审核信息
     *
     * @param organizeName 组织名称
     * @return ApproveManageDO
     */
    @Select("SELECT * FROM fy_approve_manage WHERE organize_name = #{organizeName}")
    ApproveManageDO getAdminApprove(String organizeName);

    /**
     * 设置管理员审核信息
     *
     * @param newApproveManageDO ApproveManageDO
     */
    @Insert("""
    INSERT INTO fy_approve_manage (account_uuid, account_type, organize_name, organize_authorize_url, legal_representative_name, legal_representative_id, legal_id_card_front_url, legal_id_card_back_url, remarks)
    VALUES (#{accountUuid}, #{accountType}, #{organizeName}, #{organizeAuthorizeUrl}, #{legalRepresentativeName}, #{legalRepresentativeId}, #{legalIdCardFrontUrl}, #{legalIdCardBackUrl}, #{remarks})
    """)
    void setApproveAdmin(ApproveManageDO newApproveManageDO);
}
