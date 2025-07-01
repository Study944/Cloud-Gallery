package com.cloudgallery.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName image
 */
@TableName(value ="image")
@Data
public class Image implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图片名
     */
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
     * 图片格式
     */
    private String format;

    /**
     * 标签
     */
    private String label;

    /**
     * 图片空间ID
     */
    private Long spaceId;

    /**
     * 图片状态: 0-禁用,1-正常
     */
    private Integer status;

    /**
     * 审核状态: 0-待审核, 1-通过, 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审核不通过原因
     */
    private String auditReason;

    /**
     * 审核人ID
     */
    private Long auditId;

    /**
     * 图片大小
     */
    private Long size;

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

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}