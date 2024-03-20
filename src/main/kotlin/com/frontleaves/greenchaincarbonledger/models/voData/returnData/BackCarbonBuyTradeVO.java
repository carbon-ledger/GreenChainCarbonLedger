package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放完成碳交易后的返回值
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class BackCarbonBuyTradeVO {
    BackUserVO organize;
    String quotaAmount;
    String pricePerUnit;
    String description;
}
