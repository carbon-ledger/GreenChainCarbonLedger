package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ReviewOrganizeVO
 * <hr/>
 * 组织审核VO，用于获取组织审核信息，包括组织名称、信用代码、营业执照、注册资本、成立日期、法定代表人姓名、
 * 法定代表人身份证号、法定代表人身份证正面、法定代表人身份证反面
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class ReviewOrganizeVO {
    public String organizeName;
    public String creditCode;
    public String license;
    public String registeredCapital;
    public String establishmentDate;
    public String legalRepresentativeName;
    public String legalRepresentativeId;
    public String legalIdCardFront;
    public String legalIdCardBack;
}
