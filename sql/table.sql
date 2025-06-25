# 建表
create database if not exists cloud_gallery;
use cloud_gallery;
# user用户表
create table if not exists user
(
    id  bigint primary key auto_increment,
    name varchar(255) not null,
    account varchar(255) not null,
    password varchar(255) not null,
    sex int default 1 comment '1-男，2-女',
    avatar varchar(1024) comment '头像',
    role varchar(255) default 'user' comment 'user/admin',
    create_time datetime default current_timestamp comment '创建时间',
    index idx_userId(id)
)comment '用户表' collate utf8mb4_unicode_ci;

# 图片表
create table if not exists image
(
    id  bigint primary key auto_increment,
    name varchar(255) not null,
    url varchar(1024) not null,
    user_id bigint not null,
    description varchar(1024) comment '描述',
    label varchar(255) comment '标签',
    size int default 0 comment '图片大小',
    width int default 0 comment '图片宽度',
    height int default 0 comment '图片高度',
    create_time datetime default current_timestamp comment '创建时间',
    index idx_imageId(id)
)