package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * ApproveManageDO
 * <hr/>
 * 用于获取管理审核信息，包括类型、组织名称、组织授权、法定代表人姓名、法定代表人身份证号、
 * 法定代表人身份证正面、法定代表人身份证反面
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@Accessors(chain = true)
public class ApproveManageDO {
    public Long id;
    public String accountUuid;
    public Short accountType;
    public String organizeName;
    public String organizeAuthorizeUrl;
    public String legalRepresentativeName;
    public String legalRepresentativeId;
    public String legalIdCardFrontUrl;
    public String legalIdCardBackUrl;
    public Short certificationStatus;
    public Timestamp applyTime;
    public Timestamp approveTime;
    public Timestamp updatedAt;
    public String remarks;
    public String approveUuid;
    public String approveRemarks;
}
