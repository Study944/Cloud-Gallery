package com.cloudgallery.manager.cos;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.config.CosClientConfig;
import com.cloudgallery.controller.ImageController;
import com.cloudgallery.exception.BusinessException;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.service.impl.SpaceServiceImpl;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public abstract class ImageUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;
    @Resource
    private ImageService imageService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SpaceService spaceService;

    public ImageVO uploadImage(Object dataSource, User loginUser, ImageUploadDto imageUploadDto) throws IOException {
        // 1.文件/URL格式校验
        validator(dataSource);
        // 2.设置图片上传信息
        String name= getFilename(dataSource);
        String filename = DateUtil.formatDate(new Date())+"_"+RandomUtil.randomString(6) +"_"+ name;
        String key = String.format("public/%s/%s",loginUser.getId(),filename);
        if (imageUploadDto.getSpaceId()!=null&&imageUploadDto.getSpaceId()!=0){
            key = String.format("private/%s/%s",imageUploadDto.getSpaceId(),filename);
        }
        // 3.图片上传
        File file = null;
        ImageVO imageVO;
        try {
            // 3.1获取文件/URL对应的图片
            file = getFileBySource(dataSource, File.createTempFile(key, null));
            // 3.2上传图片到COS并获取图片信息
            PutObjectResult putObjectResult = cosManager.uploadFileWithData(file, key);
            // 3.3数据库插入图片信息
            String imageName = imageUploadDto.getName();
            if (imageName==null) imageUploadDto.setName(name);
            imageVO = getImage(loginUser, putObjectResult,imageUploadDto, key,file.length());
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
    private ImageVO getImage(User loginUser, PutObjectResult putObjectResult, ImageUploadDto imageUploadDto, String key , long size) {
        // 获取图片信息
        ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
        String name = imageUploadDto.getName();
        Long spaceId = imageUploadDto.getSpaceId();
        if (spaceId== null) spaceId = 0L;
        String description = imageUploadDto.getDescription();
        String label = imageUploadDto.getLabel();
        // 数据库插入图片信息
        Image image = new Image();
        image.setName(name);
        image.setUrl(cosClientConfig.getHOST()+"/"+ key);
        image.setUserId(loginUser.getId());
        image.setSize(size);
        image.setWidth(imageInfo.getWidth());
        image.setHeight(imageInfo.getHeight());
        image.setFormat(imageInfo.getFormat());
        // 设置空间id
        image.setSpaceId(spaceId);
        if (spaceId>0L) image.setAuditStatus(1);
        image.setLabel(label);
        image.setDescription(description);
        image.setUpdateTime(new Date());
        image.setCreateTime(new Date());
        final Long finalSpaceId = spaceId;
        // 使用编程式事务保证图片插入成功和空间额度扣除成功
        transactionTemplate.execute(status -> {
            // 插入图片信息
            boolean save = imageService.save(image);
            ThrowUtil.throwIf(!save, ErrorCode.OPERATION_ERROR);
            // 扣减空间额度
            if (finalSpaceId>0&&finalSpaceId!=null){
                Space space = spaceService.getById(finalSpaceId);
                space.setSize(space.getSize()+size);
                space.setNumber(space.getNumber()+1);
                boolean update = spaceService.updateById(space);
                ThrowUtil.throwIf(!update, ErrorCode.OPERATION_ERROR);
            }
            return true;
        });
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
