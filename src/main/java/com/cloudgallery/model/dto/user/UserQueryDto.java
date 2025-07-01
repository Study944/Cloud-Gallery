package com.cloudgallery.model.dto.user;

import lombok.Data;

@Data
public class UserQueryDto {
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
     * 用户名
     */
    private String name;

    /**
     * 账号
     */
    private String account;

    /**
     * 性别
     */
    private Integer sex;

}
