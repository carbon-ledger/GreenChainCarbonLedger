package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * UserEditVO
 * <hr/>
 * 用户编辑信息的请求参数, 用于接收用户编辑信息的请求参数
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
public class UserEditVO {
    @Pattern(regexp = "^([\\u4e00-\\u9fa50-9A-Za-z-_]{2,40}|)$", message = "用户昵称在2到40个字符之间，允许中文、数字、大小写字母以及\"-\"和\"_\"")
    public String nickName;
    @Pattern(regexp = "^([a-zA-z]+://\\S*|)$", message = "头像格式不正确")
    public String avatar;
    @Email(message = "邮箱格式不正确")
    public String email;
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式不正确")
    public String phone;
    @Pattern(regexp = "^([0-9A-Za-z]{6,10}|)$", message = "邮箱验证码格式不正确")
    public String emailCode;
    @Pattern(regexp = "^([0-9A-Za-z]{6,10}|)$", message = "手机验证码格式不正确")
    public String phoneCode;
}
