package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * 存放碳排放报告数据表的数据
 * @author FLASHLACK
 */
@Data
public class CarbonReportDO {
    String id;
    String organizeUuid;
    String accountingPeriod;
    double totalEmission;
    double emissionReduction;
    double netEmission;
    String reportStatus;
    String verifierUuid;
    String verificationDate;
    String reportSummary;
    String blockchainTxId;
    String createdAt;
    String updateAt;
}
