package com.frontleaves.greenchaincarbonledger.models.doData.ExcelData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存放电力附表所需要的信息
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class ElectricityCombustionDO {
    String displayName;
    String netCombustion;
    String factor;
}
