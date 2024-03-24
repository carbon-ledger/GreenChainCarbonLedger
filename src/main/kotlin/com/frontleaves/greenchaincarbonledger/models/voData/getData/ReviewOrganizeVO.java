package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ReviewOrganizeVO
 * <hr/>
 * 组织审核VO，用于获取组织审核信息，包括组织名称、信用代码、营业执照、注册资本、成立日期、法定代表人姓名、
 * 法定代表人身份证号、法定代表人身份证正面、法定代表人身份证反面
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Accessors(chain = true)
public class ReviewOrganizeVO {
    @NotBlank(message = "组织名称不能为空")
    public String organizeName;
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$", message = "信用代码格式不正确")
    public String creditCode;
    @NotBlank(message = "营业执照必须是 base64 图像数据")
    public String license;
    @NotBlank(message = "注册资本不能为空")
    public String registeredCapital;
    @NotBlank(message = "成立日期不能为空")
    public String establishmentDate;
    @NotBlank(message = "法人姓名不能为空")
    public String legalRepresentativeName;
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", message = "法人身份证号格式不正确")
    public String legalRepresentativeId;
    @NotBlank(message = "法人身份证正面照必须是 base64 图像数据")
    public String legalIdCardFront;
    @NotBlank(message = "法人身份证反面照必须是 base64 图像数据")
    public String legalIdCardBack;
    public String remark;
}
