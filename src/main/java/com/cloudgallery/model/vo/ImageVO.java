package com.cloudgallery.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 图片表
 */
@Data
public class ImageVO{

    private Long id;

    private String name;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String label;

    /**
     * 图片大小
     */
    private Integer size;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 创建时间
     */
    private Date createTime;

}