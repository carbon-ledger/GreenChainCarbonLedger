package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ReviewAdminVO
 * <hr/>
 * 管理员审核VO，用于获取管理员审核信息，包括类型、组织名称、组织授权、法定代表人姓名、法定代表人身份证号、
 * 法定代表人身份证正面、法定代表人身份证反面
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class ReviewAdminVO {
    public Short type;
    public String organizeName;
    public String organizeAuthorize;
    public String legalRepresentativeName;
    public String legalRepresentativeId;
    public String legalIdCardFront;
    public String legalIdCardBack;
    public String remarks;
}
