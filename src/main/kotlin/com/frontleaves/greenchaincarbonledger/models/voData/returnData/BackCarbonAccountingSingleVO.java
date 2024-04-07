package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BackCarbonAccountingSingleVO {
    Long id;
    String organizeUuid;
    Long reportId;
    String emissionType;
    String emissionVolume;
    Double emissionAmount;
    String accountingPeriod;
    String dataVerificationStatus;
    String verifierUuid;
    String verificationNotes;
    String createdAt;
    String updatedAt;
}
