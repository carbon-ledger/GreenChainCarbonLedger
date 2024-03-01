package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * AuthChangeVO
 * <hr/>
 * 用于接收用户修改密码的请求参数，包括当前密码，新密码，再次输入的新密码
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author FLASHLACK
 */
@Data
public class AuthChangeVO {
    @NotBlank(message = "当前密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "当前密码根式不正确")
    public String currentPassword;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "新密码密码根式不正确")
    public String newPassword;
    @NotBlank(message = "再次输入的新密码不能为空")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,30}$", message = "再次输入的新密码根式不正确")
    public String newPasswordConfirm;
}