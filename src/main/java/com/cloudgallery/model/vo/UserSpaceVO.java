package com.cloudgallery.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserSpaceVO {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * viewer,editor,admin
     */
    private String spaceRole;

    /**
     * 创建时间
     */
    private Date createTime;

}
