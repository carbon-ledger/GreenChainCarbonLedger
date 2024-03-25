package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于其他碳排放因子
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class OtherEmissionFactorDO {
    String id;
    String name;
    String displayName;
    Double factor;
    String unit;
}
