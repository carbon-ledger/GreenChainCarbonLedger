package com.frontleaves.greenchaincarbonledger.models.doData.ExcelData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存放固碳所需的附表信息
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonSequestrationConsumptionDO {
    String displayName;
    String netConsumption;
    String factor;
}
