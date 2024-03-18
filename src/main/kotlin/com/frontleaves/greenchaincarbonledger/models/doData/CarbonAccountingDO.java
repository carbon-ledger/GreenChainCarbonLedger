package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

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
public class CarbonAccountingDO {
    String id;
    String organizeUuid;
    String emissionSource;
    Integer emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String verifierUuid;
    String verificationNotes;
    String carbonReportId;
    Integer blockchainTxId;
    String createdAt;
    String updatedAt;
}
