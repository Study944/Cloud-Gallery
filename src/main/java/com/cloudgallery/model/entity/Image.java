package com.cloudgallery.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图片表
 */
@TableName(value ="image")
@Data
public class Image implements Serializable {

    @TableId(type = IdType.AUTO)
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}