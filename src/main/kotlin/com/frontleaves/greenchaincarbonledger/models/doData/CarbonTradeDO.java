package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * @author 32841
 */

@Data
public class CarbonTradeDO {
    String organizeUuid;
    Double quotaAmount;
    Double pricePerUnit;
    String status;
    String description;
    String verifyUuid;
    String blockChainTxId;
    String createAt;
    String updateAt;
}
