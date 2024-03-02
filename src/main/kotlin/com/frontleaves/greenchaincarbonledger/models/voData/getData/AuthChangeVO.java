package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * AuthChangeVO
 * <hr/>
 * 用于接收用户修改密码的请求参数，包括当前密码，新密码，再次输入的新密码
 *
 * @author FLASHLACK
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
public class AuthChangeVO {
    @NotBlank(message = "当前密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "当前密码根式不正确")
    public String currentPassword;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "新密码密码根式不正确【必须包含大小写字母和数字的组合，可以使用特殊字符，长度在6-30之间】")
    public String newPassword;
    @NotBlank(message = "再次输入的新密码不能为空")
    public String newPasswordConfirm;

    //判断新密码与再次输入密码重复
    @AssertTrue(message = "新密码和再次输入的新密码不相同")
    public boolean isPasswordMatch() {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
        //还没有加上修改密码后邮箱的发送提醒
    }
}