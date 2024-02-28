package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginVO {
    @NotBlank(message = "user demo")
    String user;
    String password;
}
