package com.cloudgallery.model.dto.image;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片更新请求表
 */
@Data
public class ImageUpdateDto{

    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String label;


}