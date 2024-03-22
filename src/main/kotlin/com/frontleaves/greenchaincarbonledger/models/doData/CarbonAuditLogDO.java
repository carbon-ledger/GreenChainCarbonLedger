package com.frontleaves.greenchaincarbonledger.models.doData;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放碳排放配额的审计日志
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonAuditLogDO {
    String date;
    String log;
    String operate;
}
