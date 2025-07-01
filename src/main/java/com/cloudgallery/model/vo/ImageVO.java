package com.cloudgallery.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.cloudgallery.model.entity.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片表
 */
@Data
public class ImageVO implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    public static ImageVO imageToImageVO(Image image) {
        ImageVO imageVO = new ImageVO();
        BeanUtil.copyProperties(image, imageVO);
        return imageVO;
    }
}