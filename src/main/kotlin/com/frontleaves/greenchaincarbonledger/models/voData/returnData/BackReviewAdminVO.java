package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

/**
 * BackReviewAdminVO
 * <hr/>
 * 用于返回检察审核信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class BackReviewAdminVO {
    public BackUserVO account;
    public Short accountType;
    public String organizeName;
    public String organizeAuthorizeUrl;
    public String legalRepresentativeName;
    public String legalRepresentativeId;
    public String legalIdCardFrontUrl;
    public String legalIdCardBackUrl;
    public Short certificationStatus;
    public Date applyTime;
    public String remarks;
}
