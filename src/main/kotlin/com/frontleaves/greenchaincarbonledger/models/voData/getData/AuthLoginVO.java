package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author 26473
 */
@Data
public class AuthLoginVO {
    @NotBlank(message = "用户名不能为空")
    String user;
    @NotBlank(message = "密码不能为空")
    String password;
}
