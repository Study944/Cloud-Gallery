package com.cloudgallery.controller;

import com.cloudgallery.annotation.UserRole;
import com.cloudgallery.common.BaseResponse;
import com.cloudgallery.common.ResultUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.space.SpaceAddDto;
import com.cloudgallery.model.dto.space.SpaceUpdateDto;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.SpaceAddVO;
import com.cloudgallery.model.vo.SpaceUpdateVO;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/space")
@RestController
public class SpaceController {

    @Resource
    SpaceService spaceService;
    @Resource
    UserService userService;

    @PostMapping("/add")
    @UserRole(role = "user")
    public BaseResponse<SpaceAddVO> addSpace(@RequestBody SpaceAddDto spaceAddDto, HttpServletRequest request){
        ThrowUtil.throwIf(spaceAddDto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceAddVO spaceAddVO = spaceService.addSpace(spaceAddDto,loginUser);
        return ResultUtil.success(spaceAddVO);
    }

    @GetMapping("/delete")
    @UserRole(role = "user")
    public BaseResponse<Boolean> deleteSpace(@RequestParam Long spaceId, HttpServletRequest request){
        ThrowUtil.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean delete = spaceService.deleteSpace(spaceId,loginUser);
        return ResultUtil.success(delete);
    }

    @PostMapping("/update")
    @UserRole(role = "user")
    public BaseResponse<SpaceUpdateVO> updateSpace(@RequestBody SpaceUpdateDto spaceUpdateDto, HttpServletRequest request){
        ThrowUtil.throwIf(spaceUpdateDto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceUpdateVO spaceUpdateVO= spaceService.updateSpace(spaceUpdateDto,loginUser);
        return ResultUtil.success(spaceUpdateVO);
    }

    @GetMapping("/get")
    @UserRole(role = "user")
    public BaseResponse<Space> getSpace(@RequestParam Long spaceId, HttpServletRequest request){
        ThrowUtil.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR);
        Space space= spaceService.getById(spaceId);
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        return ResultUtil.success(space);
    }

}
