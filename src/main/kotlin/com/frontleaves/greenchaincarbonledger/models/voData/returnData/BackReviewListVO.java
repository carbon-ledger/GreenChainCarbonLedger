package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * BackReviewListVO
 * <hr/>
 * 用于返回审核列表信息
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@Accessors(chain = true)
public class BackReviewListVO {
    public BackUserVO account;
    public String organizeName;
    public String legalRepresentativeName;
    public Timestamp applyTime;
}
