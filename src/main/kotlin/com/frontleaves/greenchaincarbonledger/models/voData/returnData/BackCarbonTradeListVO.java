package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放返回自己组织碳交易发布信息列表的值
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class BackCarbonTradeListVO {
    BackUserVO organize;
    String quotaAmount;
    String pricePerUnit;
    String description;
    String status;
}
