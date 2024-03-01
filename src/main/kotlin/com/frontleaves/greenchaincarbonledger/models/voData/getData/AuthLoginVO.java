package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * AuthLoginVO
 * <hr/>
 * 用于接收用户登录的请求参数，包括用户名和密码, 用于登录
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author FLASHLACK
 */
@Data
public class AuthLoginVO {
    @NotBlank(message = "用户名不能为空")
    String user;
    @NotBlank(message = "密码不能为空")
    //若密码格式错误则密码肯定错误
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$",message = "密码错误")
    String password;
}
