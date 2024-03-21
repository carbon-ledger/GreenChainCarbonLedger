package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 获取未组织添加配额的信息
 * @author FLASHLACK
 */
@Data
@NotNull
public class CarbonAddQuotaVO {
    String quota;
    Boolean status;
}
