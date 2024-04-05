package com.frontleaves.greenchaincarbonledger.models.doData.excel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author DC_DC
 * Date: 2024/3/31/23:17
 */
@Data
@Accessors(chain = true)
public class ExcelEmissionActivityLevelDO {
    // 燃料名称: 净消耗量
    List<Map<String, Double>> fuelConsumption;
    // 燃料名称: 低位发热量
    List<Map<String, Double>> fuelLowCalorificValue;
    // 脱硫剂名称: 脱硫剂消耗量
    List<Map<String, Double>> desulfurizerConsumption;
    // 电力消耗量
    String electricityConsumption;
}
