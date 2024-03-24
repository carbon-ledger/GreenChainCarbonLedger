package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放碳核算种类的对象
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonTypeDO {
    String uuid;
    String name;
    String displayName;
    String createdAt;

}
