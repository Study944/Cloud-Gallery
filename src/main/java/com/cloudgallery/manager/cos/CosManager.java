package com.cloudgallery.manager.cos;

import com.cloudgallery.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 对象存储上传工具类
 */
@Component
public class CosManager {

    @Resource
    private COSClient cosClient;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 图片文件上传
     * @param file
     * @param key
     * @return PutObjectResult
     */
    public PutObjectResult uploadFile(File file, String key) {
        // 1.获取存储桶名称
        String bucket = cosClientConfig.getBUCKET();
        // 2.创建上传请求
        PutObjectRequest request = new PutObjectRequest(bucket, key, file);
        // 3.上传
        PutObjectResult putObjectResult = cosClient.putObject(request);
        return putObjectResult;
    }

    /**
     * 图片文件上传，并添加图片处理参数
     * @param file
     * @param key
     * @return PutObjectResult
     */
    public PutObjectResult uploadFileWithData(File file, String key) {
        String bucket = cosClientConfig.getBUCKET();
        PutObjectRequest request = new PutObjectRequest(bucket, key, file);
        // 添加图片处理参数
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        request.setPicOperations(picOperations);
        PutObjectResult putObjectResult = cosClient.putObject(request);
        return putObjectResult;
    }

    /**
     * 图片文件下载
     * @param key
     * @return COSObject
     */
    public COSObject downloadFile(String key) {
        // 1.获取存储桶名称
        String bucket = cosClientConfig.getBUCKET();
        // 2.创建下载请求
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        // 3.下载
        COSObject cosObject = cosClient.getObject(request);
        return cosObject;
    }

}
