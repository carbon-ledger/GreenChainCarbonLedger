package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用于存放创建碳核算报告的body值
 * @author  FLASHLACK
 */
@Data
public class CarbonConsumeVO {
    @Pattern(regexp = "^(steelProduction|generateElectricity)$", message = "type 只能为 steelProduction 或 generateElectricity")
    String type;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "开始时间格式不正确")
    String startTime;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "结束时间格式不正确")
    String endTime;
    @NotBlank(message = "报告抬头标题不正确")
    String title;
    @NotBlank(message = "具体用料信息参数不能为空")
    String materials;
    @NotBlank(message = "电力购入量不能为空")
    String electricBuy;
    @NotBlank(message = "电力生产之外的电力消耗不能为空")
    String electricOutside;
    @NotBlank(message = "电力外销不能为空")
    String electricExport;
    @NotBlank(message = "电力公司不能为空")
    String electricCompany;
    @NotBlank(message = "报告摘要不能为空")
    String summary;
    Boolean send;
}
