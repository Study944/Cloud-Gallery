package com.cloudgallery.manager.auth.config;

import java.io.Serializable;

/**
 * 用户空间权限
 */
public class UserSpacePermission implements Serializable {
    // 权限值
    public String key;
    // 权限名称
    public String name;
    // 权限描述
    public String description;
}
