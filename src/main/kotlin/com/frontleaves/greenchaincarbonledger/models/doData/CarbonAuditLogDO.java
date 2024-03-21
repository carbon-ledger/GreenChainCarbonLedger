package com.frontleaves.greenchaincarbonledger.models.doData;
import lombok.Data;

/**
 * 用于存放碳排放配额的审计日志
 * @author FLASHLACK
 */
@Data
public class CarbonAuditLogDO {
    String date;
    String log;
    String operate;
}
