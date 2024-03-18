package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

/**
 * BackReviewListVO
 * <hr/>
 * 用于返回审核列表信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class BackReviewListVO {
    public BackUserVO account;
    public Short type;
    public String organizeName;
    public String legalRepresentativeName;
    public Date applyTime;
}
