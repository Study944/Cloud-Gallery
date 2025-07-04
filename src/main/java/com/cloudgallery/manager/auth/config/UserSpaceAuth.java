package com.cloudgallery.manager.auth.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户空间权RBAC配置类
 */
@Data
public class UserSpaceAuth implements Serializable {
    // 用户空间权限集合
    public List<UserSpacePermission> permissions;
    // 用户空间角色集合
    public List<UserSpaceRole> roles;
}
