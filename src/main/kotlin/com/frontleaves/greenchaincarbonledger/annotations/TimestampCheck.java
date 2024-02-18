package com.frontleaves.greenchaincarbonledger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间戳检查
 * <hr/>
 * 用于时间戳检查, 用于防止重放攻击, 保证请求的唯一性
 * 处理表单存在的时间戳
 *
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimestampCheck {
    String value() default "timestamp";
    long time() default 300000L;
}
