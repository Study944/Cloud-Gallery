package com.cloudgallery.manager.websocket.model;

import lombok.Data;

/**
 * 客户端协同编辑请求
 */
@Data
public class ImageEditMessageDto {

    // 消息类型
    private String type;
    // 事件
    private String editAction;

}
