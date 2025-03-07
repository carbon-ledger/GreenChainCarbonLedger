package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.ApproveManageDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;

/**
 * ReviewMapper
 * <hr/>
 * 用于审核的数据访问对象, 用于Mybatis
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Mapper
public interface ReviewMapper {

    /**
     * 获取组织审核信息‘
     *
     * @param organizeName 组织名称
     * @param creditCode   组织信用代码
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
            INSERT INTO fy_approve_organize (
                account_uuid,
                type,
                organize_name,
                organize_license_url,
                organize_credit_code,
                organize_registered_capital,
                organize_establishment_date,
                legal_representative_name,
                legal_representative_id,
                legal_id_card_front_url,
                legal_id_card_back_url,
                account_bank,
                account_number,
                remarks
            )
            VALUES (
                #{accountUuid},
                #{type},
                #{organizeName},
                #{organizeLicenseUrl},
                #{organizeCreditCode},
                #{organizeRegisteredCapital},
                #{organizeEstablishmentDate},
                #{legalRepresentativeName},
                #{legalRepresentativeId},
                #{legalIdCardFrontUrl},
                #{legalIdCardBackUrl},
                #{accountBank},
                #{accountNumber},
                #{remarks}
            )
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
            INSERT INTO fy_approve_manage (
                account_uuid,
                account_type,
                organize_name,
                organize_authorize_url,
                legal_representative_name,
                legal_representative_id,
                legal_id_card_front_url,
                legal_id_card_back_url,
                remarks
            )
            VALUES (
                #{accountUuid},
                #{accountType},
                #{organizeName},
                #{organizeAuthorizeUrl},
                #{legalRepresentativeName},
                #{legalRepresentativeId},
                #{legalIdCardFrontUrl},
                #{legalIdCardBackUrl},
                #{remarks}
            )
            """)
    void setApproveAdmin(ApproveManageDO newApproveManageDO);

    /**
     * 获取组织审核信息
     * <hr/>
     * 根据Id获取组织审核相关的信息，获取全部的数据，若没有数据返回 null
     *
     * @param checkId 审核id
     * @return ApproveOrganizeDO
     */
    @Select("SELECT * FROM fy_approve_organize WHERE id = #{id}")
    ApproveOrganizeDO getOrganizeApproveById(long checkId);

    @Update("UPDATE fy_approve_organize SET certification_status = #{allow}, approve_uuid = #{approveRemarksUuid}, approve_remarks = #{approveRemarks}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateReviewOrganizeCheck(short allow, String approveRemarksUuid, String approveRemarks, Timestamp updatedAt, long id);

    @Select("SELECT * FROM fy_approve_manage WHERE id = #{id}")
    ApproveManageDO getAdminApproveById(long id);

    @Update("UPDATE fy_approve_manage SET certification_status = #{value}, approve_uuid = #{approveRemarksUuid}, approve_remarks = #{approveRemarks}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateReviewAdminCheck(short value, String approveRemarksUuid, String approveRemarks, Timestamp updatedAt, long id);

    /**
     * 更新组织审核信息
     *
     * @param getApproveOrganizeDO ApproveOrganizeDO
     */
    @Update("""
            UPDATE fy_approve_organize SET
                account_uuid = #{accountUuid},
                type = #{type},
                organize_name = #{organizeName},
                organize_license_url = #{organizeLicenseUrl},
                organize_credit_code = #{organizeCreditCode},
                organize_registered_capital = #{organizeRegisteredCapital},
                organize_establishment_date = #{organizeEstablishmentDate},
                legal_representative_name = #{legalRepresentativeName},
                legal_representative_id = #{legalRepresentativeId},
                legal_id_card_front_url = #{legalIdCardFrontUrl},
                legal_id_card_back_url = #{legalIdCardBackUrl},
                certification_status = #{certificationStatus},
                account_bank = #{accountBank},
                account_number = #{accountNumber},
                apply_time = #{applyTime},
                approve_time = #{approveTime},
                updated_at = #{updatedAt},
                remarks = #{remarks},
                approve_uuid = #{approveUuid},
                approve_remarks = #{approveRemarks}
            WHERE id = #{id}
            """)
    void updateApproveOrganize(ApproveOrganizeDO getApproveOrganizeDO);

    @Update("""
            UPDATE fy_approve_manage SET
                account_uuid = #{accountUuid},
                account_type = #{accountType},
                organize_name = #{organizeName},
                organize_authorize_url = #{organizeAuthorizeUrl},
                legal_representative_name = #{legalRepresentativeName},
                legal_representative_id = #{legalRepresentativeId},
                legal_id_card_front_url = #{legalIdCardFrontUrl},
                legal_id_card_back_url = #{legalIdCardBackUrl},
                certification_status = #{certificationStatus},
                apply_time = #{applyTime},
                approve_time = #{approveTime},
                updated_at = #{updatedAt},
                remarks = #{remarks},
                approve_uuid = #{approveUuid},
                approve_remarks = #{approveRemarks}
            WHERE id = #{id}
            """)
    void updateApproveAdmin(ApproveManageDO getApproveAdminById);

    @Select("SELECT * FROM fy_approve_organize WHERE certification_status = 0 ORDER BY id")
    List<ApproveOrganizeDO> getApproveOrganizeList();

    @Select("SELECT * FROM fy_approve_organize WHERE account_uuid = #{uuid} LIMIT 1")
    ApproveOrganizeDO getApproveOrganizeByUuid(String uuid);

    @Select("SELECT * FROM fy_approve_manage WHERE account_uuid = #{uuid} LIMIT 1")
    ApproveManageDO getApproveAdminByUuid(String uuid);

    @Select("SELECT * FROM fy_approve_manage WHERE certification_status = 0 ORDER BY id")
    List<ApproveManageDO> getApproveAdminList();
}
