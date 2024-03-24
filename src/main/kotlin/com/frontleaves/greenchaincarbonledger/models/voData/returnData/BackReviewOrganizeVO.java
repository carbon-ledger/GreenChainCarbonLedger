package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;


/**
 * BackReviewOrganizeVO
 * <hr/>
 * 用于返回组织审核信息
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackReviewOrganizeVO {
    public BackUserVO account;
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
    public Timestamp applyTime;
    public Timestamp updatedAt;
    public String remarks;
}
