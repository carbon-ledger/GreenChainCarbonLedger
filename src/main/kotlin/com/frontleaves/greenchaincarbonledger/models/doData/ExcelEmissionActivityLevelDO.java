package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author DC_DC
 * Date: 2024/3/31/23:17
 */
@Data
@Accessors(chain = true)
public class ExcelEmissionActivityLevelDO {
    // 燃料名称: 净消耗量
    Map<String, Double> fuelConsumption;
    // 燃料名称: 低位发热量
    Map<String, Double> fuelLowCalorificValue;
    //
    String desulfurizerLevel;
    String electricityLevel;
}
