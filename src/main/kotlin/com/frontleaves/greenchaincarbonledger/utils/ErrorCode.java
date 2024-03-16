package com.frontleaves.greenchaincarbonledger.utils;

import lombok.RequiredArgsConstructor;

/**
 * 错误码
 * <hr/>
 * 用于定义错误码, 用于返回错误信息, 用于返回错误码
 *
 * @author xiao_lfeng
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
    USER_PASSWORD_DEFINITION_ERROR("UserPasswordDefinitionError", 40007, "新密码格式错误"),
    USER_PASSWORD_INCONSISTENCY_ERROR("UserPasswordInconsistencyError", 40008, "新密码与确认密码不一致"),
    USER_PASSWORD_CURRENT_ERROR("UserOldError", 40009, "当前密码错误"),
    USER_PASSWORD_REPEAT_ERROR("UsePasswordRepeatError", 40010, "新密码与当前密码重复"),
    NO_PERMISSION_ERROR("NoPermissionError", 40011, "无权限"),
    REQUEST_BODY_ERROR("RequestBodyError", 40301, "请求体参数错误"),
    TOKEN_VERIFY_ERROR("TokenVerifyError", 40302, "Token 验证错误"),
    REQUEST_BODY_MISSING("RequestBodyMissing", 40303, "请求体缺失(请求体不能为空)"),
    USER_EXISTED("UsernameExisted", 40304, "用户已存在"),
    USER_NOT_EXISTED("UsernameNotExist", 40305, "用户不存在"),
    VERIFY_CODE_ERROR("VerifyCodeError", 40306, "验证码错误"),
    VERIFY_CODE_VALID("VerifyCodeValid", 40307, "验证码有效"),
    VERIFY_CODE_NOT_EXISTED("VerifyCodeNotExisted", 40397, "校验码不存在"),
    PATH_VARIABLE_ERROR("PathVariableError", 40308, "路径变量错误"),
    TEMPLATE_PARSE_ERROR("TemplateParseError", 40309, "模板解析错误"),
    CAN_T_RESET_MY_PASSWORD("CanTResetMyPassword", 40310, "不能重置自己的密码"),
    ROLE_CANNOT_BE_DELETED("RoleCannotBeDeleted", 40311, "角色不能被删除"),
    CAN_T_OPERATE_ONESELF("CanTOperateOneself", 40312, "不能操作自己或超级管理"),
    PAGE_NOT_FOUNDED("PageNotFounded", 40401, "页面不存在"),
    REQUEST_METHOD_NOT_SUPPORTED("RequestMethodNotSupported", 40500, "请求方法不支持"),
    SERVER_INTERNAL_ERROR("ServerInternalError", 50000, "服务器内部错误"),
    MAIL_TEMPLATE_NOT_EXIST("MailTemplateNotExist", 50001, "邮箱模板不存在"),
    ORGANIZE_NOT_EXISTED("OrganizeRegisterFailed", 40399, "账户注册失败"),
    INVITE_CODE_ERROR("VerifyCodeError", 40398, "邀请码错误"),
    INSERT_DATA_ERROR("InsertDataError", 40395, "插入数据失败"),
    NO_LOGIN("NoLogin", 40110, "未登录"),
    UPDATE_DATA_ERROR("UpdateDataError", 40394, "更新数据失败"),
    USER_CANNOT_BE_BANED("UserCannotBeDeleted", 40393, "用户不能被封禁");


    final String output;
    final Integer code;
    final String message;
}
