package com.cloudgallery.manager.websocket.model;

import lombok.Data;
import lombok.Getter;

/**
 * 图片编辑消息类型枚举
 */
@Getter
public enum ImageEditMessageTypeEnums {
    INFO("发送通知", "info"),
    ERROR("发送错误", "error"),
    ENTER_EDIT("进入编辑", "enterEdit"),
    EXIT_EDIT("退出编辑", "exitEdit"),
    EDIT_ACTION("图片编辑动作", "editAction"),
    ;

    // 名称
    private final String name;
    // 值
    private final String value;

    ImageEditMessageTypeEnums(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public static ImageEditMessageTypeEnums getByValue(String value) {
        for (ImageEditMessageTypeEnums item : ImageEditMessageTypeEnums.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
