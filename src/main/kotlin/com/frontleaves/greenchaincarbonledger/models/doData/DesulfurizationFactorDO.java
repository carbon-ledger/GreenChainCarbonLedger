package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

/**
 * @author DC_DC
 * Date: 2024/3/27/19:25
 */
@Data
public class DesulfurizationFactorDO {
    String name;
    String displayName;
    String desulfurizerMainContent;
    Double factor;
    Double carbonateContent;
}
