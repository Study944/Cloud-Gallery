package com.cloudgallery.manager.auth.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户空间角色
 */
@Data
public class UserSpaceRole implements Serializable {
    // 角色值
    public String key;
    // 角色名称
    public String name ;
    // 角色描述
    public String description ;
    // 角色权限集合
    public List<String> permissions ;
}
