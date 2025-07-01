package com.cloudgallery.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户私有空间新增请求
 */
@Data
public class SpaceAddDto implements Serializable {

    /**
     * 空间名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    private static final long serialVersionUID = 1L;
}