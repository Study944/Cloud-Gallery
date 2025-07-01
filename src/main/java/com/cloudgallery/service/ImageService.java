package com.cloudgallery.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.cloudgallery.model.dto.image.ImageQueryDto;
import com.cloudgallery.model.dto.image.ImageUpdateDto;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 图片服务接口
 */
public interface ImageService extends IService<Image> {

    ImageVO uploadImage(Object dataSource, ImageUploadDto imageUploadDto, User loginUser) throws IOException;

    Wrapper<Image> getQueryWrapper(ImageQueryDto imageQueryDto);

    Boolean deleteImageById(Long id,User loginUser);

    ImageVO updateImage(ImageUpdateDto imageUpdateDto, User loginUser);

    Boolean auditPassImage(Long id,User loginUser);

    Boolean auditRejectImage(Long id, String reason, User loginUser);

    ImageVO generateImage(String prompt, User loginUser) throws IOException;

    void downloadImage(Long id, HttpServletResponse  response) throws IOException;
}
