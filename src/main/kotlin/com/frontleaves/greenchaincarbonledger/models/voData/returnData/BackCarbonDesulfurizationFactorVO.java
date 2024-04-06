package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BackCarbonDesulfurizationFactorVO {
    public String name;
    public String displayName;
}
