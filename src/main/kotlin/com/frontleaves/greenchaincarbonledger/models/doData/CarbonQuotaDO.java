package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;


/**
 * 用于存放fy_carbon_quota的数据库放回的数据
 * <hr/>
 * 用于存放fy_carbon_quota的数据库放回的数据
 *
 * @author FLASHLACK
 * @since 2024-03-13
 */

@Data
@Accessors(chain = true)
public class CarbonQuotaDO {
    public String uuid;
    public String organizeUuid;
    public Integer quotaYear;
    public double totalQuota;
    public double allocatedQuota;
    public double usedQuota;
    public Date allocationDate;
    public boolean complianceStatus;
    public String auditLog;
    public String createdAt;
    public String updatedAt;
}
