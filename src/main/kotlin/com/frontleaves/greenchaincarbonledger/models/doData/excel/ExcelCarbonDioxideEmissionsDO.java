package com.frontleaves.greenchaincarbonledger.models.doData.excel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author DC_DC
 * Date: 2024/3/31/23:17
 */

@Data
@Accessors(chain = true)
public class ExcelCarbonDioxideEmissionsDO {
    // 总排放量
    String totalEmissions;
    // 燃料
    String fuel;
    // 脱硫剂
    String desulfurizer;
    // 电力
    String electricity;
}
