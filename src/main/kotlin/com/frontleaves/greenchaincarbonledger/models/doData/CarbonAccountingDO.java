package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * @author 32841
 */
@Data
public class CarbonAccountingDO {
    String id;
    String organizeUuid;
    String emissionSource;
    Integer emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String verificationNotes;
    Integer carbonReportId;
    Integer blockchainTxId;
    String createAt;
    String updateAt;
}
