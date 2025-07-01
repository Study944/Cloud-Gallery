package com.cloudgallery.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户私有空间表
 * @TableName space
 */
@TableName(value ="space")
@Data
@Builder
public class Space implements Serializable {
    /**
     * 空间id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 空间名称
     */
    private String name;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 空间等级: 0-普通，1-VIP，2-SVip
     */
    private Integer level;

    /**
     * 空间最大容量
     */
    private Long maxSize;

    /**
     * 空间图片总大小
     */
    private Long size;

    /**
     * 空间图片最大数量
     */
    private Long maxNumber;

    /**
     * 空间图片数量
     */
    private Long number;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}