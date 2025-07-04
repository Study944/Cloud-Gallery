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
    format varchar(255) comment '图片格式',
    label varchar(255) comment '标签',
    status tinyint default 1 comment '图片状态: 0-禁用,1-正常',
    audit_status tinyint default 0 comment '审核状态: 0-待审核, 1-通过, 2-拒绝',
    audit_reason varchar(512) comment '审核不通过原因',
    audit_id tinyint comment '审核人id',
    size bigint default 0 comment '图片大小',
    width int default 0 comment '图片宽度',
    height int default 0 comment '图片高度',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp comment '更新时间',
    index idx_imageId(id)
)comment '图片表' collate utf8mb4_unicode_ci;

# 添加审核人id
alter table image add column audit_id tinyint comment '审核人id';

# 用户私有空间表
create table if not exists space
(
    id  bigint primary key auto_increment comment '空间id',
    name varchar(255) not null comment '空间名称',
    user_id bigint not null comment '用户id',
    description varchar(1024) comment '描述',
    level tinyint default 0 comment '空间等级: 0-普通，1-VIP，2-SVip',
    max_size bigint default 1024 comment '空间最大容量',
    size bigint default 0 comment '空间图片总大小',
    max_number bigint default 1024 comment '空间图片最大数量',
    number bigint default 0 comment '空间图片数量',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp comment '更新时间',
    index idx_spaceId(id),index idx_userId(user_id)
)comment '用户私有空间表' collate utf8mb4_unicode_ci;

# 图片表添加空间Id字段
alter table image add column space_id bigint default 0 comment '空间id';

# 空间表添加空间类型字段
alter table space add column space_type tinyint default 0 comment '空间类型: 0-个人，1-团队';

# 用户-团队空间关系表
create table if not exists user_space
(
    id  bigint primary key auto_increment comment '关系id',
    user_id bigint not null comment '用户id',
    space_id bigint not null comment '空间id',
    space_role varchar(255) default 'viewer' comment 'viewer,editor,admin',
    create_time datetime default current_timestamp comment '创建时间',
    index idx_spaceId(space_id)
)comment '用户-团队空间关系表' collate utf8mb4_unicode_ci;



























