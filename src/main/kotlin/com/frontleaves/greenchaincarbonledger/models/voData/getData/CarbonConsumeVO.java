package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;

/**
 * 用于存放创建碳核算报告的body值
 * @author  FLASHLACK
 */
@Data
public class CarbonConsumeVO {
    String type;
    String startTime;
    String endTime;
    String title;
    String materials;
    String electricBuy;
    String electricOutside;
    String electricExport;
    String electricCompany;
    String summary;
    String parameters;
}
