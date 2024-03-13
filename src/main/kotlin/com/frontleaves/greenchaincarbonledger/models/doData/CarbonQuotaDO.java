package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 用于存放fy_carbon_quota的数据库放回的数据
 * <hr/>
 * 用于存放fy_carbon_quota的数据库放回的数据
 * @author FLASHLACK
 * @since 2024-03-13
 */

@Data
public class CarbonQuotaDO {
    public String uuid;
    public String organizeUuid;
    public Integer quotaYear;
    public double totalQuota;
    public double allocatedQuota;
    public double usedQuota;
    public String allocationDate;
    public boolean complianceStatus;
    public ArrayList<String> auditLog;
    public String createdAtl;
    public String updatedAt;
}
