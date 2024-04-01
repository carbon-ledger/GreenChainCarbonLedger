package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author DC_DC
 * Date: 2024/3/31/23:17
 */
@Data
@Accessors(chain = true)
public class ExcelEmissionFactorDO {
     String fuelFactor;
    String desulfurizerFactor;
    String electricityFactor;
}
