package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * 用于存放用户登录状态信息
 * <hr/>
 * 用于存放用户登录状态信息(Uuid,Token,UserAgent,UserIp)
 * @since 2024/34
 * @version V1.0
 * @author FLASHLACK
 */
@NotNull
@Data
@Accessors(chain = true)
public class UserLoginDO {
    public String uuid;
    public String token;
    public String userAgent;
    public String userIp;
}
