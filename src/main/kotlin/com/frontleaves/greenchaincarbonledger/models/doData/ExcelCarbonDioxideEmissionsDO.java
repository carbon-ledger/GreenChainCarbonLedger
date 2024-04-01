package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author DC_DC
 * Date: 2024/3/31/23:17
 */

@Data
@Accessors(chain = true)
public class ExcelCarbonDioxideEmissionsDO {
    // 燃料, json格式   净消耗量: 低位发热量
    String fuel;
    // 脱硫剂, json格式  脱硫剂名称: 脱硫剂消耗量
    String desulfurizer;
    // 电力, 数字类型的字符串
    String electricity;
}
