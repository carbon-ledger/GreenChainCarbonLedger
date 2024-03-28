package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author DC_DC
 * Date: 2024/3/28/20:23
 */
@Data
@Accessors(chain = true)
public class ElectricDO {
    String electricBuy;
    String electricOutside;
    String electricExport;
    String electricCompany;
}
