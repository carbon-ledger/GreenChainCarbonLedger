package com.frontleaves.greenchaincarbonledger.models.voData.getData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author 32841
 */
@Data
public class RoleVO {
    @Pattern(regexp = "^[0-9A-Za-z_]{2,36}", message = "角色名应当只能包含大小写字母、下划线、数字，长度为2-36")
    String name;
    @NotBlank(message = "展示姓名不能为空")
    String displayName;
    ArrayList <String> permission;
}
