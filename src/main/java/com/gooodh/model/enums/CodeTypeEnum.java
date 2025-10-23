package com.gooodh.model.enums;

import lombok.Getter;

@Getter
public enum CodeTypeEnum {
    REGISTER_EMAIL_CODE("emailRegister", "邮箱注册验证码"),
    LOGIN_EMAIL_CODE("emailLogin", "邮箱登录验证码"),
    REGISTER_PHONE_CODE("phoneRegister", "手机注册验证码"),
    LOGIN_PHONE_CODE("phoneLogin", "手机登录验证码");

    private final String type;
    private final String desc;

    CodeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CodeTypeEnum getByType(String type) {
        for (CodeTypeEnum codeType : CodeTypeEnum.values()) {
            if (codeType.getType().equals(type)) {
                return codeType;
            }
        }
        return null;
    }
}
