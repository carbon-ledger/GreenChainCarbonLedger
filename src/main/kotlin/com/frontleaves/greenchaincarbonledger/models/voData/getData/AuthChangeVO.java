package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;

@Data
public class AuthChangeVO {
    String currentPassword;
    String newPassword;
    String newPasswordConfirm;
}