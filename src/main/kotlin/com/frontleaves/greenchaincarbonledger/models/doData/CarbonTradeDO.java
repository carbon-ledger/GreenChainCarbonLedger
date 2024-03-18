package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * CarbonAccountingDO
 * <hr/>
 * 用于碳交易技术,数据库操作,CarbonTrade
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonTradeDO {
    String id;
    String organizeUuid;
    double quotaAmount;
    double pricePerUnit;
    String status;
    String description;
    String verifyUuid;
    String blockchainTxId;
    String createdAt;
    String updateAt;
}
