package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * VerifyCodeDO
 * <hr/>
 * 用于验证码的数据对象
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class VerifyCodeDO {
    public Boolean type;
    public String content;
    public String code;
    public Timestamp createdAt;
    public Timestamp expiredAt;
    public String userAgent;
    public String userIp;
}
