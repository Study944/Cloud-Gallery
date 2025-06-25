package com.cloudgallery.manager.cos;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.config.CosClientConfig;
import com.cloudgallery.exception.BusinessException;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.UserService;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public abstract class ImageUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private UserService userService;
    @Resource
    private CosManager cosManager;
    @Resource
    private ImageService imageService;

    public ImageVO uploadImage(Object dataSource, User loginUser,String imageName) throws IOException {
        // 1.文件/URL格式校验
        validator(dataSource);
        // 2.设置图片上传信息
        String name= getFilename(dataSource);
        String filename = DateUtil.formatDate(new Date())+"_"+RandomUtil.randomString(6) +"_"+ name;
        String key = String.format("image/%s", filename);
        // 3.图片上传
        File file = null;
        ImageVO imageVO;
        try {
            // 3.1获取文件/URL对应的图片
            file = getFileBySource(dataSource, File.createTempFile(key, null));
            // 3.2上传图片到COS并获取图片信息
            PutObjectResult putObjectResult = cosManager.uploadFileWithData(file, key);
            // 3.3数据库插入图片信息
            if (imageName==null) imageName = name;
            imageVO = getImage(loginUser, putObjectResult,imageName, key);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        } finally {
            if (file != null) file.delete();
        }
        return imageVO;
    }

    /**
     * 数据库插入图片信息
     */
    private ImageVO getImage(User loginUser, PutObjectResult putObjectResult, String name, String key) {
        // 获取图片信息
        ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
        // 数据库插入图片信息
        Image image = new Image();
        image.setName(name);
        image.setUrl(cosClientConfig.getHOST()+"/"+ key);
        image.setUserId(loginUser.getId());
        image.setSize(imageInfo.getQuality());
        image.setWidth(imageInfo.getWidth());
        image.setHeight(imageInfo.getHeight());
        image.setCreateTime(new Date());
        boolean save = imageService.save(image);
        ThrowUtil.throwIf(!save, ErrorCode.OPERATION_ERROR);
        // 构建返回结果
        ImageVO imageVO = new ImageVO();
        BeanUtil.copyProperties(image, imageVO);
        return imageVO;
    }

    /**
     * 文件/URL格式校验
     * @param dataSource
     */
    abstract void validator(Object dataSource) throws IOException;

    /**
     * 获取文件名
     * @param dataSource
     */
    abstract String getFilename(Object dataSource);

    /**
     * 获取文件
     * @param dataSource
     * @param file
     */
    abstract File getFileBySource(Object dataSource, File file) throws IOException;

}
