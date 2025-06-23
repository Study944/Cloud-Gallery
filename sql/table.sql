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
