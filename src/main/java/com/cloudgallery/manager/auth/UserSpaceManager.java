package com.cloudgallery.manager.auth;

import com.cloudgallery.manager.auth.config.UserSpaceAuth;
import com.cloudgallery.manager.auth.config.UserSpacePermission;
import com.cloudgallery.manager.auth.config.UserSpaceRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户空间管理类
 */
@Slf4j
@Component
public class UserSpaceManager {

    // 1.读取json配置文件到用户空间权限列表
    public static final UserSpaceAuth USER_SPACE_AUTH;
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/main/resources/role.json");
        try {
            USER_SPACE_AUTH = objectMapper.readValue(file, UserSpaceAuth.class);
        } catch (IOException e) {
            log.info("读取RBAC配置文件失败：{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据角色获取权限集合
     * @param role
     * @return
     */
    public List<String> getPermissionsByRole(String role) {
        List<UserSpaceRole> roles = USER_SPACE_AUTH.getRoles();
        for (UserSpaceRole userSpaceRole : roles) {
            if (userSpaceRole.getKey().equals(role)) {
                return userSpaceRole.getPermissions();
            }
        }
        return new ArrayList<>();
    }

}
