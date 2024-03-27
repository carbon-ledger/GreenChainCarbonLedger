package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存放企业碳排放配额原料表的实例
 * @author DC_DC
 * Date: 2024/3/27/22:38
 */
@Data
@Accessors(chain = true)
public class fyCarbonCompensationMaterialDO {
    String id;
    String accountingId;
    String rawMaterial;
    String electricMaterial;
    String createdAt;
    String updatedAt;
}
