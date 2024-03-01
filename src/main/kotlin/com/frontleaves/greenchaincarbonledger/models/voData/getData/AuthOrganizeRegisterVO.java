package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author 32841
 */
@Data
public class AuthOrganizeRegisterVO {
    String organize;
    String username;
    String phone;
    String email;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "验证码不正确")
    String code;
    @Pattern(regexp = "^[0-9A-Za-z]{10}", message = "邀请码不正确")
    String invite;
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "密码格式不正确【必须包含大小写字母和数字的组合，可以使用特殊字符，长度在6-30之间】")
    String password;

}
