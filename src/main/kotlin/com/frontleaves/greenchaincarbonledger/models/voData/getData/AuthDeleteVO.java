package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * AuthDeleteVO
 * <hr/>
 * 用于接收用户账号注销的请求参数，包括当前密码，邮箱验证码
 *
 * @author FLASHLACK
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
public class AuthDeleteVO {
    @NotBlank(message = "用户密码不可为空")
    String password;
    @NotBlank(message = "邮箱验证码不可以为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "邮箱验证码错误")
    String code;

}
