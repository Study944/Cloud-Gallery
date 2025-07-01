package com.cloudgallery.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpaceAddVO implements Serializable {


    /**
     * 空间id
     */
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


}
