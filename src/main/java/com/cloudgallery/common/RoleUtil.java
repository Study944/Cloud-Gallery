package com.cloudgallery.common;


import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.enums.UserEnums;

/**
 * 权限校验工具类
 */
public class RoleUtil {

    /**
     * 校验当前登录用户权限
     * @param loginUser
     * @param userId
     */
    public static void verifyRole(User loginUser, Long userId) {
        ThrowUtil.throwIf(
                !loginUser.getId().equals(userId)&&!loginUser.getRole().equals(UserEnums.ADMIN.getValue()),
                ErrorCode.NO_AUTH_ERROR);
    }

}
