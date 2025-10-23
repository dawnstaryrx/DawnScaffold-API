package com.gooodh.model.enums;

/**
 * 统一的业务状态码定义
 * 所有状态码在此集中管理，避免魔法值散落各处。
 */
public enum ResultCode {

    // ======== 通用 ========
    SUCCESS(0, "操作成功"),
    FAILURE(1, "操作失败"),

    // ======== 认证与权限 ========
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "权限不足，无法访问"),

    // ======== 客户端错误 ========
    BAD_REQUEST(400, "请求参数不合法"),
    NOT_FOUND(404, "资源不存在"),

    // ======== 服务器错误 ========
    SERVER_ERROR(500, "服务器内部错误"),

    // ======== 业务自定义扩展 ========
    DUPLICATE_REQUEST(1001, "重复请求，请勿频繁提交"),
    DATA_CONFLICT(1002, "数据冲突，操作失败"),
    VALIDATION_ERROR(1003, "数据校验失败");

    /** 状态码 */
    private final Integer code;

    /** 提示信息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

