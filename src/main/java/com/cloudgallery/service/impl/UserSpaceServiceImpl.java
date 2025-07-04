package com.cloudgallery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudgallery.common.RoleUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.userspace.UserSpaceDto;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.entity.UserSpace;
import com.cloudgallery.model.enums.SpaceRoleEnums;
import com.cloudgallery.model.vo.UserSpaceVO;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.service.UserService;
import com.cloudgallery.service.UserSpaceService;
import com.cloudgallery.mapper.UserSpaceMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* user_space(用户-团队空间关系表)Service实现
*/
@Service
public class UserSpaceServiceImpl extends ServiceImpl<UserSpaceMapper, UserSpace>
    implements UserSpaceService{

    @Resource
    private SpaceService spaceService;
    @Resource
    private UserService userService;

    /**
     * 添加用户-空间
     * @param userSpaceDto
     * @param loginUser
     * @return
     */
    @Override
    public UserSpaceVO adduserSpace(UserSpaceDto userSpaceDto, User loginUser) {
        validateAll(userSpaceDto);
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        String spaceRole = userSpaceDto.getSpaceRole();
        // 2.校验权限
        Space space = spaceService.getById(spaceId);
        Long creator = space.getUserId();
        RoleUtil.verifyRole(loginUser, creator);
        // 3.用户是否已存在空间
        boolean exists = lambdaQuery()
                .eq(UserSpace::getUserId, userId)
                .eq(UserSpace::getSpaceId, spaceId)
                .exists();
        ThrowUtil.throwIf(exists, ErrorCode.OPERATION_ERROR, "用户已存在空间");
        // 4.添加用户-空间表
        UserSpace userSpace = UserSpace.builder()
                .userId(userId)
                .spaceId(spaceId)
                .spaceRole(spaceRole)
                .build();
        boolean save = save(userSpace);
        ThrowUtil.throwIf(!save, ErrorCode.SYSTEM_ERROR, "用户-空间添加失败");
        return BeanUtil.copyProperties(userSpace, UserSpaceVO.class);
    }

    /**
     * 删除用户-空间
     */
    @Override
    public String deleteSpaceUser(UserSpaceDto userSpaceDto,User loginUser) {
        validate(userSpaceDto);
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        Space space = spaceService.getById(spaceId);
        RoleUtil.verifyRole(loginUser, space.getUserId());
        boolean remove = lambdaUpdate()
                .eq(UserSpace::getSpaceId, spaceId)
                .eq(UserSpace::getUserId, userId)
                .remove();
        return remove ? "删除成功" : "删除失败";
    }

    /**
     * 更新空间内的用户信息
     * @param userSpaceDto
     * @param loginUser
     * @return
     */
    @Override
    public UserSpaceVO updateSpaceUser(UserSpaceDto userSpaceDto, User loginUser) {
        // 1.参数校验
        validateAll(userSpaceDto);
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        String spaceRole = userSpaceDto.getSpaceRole();
        Space space = spaceService.getById(spaceId);
        RoleUtil.verifyRole(loginUser, space.getUserId());
        // 2.更新
        UserSpace userSpace = this.lambdaQuery().eq(UserSpace::getSpaceId, spaceId)
                .eq(UserSpace::getUserId, userId)
                .one();
        ThrowUtil.throwIf(userSpace == null, ErrorCode.NOT_FOUND_ERROR, "用户空间不存在");
        userSpace.setSpaceRole(spaceRole);
        boolean update = this.updateById(userSpace);
        ThrowUtil.throwIf(!update, ErrorCode.SYSTEM_ERROR, "用户空间更新失败");
        return BeanUtil.copyProperties(userSpace, UserSpaceVO.class);
    }

    @Override
    public List<UserSpaceVO> listSpaceUser(Long spaceId, User loginUser) {
        // 1.空间是否存在
        Space space = spaceService.getById(spaceId);
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        // 2.空间权限校验
        Long creator = space.getUserId();
        RoleUtil.verifyRole(loginUser, creator);
        // 3.查询用户-空间表
        List<UserSpace> list = this.lambdaQuery()
                .eq(UserSpace::getSpaceId, spaceId)
                .list();
        List<UserSpaceVO> res = list.stream().map(userSpace -> BeanUtil.copyProperties(userSpace, UserSpaceVO.class))
                .collect(Collectors.toList());
        return res;
    }

    /**
     * 校验用户存在 ，空间存在，空间角色正确--用于新增和修改用户
     * @param userSpaceDto
     */
    public void validateAll(UserSpaceDto userSpaceDto) {
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        String spaceRole = userSpaceDto.getSpaceRole();
        ThrowUtil.throwIf(spaceId == null || userId == null || spaceRole == null, ErrorCode.PARAMS_ERROR);
        SpaceRoleEnums spaceRoleEnums = SpaceRoleEnums.getByValue(spaceRole);
        ThrowUtil.throwIf(spaceRoleEnums == null, ErrorCode.PARAMS_ERROR, "空间角色错误");
        Space space = spaceService.getById(spaceId);
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        User user = userService.getById(userId);
        ThrowUtil.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR,"用户不存在");
    }

    /**
     * 校验用户存在，空间存在--用于删除用户
     * @param userSpaceDto
     */
    public void validate(UserSpaceDto userSpaceDto) {
        Long spaceId = userSpaceDto.getSpaceId();
        Long userId = userSpaceDto.getUserId();
        ThrowUtil.throwIf(spaceId == null||userId == null, ErrorCode.PARAMS_ERROR, "参数不能拿为空");
        Space space = spaceService.getById(spaceId);
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        User user = userService.getById(userId);
        ThrowUtil.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR,"用户不存在");
    }
}




