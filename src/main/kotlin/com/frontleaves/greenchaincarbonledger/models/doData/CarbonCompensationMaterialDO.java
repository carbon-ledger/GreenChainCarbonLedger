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
public class CarbonCompensationMaterialDO {
    Long id;
    // 碳核算数据ID
    Long accountingId;
    // 原料
    String rawMaterial;
    // 电力原料
    String electricMaterial;
    // 创建时间
    String createdAt;
    // 修改时间
    String updatedAt;
}
