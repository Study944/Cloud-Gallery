package com.cloudgallery.controller;

import com.cloudgallery.annotation.UserRole;
import com.cloudgallery.common.BaseResponse;
import com.cloudgallery.common.ResultUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.manager.auth.SaSpaceCheckPermission;
import com.cloudgallery.manager.auth.config.SpaceUserPermissionConstant;
import com.cloudgallery.model.dto.userspace.UserSpaceDto;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.UserSpaceVO;
import com.cloudgallery.service.UserService;
import com.cloudgallery.service.UserSpaceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/userSpace")
@RestController
public class UserSpaceController {
    
    @Resource
    private UserService userService;
    @Resource
    private UserSpaceService userSpaceService;

    /**
     * 添加空间用户
     * @param userSpaceDto
     * @param request
     * @return
     */
    @PostMapping("/add/user")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<UserSpaceVO> adduserSpace(@RequestBody UserSpaceDto userSpaceDto, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(userSpaceDto == null, ErrorCode.PARAMS_ERROR);
        UserSpaceVO userSpaceVO = userSpaceService.adduserSpace(userSpaceDto, loginUser);
        return ResultUtil.success(userSpaceVO);
    }

    /**
     * 删除空间用户
     * @param userSpaceDto
     * @param request
     * @return
     */
    @PostMapping("delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<String> deleteSpaceUser(@RequestBody UserSpaceDto userSpaceDto, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);;
        String res = userSpaceService.deleteSpaceUser(userSpaceDto, loginUser);
        return ResultUtil.success(res);
    }

    /**
     * 修改空间用户
     * @param userSpaceDto
     * @param request
     * @return
     */
    @PostMapping("/update")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<UserSpaceVO> updateSpaceUser(@RequestBody UserSpaceDto userSpaceDto, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        ThrowUtil.throwIf(spaceId == null||userId == null, ErrorCode.PARAMS_ERROR, "参数不能拿为空");
        UserSpaceVO userSpaceVO = userSpaceService.updateSpaceUser(userSpaceDto ,loginUser);
        return ResultUtil.success(userSpaceVO);
    }

    /**
     * 获取空间用户列表
     * @param spaceId
     * @param request
     * @return
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<List<UserSpaceVO>> listSpaceUser(Long spaceId, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR, "参数不能拿为空");
        List<UserSpaceVO> userSpaceVO = userSpaceService.listSpaceUser(spaceId, loginUser);
        return ResultUtil.success(userSpaceVO);
    }

}
