package com.cloudgallery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudgallery.common.RoleUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.space.SpaceAddDto;
import com.cloudgallery.model.dto.space.SpaceUpdateDto;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.entity.UserSpace;
import com.cloudgallery.model.enums.SpaceLevelEnums;
import com.cloudgallery.model.enums.SpaceRoleEnums;
import com.cloudgallery.model.vo.SpaceAddVO;
import com.cloudgallery.model.vo.SpaceUpdateVO;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.mapper.SpaceMapper;
import com.cloudgallery.service.UserSpaceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * 空间实现
 */
@Service
@Slf4j
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    @Lazy
    private UserSpaceService userSpaceService;

    @Override
    public SpaceAddVO addSpace(SpaceAddDto spaceAddDto, User loginUser) {
        // 1.空间参数校验
        String name = spaceAddDto.getName();
        String description = spaceAddDto.getDescription();
        Integer spaceType = spaceAddDto.getSpaceType();
        ThrowUtil.throwIf(name == null || name.length() > 20, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(description == null || description.length() > 100, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(spaceType != 0 && spaceType != 1, ErrorCode.PARAMS_ERROR);
        // 2.空间字段填充
        Space space = Space.builder()
                .name(name)
                .description(description)
                .userId(loginUser.getId())
                .level(SpaceLevelEnums.LEVEL_ORDINARY.getLevel())
                .maxSize(SpaceLevelEnums.LEVEL_ORDINARY.getMaxSize())
                .maxNumber(SpaceLevelEnums.LEVEL_ORDINARY.getMaxNumber())
                .spaceType(spaceType)
                .build();
        // 3.空间新增
        String lock = loginUser.getId().toString();
        // 使用同步锁防止在高并发情况下，用户重复添加空间
        synchronized (lock) {
            // 需要同时操作空间表和用户-空间表，使用事务
            transactionTemplate.execute(status -> {
                // 查询用户是否已存在指定空间
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, loginUser.getId())
                        .eq(Space::getSpaceType, spaceType)
                        .exists();
                ThrowUtil.throwIf(exists, ErrorCode.OPERATION_ERROR, "用户已存在空间");
                boolean save = this.save(space);
                ThrowUtil.throwIf(!save, ErrorCode.SYSTEM_ERROR, "空间添加失败");
                // 添加用户-空间表
                UserSpace userSpace = UserSpace.builder()
                        .userId(loginUser.getId())
                        .spaceId(space.getId())
                        .spaceRole(SpaceRoleEnums.OWNER.getValue())
                        .build();
                boolean saveUserSpace = userSpaceService.save(userSpace);
                ThrowUtil.throwIf(!saveUserSpace, ErrorCode.SYSTEM_ERROR, "用户-空间添加失败");
                return true;
            });
        }
        return BeanUtil.copyProperties(space, SpaceAddVO.class);
    }

    @Override
    public boolean deleteSpace(Long spaceId, User loginUser) {
        // 1.校验空间是否存在
        Space space = this.getById(spaceId);
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        // 2.检验权限
        Long userId = space.getUserId();
        RoleUtil.verifyRole(loginUser, userId);
        // 3.删除空间
        boolean removeById = this.removeById(spaceId);
        // TODO 添加删除空间下的图片
        return removeById;
    }

    @Override
    public SpaceUpdateVO updateSpace(SpaceUpdateDto spaceUpdateDto, User loginUser) {
        // 1.校验空间是否存在
        Space space = this.getById(spaceUpdateDto.getSpaceId());
        ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        // 2.检验权限
        Long userId = space.getUserId();
        RoleUtil.verifyRole(loginUser, userId);
        // 3.更新空间
        if (spaceUpdateDto.getName() != null) space.setName(spaceUpdateDto.getName());
        if (spaceUpdateDto.getDescription() != null) space.setDescription(spaceUpdateDto.getDescription());
        boolean update = this.updateById(space);
        ThrowUtil.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新空间失败");
        return BeanUtil.copyProperties(space, SpaceUpdateVO.class);
    }
}




