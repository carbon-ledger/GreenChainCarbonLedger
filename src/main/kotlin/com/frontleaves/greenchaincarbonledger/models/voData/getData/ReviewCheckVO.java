package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ReviewCheckVO
 * <hr/>
 * 用于获取账户审核信息，包括备注
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class ReviewCheckVO {
    public String remark;
    public Boolean allow;
}
