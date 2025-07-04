package com.cloudgallery.model.enums;

import lombok.Getter;

/**
 * 空间权限枚举
 */
@Getter
public enum SpaceRoleEnums {
     VIEWER("viewer", "只读"),
     EDITOR("editor", "可编辑"),
     OWNER("owner", "管理员")
    ;

    private final String value;
    private final String name;


    SpaceRoleEnums(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SpaceRoleEnums getByValue(String value) {
        for (SpaceRoleEnums item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
