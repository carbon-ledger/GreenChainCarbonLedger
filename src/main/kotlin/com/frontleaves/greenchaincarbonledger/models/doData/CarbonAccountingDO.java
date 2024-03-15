package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

@Data
public class CarbonAccountingDO {
    String id;
    String organizeUuid;
    String emissionSource;
    int emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String verifierUuid;
    String verificationNotes;
    String carbonReportId;
    int blockchainTxId;
    String creatAt;
    String updateAt;
}
