package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BackLoginInfoVO
 * <hr/>
 * 用于返回用户登录信息
 *
 * @since 2024/34
 * @version V1.0
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class BackLoginInfoVO {
    public String userIp;
    public String deviceType;
    public String browserType;
    public String loginTime;
    public String expireTime;
}
