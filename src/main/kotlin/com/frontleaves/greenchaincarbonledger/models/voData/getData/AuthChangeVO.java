package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author 26473
 */
@Data
public class AuthChangeVO {
    @NotBlank(message = "当前密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "当前密码根式不正确")
    String currentPassword;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "新密码密码根式不正确")
    String newPassword;
    @NotBlank(message = "再次输入的新密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "再次输入的新密码根式不正确")
    String newPasswordConfirm;
}