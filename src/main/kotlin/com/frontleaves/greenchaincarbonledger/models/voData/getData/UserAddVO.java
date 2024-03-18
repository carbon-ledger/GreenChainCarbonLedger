package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * UserAddVO
 * <hr/>
 * 用户添加信息的请求参数, 用于接收用户添加信息的请求参数
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author 戴闯
 */
@Data
public class UserAddVO {
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,40}$", message = "用户名格式不正确【用户名长度在4-40之间，只能包含字母、数字、下划线、减号】")
    public String username;
    @NotBlank(message = "真实信息不能为空")
    public String realname;
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式不正确")
    public String phone;
    @Email(message = "邮箱格式不正确")
    public String email;
    @Pattern(regexp = "^(console|admin|organize)$", message = "角色格式不正确【角色只能是console、admin、organize】")
    public String role;
}
