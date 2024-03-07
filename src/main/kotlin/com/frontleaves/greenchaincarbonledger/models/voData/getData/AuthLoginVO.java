package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AuthLoginVO
 * <hr/>
 * 用于接收用户登录的请求参数，包括用户名和密码, 用于登录
 *
 * @author FLASHLACK
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
public class AuthLoginVO {
    @NotBlank(message = "用户名不能为空")
    public String user;
    @NotBlank(message = "密码不能为空")
    public String password;
}
