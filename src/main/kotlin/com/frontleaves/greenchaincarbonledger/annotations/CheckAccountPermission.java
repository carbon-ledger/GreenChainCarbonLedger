package com.frontleaves.greenchaincarbonledger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CheckAccountPermission
 * <hr/>
 * 用于检查账户权限的注解, 用于检查账户权限
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAccountPermission {
    // 用户角色（支持 "console, admin, organize"）
    String value() default "";

    // 所需要权限
    String[] permissions();
}
