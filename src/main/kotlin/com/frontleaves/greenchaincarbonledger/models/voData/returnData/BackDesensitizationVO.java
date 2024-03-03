package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author 32841
 */
@Data
@Accessors(chain = true)
public class BackDesensitizationVO {
    Long uid;
    String uuid;
    String username;
    String nickname;
    String realname;
    String email;
    String avatar;
    String role;
    Timestamp createAt;
    Timestamp updateAt;
    Boolean ban;
    String invite;
    Timestamp deletedAt;
}