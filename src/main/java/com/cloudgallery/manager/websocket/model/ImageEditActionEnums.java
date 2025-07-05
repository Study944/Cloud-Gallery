package com.cloudgallery.manager.websocket.model;

import lombok.Getter;

/**
 * 事件类型枚举
 */
@Getter
public enum ImageEditActionEnums {
    ZOOM_IN("放大操作", "zoomIn"),
    ZOOM_OUT("缩小操作", "zoomOut"),
    ROTATE_LEFT("左旋操作", "rotateLeft"),
    ROTATE_RIGHT("右旋操作", "rotateRight")
    ;

    // 名称
    private final String name;
    // 值
    private final String value;


    ImageEditActionEnums(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ImageEditActionEnums getByValue(String value) {
        for (ImageEditActionEnums item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
