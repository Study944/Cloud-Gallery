package com.cloudgallery.model.enums;

import lombok.Getter;

/**
 * 空间等级额度枚举
 */
@Getter
public enum SpaceLevelEnums {
    LEVEL_ORDINARY(0,  1024 * 1024 * 100L, 100L),
    LEVEL_VIP(1, 1024 * 1024 * 1024 * 10L, 1000L),
    LEVEL_SUPER_VIP(2, 1024 * 1024 * 1024 * 100L, 10000L);
    ;


    private final int level;

    private final Long maxSize;

    private final Long maxNumber;

    SpaceLevelEnums(int level, Long maxSize, Long maxNumber) {
        this.level = level;
        this.maxSize = maxSize;
        this.maxNumber = maxNumber;
    }
}
