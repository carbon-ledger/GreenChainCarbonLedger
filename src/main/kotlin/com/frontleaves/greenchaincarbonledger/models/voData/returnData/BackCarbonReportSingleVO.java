package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BackCarbonReportSingleVO {
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
    String listOfReports;
    String createdAt;
    String updatedAt;
}
