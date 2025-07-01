package com.cloudgallery.model.dto.image;

import lombok.Data;

import java.util.Date;

@Data
public class ImageQueryDto {
    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = "descend";

    /**
     * 图片ID
     */
    private Long id;

    /**
     * 图片名称
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
     * 标签
     */
    private String label;

    /**
     * 图片空间ID
     */
    private Long spaceId;

    /**
     * 图片大小
     */
    private Long size;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片状态: 0-禁用,1-正常
     */
    private Integer status;

    /**
     * 审核状态: 0-待审核, 1-通过, 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 创建时间
     */
    private Date createTime;

}
