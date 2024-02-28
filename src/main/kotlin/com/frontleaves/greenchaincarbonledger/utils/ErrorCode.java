package com.frontleaves.greenchaincarbonledger.utils;

import lombok.RequiredArgsConstructor;

/**
 * 错误码
 * <hr/>
 * 用于定义错误码, 用于返回错误信息, 用于返回错误码
 *
 * @author xiao_feng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_NOT_EXIST("TokenNotExist", 40001, "Token 不存在"),
    UUID_NOT_EXIST("UuidNotExist", 40002, "UUID 不存在"),
    TIMESTAMP_NOT_EXIST("TimestampNotExist", 40003, "时间戳不存在"),
    TIMESTAMP_INVALID("TimestampInvalid", 40004, "时间戳无效"),
    USER_ACCESS_ILLEGAL("UserAccessIllegal", 40005, "用户访问非法"),
    USER_PASSWORD_ERROR("UserPasswordError", 40006, "用户密码错误"),
    USER_PASSWORD_DEFINITION_ERROR("UserPasswordDefinitionError",40007,"新密码格式错误"),
    USER_PASSWORD_INCONSISTENCY_ERROR("UserPassworInconsistencyError",40008,"新密码与确认密码不一致"),
    USER_PASSWORD_CURRENT_ERROR("UserOldERROR",40009,"当前密码错误"),
    USER_PASSWORD_REPEAT_ERROR("UsePasswordRepeatError",40010,"新密码与当前密码重复"),
    REQUEST_BODY_ERROR("RequestBodyError", 40301, "请求体参数错误"),
    REQUEST_BODY_MISSING("RequestBodyMissing", 40302, "请求体缺失(请求体不能为空)"),
    USER_EXISTED("UsernameExisted", 40303, "用户已存在"),
    USER_NOT_EXISTED("UsernameNotExist", 40304, "用户不存在"),
    VERIFY_CODE_ERROR("VerifyCodeError", 40305, "验证码错误"),
    VERIFY_CODE_VALID("VerifyCodeValid", 40306, "验证码有效"),
    PATH_VARIABLE_ERROR("PathVariableError", 40307, "路径变量错误"),
    TEMPLATE_PARSE_ERROR("TemplateParseError", 40308, "模板解析错误"),
    PAGE_NOT_FOUNDED("PageNotFounded", 40401, "页面不存在"),
    REQUEST_METHOD_NOT_SUPPORTED("RequestMethodNotSupported", 40500, "请求方法不支持"),
    SERVER_INTERNAL_ERROR("ServerInternalError", 50000, "服务器内部错误");

    final String output;
    final Integer code;
    final String message;
}
