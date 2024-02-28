package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;

/**
 * @author 32841
 */
@Data
public class AuthNewOrganizeRegisterVO {
    String organize;
    String username;
    // 为什么此处的手机号的类型为字符串类型
    String phone;
    String email;
    String code;
    String invite;
    String password;

}
