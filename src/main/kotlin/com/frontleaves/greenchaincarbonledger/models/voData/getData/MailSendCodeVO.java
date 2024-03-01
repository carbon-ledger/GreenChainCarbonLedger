package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * MailSendCodeVO
 * <hr/>
 * 用于邮件发送验证码的数据对象
 *
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
public class MailSendCodeVO {
    @Email(message = "邮箱格式不正确")
    public String email;
    @NotBlank(message = "模板不能为空")
    public String template;
}
