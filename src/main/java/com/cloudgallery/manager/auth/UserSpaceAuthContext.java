package com.cloudgallery.manager.auth;

import lombok.Data;

@Data
public class UserSpaceAuthContext {
    // 请求中的空间Id
    public Long spaceId;
    // 请求中的图片Id
    public Long id;

}
