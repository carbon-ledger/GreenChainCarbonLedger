package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * BackUserVO
 * <hr/>
 * 用于返回后台用户信息的数据对象
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class BackUserVO {
    public String uuid;
    public String userName;
    public String nickName;
    public String realName;
    public String email;
    public String phone;
    public String newPassword;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
