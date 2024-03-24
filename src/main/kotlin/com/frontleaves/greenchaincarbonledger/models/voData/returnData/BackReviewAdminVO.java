package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * BackReviewAdminVO
 * <hr/>
 * 用于返回检察审核信息
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
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
    public Timestamp applyTime;
    public Timestamp updatedAt;
    public String remarks;
}
