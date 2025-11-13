package com.gooodh.model.enums;

import lombok.Getter;

@Getter
public enum PlatformEnum {
    GITHUB("GitHub", "GitHub"),
    LINUX_DO("LinuxDo", "Linux论坛");

    private final String platform;
    private final String desc;

    PlatformEnum(String platform, String desc) {
        this.platform = platform;
        this.desc = desc;
    }
}
