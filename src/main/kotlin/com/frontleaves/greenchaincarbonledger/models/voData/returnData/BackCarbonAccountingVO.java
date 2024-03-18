package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 32841
 */
@Data
@Accessors(chain = true)
public class BackCarbonAccountingVO {
    String id;
    String organizeUuid;
    String emissionSource;
    Integer emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String createAt;
    String updateAt;
}
