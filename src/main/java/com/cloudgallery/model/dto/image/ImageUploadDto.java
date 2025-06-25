package com.cloudgallery.model.dto.image;

import lombok.Data;

/**
 * 图片上传DTO
 */
@Data
public class ImageUploadDto{
    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片url
     */
    private String url;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String label;


}