package com.cloudgallery.service;

import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.Image;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图片服务接口
 */
public interface ImageService extends IService<Image> {

    ImageVO uploadImage(Object dataSource, ImageUploadDto imageUploadDto, User loginUser) throws IOException;

}
