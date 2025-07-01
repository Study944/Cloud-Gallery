package com.cloudgallery.model.dto.space;

import lombok.Data;

@Data
public class SpaceUpdateDto {

    private Long id;

    /**
     * 空间名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

}
