package com.cloudgallery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudgallery.common.RoleUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.constant.UserConstant;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.user.UserQueryDto;
import com.cloudgallery.model.dto.user.UserUpdateDto;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.UserVO;
import com.cloudgallery.service.UserService;
import com.cloudgallery.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* 用户服务实现类
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    /**
     * 用户注册
     * @param account
     * @param password
     */
    @Override
    public UserVO register(String account, String password) {
        //1.参数校验
        userParamVerify(account, password);
        //2.查询用户账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        boolean exists = exists(queryWrapper);
        ThrowUtil.throwIf(exists, ErrorCode.PARAMS_ERROR, "用户已存在");
        //3.添加用户
        User user = new User();
        user.setName("用户"+account);
        user.setAccount(account);
        user.setPassword(password);
        boolean save = save(user);
        ThrowUtil.throwIf(!save, ErrorCode.SYSTEM_ERROR, "用户注册失败");
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 用户登录
     * @param account
     * @param password
     * @param request
     */
    @Override
    public UserVO login(String account, String password, HttpServletRequest request) {
        //1.参数校验
        userParamVerify(account, password);
        //2.获取用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = getOne(queryWrapper);
        ThrowUtil.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        //3.密码校验
        ThrowUtil.throwIf(!password.equals(user.getPassword()), ErrorCode.PARAMS_ERROR, "密码错误");
        //4.保存登录信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtil.throwIf(attribute == null, ErrorCode.NOT_LOGIN_ERROR);
        User user = (User) attribute;
        return user;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public boolean logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 新增用户-管理员
     * @param user
     */
    @Override
    public UserVO addUser(User user) {
        // 1.参数校验
        userParamVerify(user.getAccount(), user.getPassword());
        // 2.查询用户账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", user.getAccount());
        boolean exists = exists(queryWrapper);
        ThrowUtil.throwIf(exists, ErrorCode.PARAMS_ERROR, "用户已存在");
        // 3.添加用户
        boolean save = save(user);
        ThrowUtil.throwIf(!save, ErrorCode.SYSTEM_ERROR, "用户添加失败");
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 更新用户-管理员
     * @param user
     */
    @Override
    public UserVO editUser(User user) {
        // 1.参数校验
        ThrowUtil.throwIf(user.getId() == null, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        // 2.更新用户
        User editUser = getById(user.getId());
        ThrowUtil.throwIf(editUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        // 3.只更新允许的字段
        editUser.setName(user.getName());
        editUser.setRole(user.getRole());
        editUser.setPassword(user.getPassword());
        editUser.setAccount(user.getAccount());
        editUser.setSex(user.getSex());
        editUser.setAvatar(user.getAvatar());
        boolean update = updateById(editUser);
        ThrowUtil.throwIf(!update, ErrorCode.SYSTEM_ERROR, "用户更新失败");
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 更新用户信息-用户
     * @param userUpdateDto
     * @param request
     */
    @Override
    public UserVO updateUser(UserUpdateDto userUpdateDto,User loginUser) {
        // 1.参数校验
        ThrowUtil.throwIf(userUpdateDto.getId() == null, ErrorCode.PARAMS_ERROR);
        // 2.权限校验，只能修改自己的信息（当前登录用户ID==参数ID||当前登录用户为管理员）
        RoleUtil.verifyRole(loginUser, userUpdateDto.getId());
        // 3.更新用户
        User targetUser = getById(userUpdateDto.getId());
        ThrowUtil.throwIf(targetUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        // 4.只更新允许的字段
        targetUser.setName(userUpdateDto.getName());
        targetUser.setPassword(userUpdateDto.getPassword());
        targetUser.setSex(userUpdateDto.getSex());
        targetUser.setAvatar(userUpdateDto.getAvatar());
        boolean update = updateById(targetUser);
        ThrowUtil.throwIf(!update, ErrorCode.SYSTEM_ERROR, "用户更新失败");
        return BeanUtil.copyProperties(loginUser, UserVO.class);
    }

    @Override
    public Wrapper<User> getQueryWrapper(UserQueryDto userQueryDto) {
        // 1.获取查询信息
        String sortField = userQueryDto.getSortField();
        String sortOrder = userQueryDto.getSortOrder();
        String name = userQueryDto.getName();
        String account = userQueryDto.getAccount();
        Integer sex = userQueryDto.getSex();
        // 2.创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(account), "account", account);
        queryWrapper.eq(sex != null, "sex", sex);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder.equals("scend"), sortField);
        return queryWrapper;
    }

    /**
     * 用户参数校验
     * @param account
     * @param password
     */
    public void userParamVerify(String account, String password){
        ThrowUtil.throwIf(account == null || account.length() < 6, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(password == null || password.length() < 6, ErrorCode.PARAMS_ERROR);
    }
}




