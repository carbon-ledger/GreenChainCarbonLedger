package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * 用于存放碳排放因子的对象
 * @author FALSHLACK
 */
@Data
public class ProcessEmissionFactorDO {
    String id;
    String name;
    String displayName;
    Double factor;
}
