package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用来存放碳排放类型表的对象
 * @author  FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonItemTypeDO {
    String id;
    String mode;
    String name;
    String displayName;
    Double lowCalorific;
    Double carbonUnitCalorific;
    Double fuelOxidationRate;
}
