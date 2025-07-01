package com.cloudgallery.service;

import com.cloudgallery.model.dto.space.SpaceAddDto;
import com.cloudgallery.model.dto.space.SpaceUpdateDto;
import com.cloudgallery.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.SpaceAddVO;
import com.cloudgallery.model.vo.SpaceUpdateVO;

/**
 * 空间接口
 */
public interface SpaceService extends IService<Space> {

    SpaceAddVO addSpace(SpaceAddDto spaceAddDto, User loginUser);

    boolean deleteSpace(Long spaceId, User loginUser);

    SpaceUpdateVO updateSpace(SpaceUpdateDto spaceUpdateDto, User loginUser);
}
