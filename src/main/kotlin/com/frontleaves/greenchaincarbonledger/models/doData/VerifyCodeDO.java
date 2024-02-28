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
    Boolean type;
    String content;
    String code;
    Timestamp createdAt;
    Timestamp expiredAt;
    String userAgent;
    String userIp;
}
