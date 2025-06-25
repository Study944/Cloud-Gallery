package com.cloudgallery.manager.cos;

import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class FileUploadManager extends ImageUploadTemplate{

    @Override
    void validator(Object dataSource) throws IOException {
        // 1.校验文件后缀格式
        MultipartFile multipartFile = (MultipartFile) dataSource;
        String fileName = multipartFile.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        final List<String> fileSuffixList = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        ThrowUtil.throwIf(!fileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件格式错误");
        // 2.校验文件大小
        final long maxSize = 1024 * 1024 * 10;
        ThrowUtil.throwIf(multipartFile.getSize() > maxSize, ErrorCode.PARAMS_ERROR, "文件大小不能超过10M");
        // 3.校验文件合法性
        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        ThrowUtil.throwIf(bufferedImage == null, ErrorCode.PARAMS_ERROR, "文件内容不合法");
    }

    @Override
    String getFilename(Object dataSource) {
        MultipartFile multipartFile = (MultipartFile) dataSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    File getFileBySource(Object dataSource, File file) throws IOException {
        MultipartFile multipartFile = (MultipartFile) dataSource;
        multipartFile.transferTo(file);
        return file;
    }
}
