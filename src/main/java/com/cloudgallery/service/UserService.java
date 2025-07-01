package com.cloudgallery.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.cloudgallery.model.dto.user.UserQueryDto;
import com.cloudgallery.model.dto.user.UserUpdateDto;
import com.cloudgallery.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudgallery.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* 用户服务类
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     */
    UserVO register(String account, String password);

    /**
     * 用户登录
     */
    UserVO login(String account, String password, HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean logout(HttpServletRequest request);

    /**
     * 新增用户-管理员
     */
    UserVO addUser(User user);

    /**
     * 修改用户-管理员
     */
    UserVO editUser(User user);

    /**
     * 更新用户信息-用户
     */
    UserVO updateUser(UserUpdateDto userUpdateDto,User loginUser);

    /**
     * 获取查询条件
     */
    Wrapper<User> getQueryWrapper(UserQueryDto userQueryDto);
}
