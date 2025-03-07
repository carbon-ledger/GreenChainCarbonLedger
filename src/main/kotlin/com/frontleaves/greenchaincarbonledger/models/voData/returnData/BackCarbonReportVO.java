package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放会回用户的组织碳核算报告
 * <hr/>
 * 用于存放会回用户的组织碳核算报告
 * @author FLASHLACK
 * @since 2024-3-14
 */
@Data
@Accessors(chain = true)
public class BackCarbonReportVO {
    Long id;
    String organizeUuid;
    String accountingPeriod;
    Double totalEmission;
    String reportStatus;
    String listOfReports;
    String createdAt;
    String updatedAt;
}
