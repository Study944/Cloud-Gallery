package com.cloudgallery.service;

import com.cloudgallery.model.dto.userspace.UserSpaceDto;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.entity.UserSpace;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudgallery.model.vo.UserSpaceVO;

import java.util.List;

/**
* 用户-团队空间关系表Service
*/
public interface UserSpaceService extends IService<UserSpace> {

    UserSpaceVO adduserSpace(UserSpaceDto userSpaceDto, User loginUser);

    String deleteSpaceUser(UserSpaceDto userSpaceDto, User loginUser);

    UserSpaceVO updateSpaceUser(UserSpaceDto userSpaceDto, User loginUser);

    List<UserSpaceVO> listSpaceUser(Long spaceId, User loginUser);
}
