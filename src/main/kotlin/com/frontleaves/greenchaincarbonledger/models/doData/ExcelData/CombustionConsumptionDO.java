package com.frontleaves.greenchaincarbonledger.models.doData.ExcelData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放对于附表中所需要的数据
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class CombustionConsumptionDO {
    String displayName;
    String netConsumption;
    String lowCalorific;
    String carbonUnitCalorific;
    String fuelOxidationRate;
}
