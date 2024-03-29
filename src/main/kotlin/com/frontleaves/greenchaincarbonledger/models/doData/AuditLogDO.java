package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审计日志
 *
 * @since v1.0.0
 * @version v1.0.0
 * @author 筱锋xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class AuditLogDO {
    public String log;
    public String date;
    public String operate;
}
