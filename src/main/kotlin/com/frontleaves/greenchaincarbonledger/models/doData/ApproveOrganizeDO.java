package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * ApproveOrganizeDO
 * <hr/>
 * 用于获取组织审核信息，包括组织名称、信用代码、营业执照、注册资本、成立日期、法定代表人姓名、
 * 法定代表人身份证号、法定代表人身份证正面、法定代表人身份证反面
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class ApproveOrganizeDO {
    public Long id;
    public String accountUuid;
    public Short type;
    public String organizeName;
    public String organizeLicenseUrl;
    public String organizeCreditCode;
    public String organizeRegisteredCapital;
    public Timestamp organizeEstablishmentDate;
    public String legalRepresentativeName;
    public String legalRepresentativeId;
    public String legalIdCardFrontUrl;
    public String legalIdCardBackUrl;
    public Short certificationStatus;
    public String accountBank;
    public String accountNumber;
    public Timestamp applyTime;
    public Timestamp approveTime;
    public Timestamp updatedAt;
    public String remarks;
    public String approveUuid;
    public String approveRemarks;
}
