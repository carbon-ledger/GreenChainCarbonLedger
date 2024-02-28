package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * AuthUserRegisterVO
 * <hr/>
 * 用于接收用户注册请求的数据, 用于接收用户注册请求的数据
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
public class AuthUserRegisterVO {
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,40}$", message = "用户名格式不正确")
    String username;
    @NotBlank(message = "真实信息不能为空")
    String realname;
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式不正确")
    String phone;
    @Email(message = "邮箱格式不正确")
    String email;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "验证码不正确")
    String code;
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "密码格式不正确【必须包含大小写字母和数字的组合，可以使用特殊字符，长度在6-30之间】")
    String password;
}
