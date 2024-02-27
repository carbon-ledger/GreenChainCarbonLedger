package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import lombok.Data;

/**
 * AuthOrganizeRegisterVO
 * <hr/>
 * 用于接收用户注册请求的数据, 用于接收用户注册请求的数据
 *
 * @since v1.0.0
 * @version v1.0.0
 * @author xiao_lfeng
 */
@Data
public class AuthOrganizeRegisterVO {
    String username;
    String realname;
    String phone;
    String email;
    String code;
    String password;
}
