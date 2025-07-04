package com.cloudgallery.model.dto.userspace;

import lombok.Data;

/**
 * 添加空间用户请求
 */
@Data
public class UserSpaceDto {
    // 空间id
    private Long spaceId;
    // 用户id
    private Long userId;
    // 空间角色
    private String spaceRole;
}
