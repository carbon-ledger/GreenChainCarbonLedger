package com.frontleaves.greenchaincarbonledger.models.doData.ExcelData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存放E过程附表所需的信息
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CoursesConsumptionDO {
    String displayName;
    String netConsumption;
    String factor;
}
