package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存放碳排放报告数据表的数据
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonReportDO {
    Long id;
    String organizeUuid;
    String reportTitle;
    String reportType;
    String accountingPeriod;
    Double totalEmission;
    String reportStatus;
    String verifierUuid;
    String verificationDate;
    String reportSummary;
    String blockchainTxId;
    String listOfReports;
    String createdAt;
    String updatedAt;
}
