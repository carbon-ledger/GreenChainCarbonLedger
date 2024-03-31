package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author 32841
 */
@Data
public class TradeReleaseVO {
    @Pattern(regexp = "\\d+(\\.\\d{0,2})?$", message = "必须为正数，且最多两位小数")
    String amount;
    @Pattern(regexp = "\\d+(\\.\\d{0,2})?$", message = "必须为正数，且最多两位小数")
    String unit;
    String text;
    Boolean draft;
}
