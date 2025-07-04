package com.cloudgallery.model.enums;

import lombok.Getter;

/**
 * 空间类型枚举
 */
@Getter
public enum SpaceTypeEnums {
    PRIVATE(0, "私有空间"),
    GROUP(1, "团队空间")
    ;

    private final int type;
    private final String name;

    SpaceTypeEnums(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
