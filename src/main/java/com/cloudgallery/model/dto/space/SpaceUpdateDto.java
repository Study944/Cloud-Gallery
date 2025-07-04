package com.cloudgallery.model.dto.space;

import lombok.Data;

@Data
public class SpaceUpdateDto {

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 空间名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

}
