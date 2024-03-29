package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * CarbonAccountingDO
 * <hr/>
 * 用于碳交易技术,数据库操作,CarbonAccountingDO
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonAccountingDO {
    String id;
    String organizeUuid;
    String reportId;
    String emissionType;
    String emissionVolume;
    Integer emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String verifierUuid;
    String verificationNotes;
    Integer blockchainTxId;
    String createdAt;
    String updatedAt;
}
