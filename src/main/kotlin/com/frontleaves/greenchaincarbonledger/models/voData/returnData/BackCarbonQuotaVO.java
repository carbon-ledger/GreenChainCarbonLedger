package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import com.frontleaves.greenchaincarbonledger.models.doData.AuditLogDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

/**
 * 用于存放返回获取自己组织碳排放配额的信息
 * <hr/>
 * 用于存放返回获取自己组织碳排放配额的信息
 * @author FLASHLACK
 * @since 2023-03-13
 */
@Data
@Accessors(chain = true)
public class BackCarbonQuotaVO {
    public String uuid;
    public String organizeUuid;
    public Integer quotaYear;
    public double totalQuota;
    public double allocatedQuota;
    public double usedQuota;
    public String allocationDate;
    public boolean complianceStatus;
    public ArrayList<AuditLogDO> auditLog;
    public String createdAt;
    public String updatedAt;
}
