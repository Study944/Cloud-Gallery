package com.cloudgallery.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudgallery.annotation.UserRole;
import com.cloudgallery.common.BaseResponse;
import com.cloudgallery.common.ResultUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.manager.auth.StpKit;
import com.cloudgallery.model.dto.user.UserDto;
import com.cloudgallery.model.dto.user.UserQueryDto;
import com.cloudgallery.model.dto.user.UserUpdateDto;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.UserVO;
import com.cloudgallery.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userDto
     */
    @PostMapping("/register")
    public BaseResponse<UserVO> register(@RequestBody UserDto userDto) {
        // 判空校验
        ThrowUtil.throwIf(userDto == null, ErrorCode.PARAMS_ERROR);
        String account = userDto.getAccount();
        String password = userDto.getPassword();
        UserVO result = userService.register(account, password);
        return ResultUtil.success(result);
    }

    /**
     * 用户登录
     * @param userDto
     * @param request
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserDto userDto, HttpServletRequest request) {
        // 判空校验
        ThrowUtil.throwIf(userDto == null, ErrorCode.PARAMS_ERROR);
        String account = userDto.getAccount();
        String password = userDto.getPassword();
        UserVO result = userService.login(account, password, request);
        return ResultUtil.success(result);
    }

    /**
     * 获取当前登录用户
     * @param request
     */
    @GetMapping("/getLoginUser")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO result = BeanUtil.copyProperties(user, UserVO.class);
        return ResultUtil.success(result);
    }

    /**
     * 用户注销
     * @param request
     */
    @GetMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        boolean result = userService.logout(request);
        return ResultUtil.success(result?"注销成功":"注销失败");
    }

    /**
     * 新增用户
     * @param user
     */
    @PostMapping("/add")
    @UserRole(role = "admin")
    public BaseResponse<UserVO> addUser(@RequestBody User user) {
        // 判空校验
        ThrowUtil.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        UserVO result = userService.addUser(user);
        return ResultUtil.success(result);
    }

    /**
     * 删除用户
     * @param id
     */
    @PostMapping("/delete")
    @UserRole(role = "admin")
    public BaseResponse<Boolean> deleteUser(@RequestParam Long id) {
        // 判空校验
        ThrowUtil.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }

    /**
     * 编辑用户-管理员
     * @param user
     */
    @PostMapping("/edit")
    @UserRole(role = "admin")
    public BaseResponse<UserVO> editUser(@RequestBody User user) {
        // 判空校验
        ThrowUtil.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        UserVO result = userService.editUser(user);
        return ResultUtil.success(result);
    }

    /**
     * 编辑用户-用户
     * @param userUpdateDto
     */
    @PostMapping("/update")
    @UserRole(role = "user")
    public BaseResponse<UserVO> updateUser(@RequestBody UserUpdateDto userUpdateDto,HttpServletRequest  request) {
        // 判空校验
        ThrowUtil.throwIf(userUpdateDto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        UserVO result = userService.updateUser(userUpdateDto,loginUser);
        return ResultUtil.success(result);
    }

    /**
     * 根据id获取用户
     * @param id
     */
    @PostMapping("/getById")
//    @UserRole(role = "user")
    @SaCheckRole(type = "space")
    public BaseResponse<UserVO> getUser(@RequestParam Long id) {
        // 判空校验
        ThrowUtil.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        User userById = userService.getById(id);
        UserVO userVO = BeanUtil.copyProperties(userById, UserVO.class);
        return ResultUtil.success(userVO);
    }


    /**
     * 分页查询用户
     * @param userQueryDto
     */
    @PostMapping("/page")
    @UserRole(role = "admin")
    public BaseResponse<Page<UserVO>> listUser(@RequestBody UserQueryDto userQueryDto){
        // 1.判空校验
        ThrowUtil.throwIf(userQueryDto == null, ErrorCode.PARAMS_ERROR);
        // 2.分页查询
        Page<User> page = userService.page(
                new Page<>(userQueryDto.getCurrent(), userQueryDto.getPageSize()),
                userService.getQueryWrapper(userQueryDto));
        Page<UserVO> userVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<UserVO> userVOList = page.getRecords()
                .stream()
                .map(UserVO::userToUserVO)
                .collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtil.success(userVOPage);
    }
}
