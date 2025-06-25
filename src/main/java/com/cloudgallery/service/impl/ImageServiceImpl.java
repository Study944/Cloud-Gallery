package com.cloudgallery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.config.CosClientConfig;
import com.cloudgallery.exception.BusinessException;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.manager.cos.CosManager;
import com.cloudgallery.manager.cos.FileUploadManager;
import com.cloudgallery.manager.cos.ImageUploadTemplate;
import com.cloudgallery.manager.cos.UrlUploadManager;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.mapper.ImageMapper;
import com.cloudgallery.service.UserService;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 图片服务实现类
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService{

    @Resource
    @Lazy
    private FileUploadManager fileUploadManager;
    @Resource
    @Lazy
    private UrlUploadManager urlUploadManager;

    @Override
    public ImageVO uploadImage(Object dataSource, ImageUploadDto imageUploadDto, User loginUser) throws IOException {
        // 1.判断数据来源
        ImageUploadTemplate imageUploadTemplate = fileUploadManager;
        if (dataSource instanceof String) imageUploadTemplate = urlUploadManager;
        // 2.上传文件
        ImageVO imageVO = imageUploadTemplate.uploadImage(dataSource, loginUser, imageUploadDto.getName());
        return imageVO;
    }


}




