package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * UserForceEditVO
 * <hr/>
 * 用户强制编辑信息的请求参数, 用于接收用户强制编辑信息的请求参数
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author FLASHLACK
 */
@Data
public class UserForceEditVO {
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,40}$", message = "用户名格式不正确【用户名长度在4-40之间，只能包含字母、数字、下划线、减号】")
    String userName;
    @Pattern(regexp = "^([\\u4e00-\\u9fa50-9A-Za-z-_]{2,40}|)$", message = "用户昵称在2到40个字符之间，允许中文、数字、大小写字母以及\"-\"和\"_\"")
    String nickName;
    @NotBlank(message = "真实信息不可为空")
    String realName;
    String avatar;
    @Email(message = "邮箱格式不正确")
    String email;
    @Pattern(regexp = "^((13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}|)$", message = "手机号格式不正确")
    String phone;
}
