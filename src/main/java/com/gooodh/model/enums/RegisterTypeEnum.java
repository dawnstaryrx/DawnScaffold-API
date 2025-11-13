package com.gooodh.model.enums;

import lombok.Getter;

@Getter
public enum RegisterTypeEnum {
    EMAIL_TYPE("email", "邮箱"),
    PHONE_TYPE("phone", "手机");

    private final String type;
    private final String desc;

    RegisterTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static RegisterTypeEnum getByType(String type) {
        for (RegisterTypeEnum registerType : RegisterTypeEnum.values()) {
            if (registerType.getType().equals(type)) {
                return registerType;
            }
        }
        return null;
    }
}
