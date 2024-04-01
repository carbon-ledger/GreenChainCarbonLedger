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
public class ExcelEmissionFactorDO {
    List<Map<String, Double>> carbonUnitCalorific;
    List<Map<String, Double>> fuelOxidationRate;
    List<Map<String, Double>> desulfurizerFactor;
    String electricityFactor;
}
