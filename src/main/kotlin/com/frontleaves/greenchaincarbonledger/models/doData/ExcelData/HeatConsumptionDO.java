package com.frontleaves.greenchaincarbonledger.models.doData.ExcelData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存入热力附表所需的信息
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class HeatConsumptionDO {
    String displayName;
    String netConsumption;
    String factor;
}
