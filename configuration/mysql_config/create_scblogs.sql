drop database if exists `scblog`;
create database `scblog`;
use `scblog`;

/* 设置数据库字符集为 utf8mb4, 能够存储任何 Unicode 字符 */
set names utf8mb4;
/* 关闭 MySQL 的外键约束检查 */
set FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config 存储应用程序或系统的配置信息
-- 索引类型 btree
-- 字符集 utf8
-- 排序规则 utf8_general_mysql500_ci(一种不区分大小写的排序规则)
-- 存储引擎 InnoDB
-- id 自增从 1 开始
-- 行格式为 compact
-- ----------------------------
drop table if exists `config`;
create table `config`
(
    `id`     int(10) unsigned                                                     not null auto_increment,
    `param`  varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '参数',
    `result` varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '值',
    primary key (`id`) using btree,
    unique index `param` (`param`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of config TODO : 配置信息相关
-- ----------------------------
insert into `config`
values (1, 'avatar_url', 'http://avatar.scblogs.cn/');
insert into `config`
values (2, 'test', 'test');

-- ----------------------------
-- Table structure for user
-- ----------------------------
drop table if exists `user`;
create table `user`
(
    `id`            int(11) unsigned                                                    not null auto_increment,
    `username`      varchar(20) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '用户名',
    `school_code`   int(11) unsigned                                                    null     default null comment '院校代码',
    `nickname`      varchar(20) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '昵称',
    `avatar_url`    varchar(50) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '头像链接',
    `register_time` datetime                                                            not null default current_timestamp comment '注册时间',
    `deleted`       int(10) unsigned                                                    not null default 0 comment '0为未删除，已删除时该值等于id，以避免唯一索引的异常',
    primary key (`id`) using btree,
    unique index `username` (`username`, `deleted`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
insert into `user`
values (1, 'stick', 10001, '阿杆', '1_stick', '2022-04-13 22:09:32', 0);
insert into `user`
values (2, 'scblogs', 10001, '校园博客', 'default_avatar.jpg', '2022-07-03 22:32:42', 0);

-- ----------------------------
-- Table structure for user_basic
-- ----------------------------
drop table if exists `user_basic`;
create table `user_basic`
(
    `username`         varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  not null comment '用户名',
    `realname`         varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null comment '真实姓名',
    `birthday`         datetime                                                             null     default null comment '生日',
    `intro`            varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '个人简介',
    `gender`           tinyint(1) unsigned                                                  null     default null comment '性别',
    `city_id`          int(11) unsigned                                                     null     default null comment '城市id',
    `province_id`      int(11) unsigned                                                     null     default null comment '省份id',
    `name_modify_time` datetime                                                             null     default null comment '用户名修改时间',
    `start_work_time`  datetime                                                             null     default null comment '开始工作的时间',
    `modified_time`    datetime                                                             not null default current_timestamp on update current_timestamp comment '信息修改时间',
    `deleted`          tinyint(3) unsigned                                                  not null default 0 comment '0为未删除，1为已删除',
    primary key (`username`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user_basic
-- ----------------------------
-- 暂无

-- ----------------------------
-- Table structure for user_education
-- ----------------------------
drop table if exists `user_education`;
create table `user_education`
(
    `user_id`             int(11) unsigned                                                     not null comment '用户名',
    `create_time`         datetime                                                             null     default current_timestamp comment '创建时间',
    `school`              varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null comment '学校名称',
    `school_id`           int(11) unsigned                                                     null     default null comment '学校id',
    `profession`          varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '专业',
    `degree`              tinyint(5) unsigned                                                  null     default null comment '学位程度',
    `start_time`          datetime                                                             null     default null comment '入学时间',
    `end_time`            datetime                                                             null     default null comment '毕业时间',
    `audit_img_url`       varchar(50) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null comment '图片链接',
    `safety_audit_status` varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '安全审核状态',
    `status`              varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '状态码',
    `modified_time`       datetime                                                             not null default current_timestamp on update current_timestamp comment '更新时间',
    `deleted`             tinyint(3) unsigned                                                  not null default 0 comment '0为未删除，1为已删除',
    primary key (`user_id`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user_education
-- ----------------------------
-- 暂无

-- ----------------------------
-- Table structure for user_follow
-- ----------------------------
drop table if exists `user_follow`;
create table `user_follow`
(
    `id`          int(11) unsigned                                                    not null auto_increment,
    `fans_id`     int(10) unsigned                                                    not null comment '粉丝id',
    `follow_id`   int(10) unsigned                                                    not null comment '关注id',
    `note`        varchar(50) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '备注',
    `status`      int(10) unsigned                                                    not null default 1 comment '状态码',
    `create_time` datetime                                                            not null default current_timestamp comment '创建时间',
    `deleted`     tinyint(3) unsigned                                                 not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    index `user_id` (`fans_id`) using btree,
    index `follow_id` (`follow_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user_follow
-- ----------------------------
insert into `user_follow`
values (1, 1, 2, null, 1, '2022-05-13 12:38:10', 0);
insert into `user_follow`
values (2, 2, 1, null, 1, '2022-05-13 03:41:00', 0);

-- ----------------------------
-- Table structure for user_general
-- ----------------------------
drop table if exists `user_general`;
create table `user_general`
(
    `id`          int(10) unsigned    not null auto_increment,
    `user_id`     int(11) unsigned    not null comment '用户id',
    `like_num`    int(11) unsigned    not null default 0 comment '用户获赞',
    `fans_num`    int(11) unsigned    not null default 0 comment '用户粉丝数',
    `comment_num` int(11) unsigned    not null default 0 comment '用户评论数',
    `collect_num` int(11) unsigned    not null default 0 comment '收藏博客数量',
    `view_num`    int(11) unsigned    not null default 0 comment '访问数',
    `blog_num`    int(11) unsigned    not null default 0 comment '用户博客数量',
    `week_rank`   int(11) unsigned    null     default null comment '周排行',
    `total_rank`  int(11) unsigned    null     default null comment '总排行',
    `deleted`     tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    unique index `index_user_general_user_id` (`user_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user_general
-- ----------------------------
insert into `user_general`
values (1, 1, 0, 0, 0, 0, 0, 0, null, null, 0);
insert into `user_general`
values (2, 2, 0, 0, 0, 0, 9, 0, null, null, 0);

-- ----------------------------
-- Table structure for user_safety
-- ----------------------------
drop table if exists `user_safety`;
create table `user_safety`
(
    `user_id`  int(10) unsigned                                                     not null comment '用户id',
    `username` varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  not null comment '用户名',
    `password` varchar(100) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '密码',
    `mail`     varchar(30) character set `utf8` collate `utf8_general_mysql500_ci`  not null comment '邮箱',
    `mobile`   varchar(11) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null comment '手机号',
    `deleted`  int(10) unsigned                                                     not null default 0 comment '0为未删除，已删除时该值等于id，以避免唯一索引的异常',
    primary key (`user_id`) using btree,
    unique index `mail` (`mail`, `deleted`) using btree,
    unique index `mobile` (`mobile`, `deleted`) using btree,
    index `index_user_safety_user_general_1` (`username`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of user_safety
-- ----------------------------
insert into `user_safety`
values (1, 'stick', '$2a$10$PDyiy9PeoTNn/Ad2VZmaeeVxoj1WhAlKV6AYEclcVtc4FnVSnHKEa', 'test1@qq.com', '111', 0);
insert into `user_safety`
values (2, 'scblogs', '$2a$10$PDyiy9PeoTNn/Ad2VZmaeeVxoj1WhAlKV6AYEclcVtc4FnVSnHKEa', 'test2@qq.com', '222', 0);

-- ----------------------------
-- View structure for user_view
-- ----------------------------
drop view if exists `user_view`;
create algorithm = undefined sql security definer view `user_view` as
select `user`.`id`                                    as `id`,
       `user`.`username`                              as `username`,
       `user`.`nickname`                              as `nickname`,
       `user`.`school_code`                           as `school_code`,
       concat(`config`.`result`, `user`.`avatar_url`) as `avatar_url`,
       `user`.`register_time`                         as `register_time`,
       `user`.`deleted`                               as `deleted`
from (`user`
    left join `config` on ((`config`.`param` = 'avatar_url')))
where (`user`.`deleted` = 0);

-- ----------------------------
-- View structure for fans_view
-- ----------------------------
drop view if exists `fans_view`;
create algorithm = undefined sql security definer view `fans_view` as
select `user_follow`.`id`          as `id`,
       `user_follow`.`follow_id`   as `user_id`,
       `user_follow`.`fans_id`     as `fans_id`,
       `user_follow`.`note`        as `note`,
       `user_follow`.`status`      as `status`,
       `user_follow`.`create_time` as `create_time`,
       `user_view`.`username`      as `username`,
       `user_view`.`nickname`      as `nickname`,
       `user_view`.`school_code`   as `school_code`,
       `user_view`.`avatar_url`    as `avatar_url`,
       `user_view`.`register_time` as `register_time`
from (`user_follow`
    left join `user_view` on ((`user_follow`.`fans_id` = `user_view`.`id`)))
where (`user_follow`.`deleted` = 0);

-- ----------------------------
-- View structure for follow_view
-- ----------------------------
drop view if exists `follow_view`;
create algorithm = undefined sql security definer view `follow_view` as
select `user_follow`.`id`          as `id`,
       `user_follow`.`fans_id`     as `user_id`,
       `user_follow`.`follow_id`   as `follow_id`,
       `user_follow`.`note`        as `note`,
       `user_follow`.`status`      as `status`,
       `user_follow`.`create_time` as `create_time`,
       `user_view`.`username`      as `username`,
       `user_view`.`nickname`      as `nickname`,
       `user_view`.`school_code`   as `school_code`,
       `user_view`.`avatar_url`    as `avatar_url`,
       `user_view`.`register_time` as `register_time`
from (`user_follow`
    left join `user_view` on ((`user_view`.`id` = `user_follow`.`follow_id`)))
where (`user_follow`.`deleted` = 0);

/* TODO : 外键相关 */
set FOREIGN_KEY_CHECKS = 1;


/*
 Create database `blog`

 Source Server         : scblogs
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 25/02/2023 15:11:40
*/
drop database if exists `blog`;
create database `blog`;
use `blog`;

set names utf8mb4;
set FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
drop table if exists `blog`;
create table `blog`
(
    `id`            int(11) unsigned                                                  not null auto_increment comment '博客id',
    `author_id`     int(11) unsigned                                                  not null comment '作者id',
    `title`         varchar(256) character set `utf8mb4` collate `utf8mb4_general_ci` not null comment '标题',
    `description`   varchar(256) character set `utf8mb4` collate `utf8mb4_general_ci` not null comment '描述',
    `school_code`   int(11) unsigned                                                  null     default null comment '院校代码',
    `cover_image`   char(32) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null comment '封面图',
    `release_time`  datetime                                                          null     default null comment '发表时间',
    `write_type`    tinyint(3) unsigned                                               not null default 1 comment '博客创作类型：1. 原创; 2. 转载',
    `status`        tinyint(3) unsigned                                               not null comment '发表状态（1表示已发表、2表示未发表、3为仅自己可见、4为回收站、5为审核中）',
    `create_time`   datetime                                                          not null default current_timestamp comment '创建时间',
    `modified_time` datetime                                                          not null default current_timestamp on update current_timestamp comment '修改时间',
    `deleted`       tinyint(3) unsigned                                               not null default 0 comment '是否已经删除，0未删除，1已删除',
    primary key (`id`) using btree,
    index `author_id` (`author_id`) using btree,
    index `school_code` (`school_code`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blog
-- ----------------------------
insert into `blog`
values (1, 1, '这是一篇测试文章1', '这是一篇测试文章1', null, null, null, 1, 2, '2022-11-29 23:13:17',
        '2022-11-29 23:13:16', 0);
insert into `blog`
values (2, 2, '这是一篇测试文章2', '这是一篇测试文章2', null, null, '2022-11-29 23:15:11', 1, 1, '2022-11-29 23:15:11',
        '2022-11-29 23:15:11', 0);
insert into `blog`
values (3, 2, '基于Spring-Data-Elasticsearch 优雅的实现 多字段搜索 + 高亮 + 分页 + 数据同步 功能',
        '技术要点\r\n1. 使用 *copyTo* 和  *ElasticsearchRepository* 完成的多字段搜索。\r\n2. 使用注解 *@Highlight* 和 *@HighlightField* 完成的高亮显示。\r\n3. 使用 *Pageable* 和 *SearchPage* 实现分页和高亮两不误的接口。\r\n4. 使用 *RabbitMQ* 完成 *MySQL* 和 *elasticsearch* 的数据同步。......',
        null, '7eaec2ec52592cd860526fb5469c9d58', '2022-07-13 11:09:08', 1, 1, '2022-07-13 11:09:08',
        '2023-01-13 15:25:08', 0);
insert into `blog`
values (4, 1, 'SpringCloud Fegin CircuitBreaker (原hystrix) 失效原因 —— 版本3.1.2',
        '今天想着给自己的微服务项目添加一个的功能，正好项目内远程调用是用的feign，那就直接用hystrix好了，也比较方便。然后呢，我以为比较简单的东西，却一直不生效，资料查了个遍，硬是弄了我一上午+一下午的时间，所以记录一下，给大家避坑。如果你的环境跟我差的有点多，那这篇文章可能对你的帮助不大。原因主要有二配置文件网上很多教程都是给的旧版本的配置文件，都是，但在新版本这是不生效的，正确的配置应该是下面我这样',
        null, 'b2105bb92a220e1f26a1772ba8d674f0', '2022-07-14 11:16:54', 1, 1, '2022-07-14 11:16:54',
        '2022-08-04 21:30:39', 0);
insert into `blog`
values (5, 1, 'MySQL占用内存过大解决方案',
        '对于部分小资玩家来说，服务器数量和内存往往是很有限的，像我个人的服务器配置就是2核4G5M。4G内存对于Java玩家来说，真的不大，开几个中间件+自己的微服务真的还蛮挤的，然后又摊上MySQL这个大冤种。我本机上的MySQL仅仅只占几M内存（虽然我不怎么用，但是本机MySQL确实是开着的）：而服务器的则要占400M，怎么说其实没什么吞吐量，纯粹是自己玩一玩，这内存占用属实有点吃不消啊。。。了解到可以通过修改配置来减小MySQL的内存占用，便尝试了一下，并进行了记录。由于我同时有两台服务器，一台装的是',
        null, '76ddaea9cd3b5f473712a3dc71381fe9', '2022-07-15 11:19:46', 1, 1, '2022-07-15 11:19:46',
        '2022-07-15 11:19:46', 0);
insert into `blog`
values (6, 1, '记一次云服务器被密码爆破的经历——关小黑屋、改密码、改端口',
        '当我登录时看到这条警告，我意识到我的服务器可能正在被爆破于是我马上通过指令  查询了登录失败的记录，输出结果令我大为震惊，大概刷新了五六秒？可能更多，登录记录显示，从本月的1号开始，到现在我写这篇博客，整整一个月，都一直有人在尝试登陆我的服务器。。。这些ip都是来自国外的，且攻击者目前还在尝试爆破，说明他可能还没有登录成功，于是我输入last指令，查看登录成功的ip：然后我把这些ip都查询了一遍，没有发现异常ip（这里都是我的ip，我就打码了）。到这，我暂时可以确定，攻击者还没有登录上我的服务器，那就不着',
        null, '1b758ffa5893eb149b2b59fdb84de221', '2022-07-16 11:21:07', 1, 1, '2022-07-16 11:21:07',
        '2022-07-16 11:21:07', 0);
insert into `blog`
values (7, 1, 'Postman 中级使用教程【环境变量、测试脚本、断言、接口文档等】',
        'Postman\r\n首先介绍一个转中文版的方式，GitHub链接：https://github.com/hlmd/Postman-cn\r\n目录Postman前言环境变量新建环境变量切换环境变量接口关联编写测试脚本动态参数内置动态参数自定义动态参数获取全局变量的方式：断言MokeService编写接口文档保存测试示例结语\r\n前言\r\n本文为postman中级使用教程，适合有一定的postman基础的小伙伴阅读，如果没有使用过',
        null, 'c6ef2efad320f2e4ab47aecb51f4c33e', '2022-07-17 11:22:54', 1, 1, '2022-07-17 11:22:54',
        '2022-07-17 11:22:54', 0);
insert into `blog`
values (8, 1, 'SpringBoot配置SSL 坑点总结【密码验证失败、连接不安全】',
        'SpringBoot配置SSL坑点总结，包括四种常见错误和不常见错误，导致出现 密码验证失败、网站不安全 的原因。', null,
        '9db4a9b6f54fc771d8ac5f3e02401f98', '2022-07-18 11:24:12', 1, 1, '2022-07-18 11:24:12', '2022-07-18 11:24:12',
        0);

-- ----------------------------
-- Table structure for blog_content
-- ----------------------------
drop table if exists `blog_content`;
create table `blog_content`
(
    `blog_id`       int(11) unsigned                                          not null comment '博客id',
    `content`       text character set `utf8mb4` collate `utf8mb4_general_ci` not null comment '博客内容',
    `modified_time` datetime                                                  not null default current_timestamp on update current_timestamp comment '修改时间',
    `deleted`       tinyint(3) unsigned                                       not null default 0 comment '0未删除，1已删除',
    primary key (`blog_id`) using btree,
    unique index `blog_id` (`blog_id`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blog_content
-- ----------------------------
insert into `blog_content`
values (1, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (2,
        '# 这是一篇测试文章\r\n## 这是一篇测试文章\r\n### 这是一篇测试文章\r\n::: hljs-right\r\n\r\n这是一篇测试文章这是一篇测试文章这是一篇测试文章这是一篇测试文章\r\n\r\n:::\r\n',
        '2022-11-29 23:15:11', 0);
insert into `blog_content`
values (3, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (4, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (5, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (6, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (7, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);
insert into `blog_content`
values (8, '加粗文本 加粗文本\n\n==标记文本==\n\n~~删除文本~~\n', '2022-10-26 16:16:18', 0);

-- ----------------------------
-- Table structure for blog_content_html
-- ----------------------------
drop table if exists `blog_content_html`;
create table `blog_content_html`
(
    `blog_id`       int(11) unsigned                                          not null comment '博客id',
    `content`       text character set `utf8mb4` collate `utf8mb4_general_ci` not null comment '博客内容（html）',
    `modified_time` datetime                                                  not null default current_timestamp on update current_timestamp,
    `deleted`       tinyint(3) unsigned                                       not null default 0,
    primary key (`blog_id`) using btree,
    unique index `blog_id` (`blog_id`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blog_content_html
-- ----------------------------
insert into `blog_content_html`
values (1, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (2,
        '<h1><a id=\"_0\"></a>这是一篇测试文章</h1>\r\n<h2><a id=\"_1\"></a>这是一篇测试文章</h2>\r\n<h3><a id=\"_2\"></a>这是一篇测试文章</h3>\r\n<div class=\"hljs-right\">\r\n<p>这是一篇测试文章这是一篇测试文章这是一篇测试文章这是一篇测试文章</p>\r\n</div>\r\n',
        '2022-11-29 23:15:11', 0);
insert into `blog_content_html`
values (3, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (4, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (5, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (6, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (7, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);
insert into `blog_content_html`
values (8, '<p>加粗文本 加粗文本</p>\n<p><mark>标记文本</mark></p>\n<p><s>删除文本</s></p>\n', '2022-10-26 16:16:18',
        0);

-- ----------------------------
-- Table structure for blog_general
-- ----------------------------
drop table if exists `blog_general`;
create table `blog_general`
(
    `blog_id`        int(10) unsigned    not null comment '博客id',
    `view_num`       int(10) unsigned    not null default 0 comment '浏览量',
    `like_num`       int(10) unsigned    not null default 0 comment '点赞量',
    `comment_num`    int(10) unsigned    not null default 0 comment '评论量',
    `collection_num` int(10) unsigned    not null default 0 comment '收藏量',
    `score`          int(10) unsigned    not null default 0 comment '评分',
    `deleted`        tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`blog_id`) using btree,
    unique index `blog_id` (`blog_id`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blog_general
-- ----------------------------
insert into `blog_general`
values (1, 0, 0, 0, 0, 30, 0);
insert into `blog_general`
values (2, 0, 0, 0, 0, 98, 0);
insert into `blog_general`
values (3, 0, 0, 0, 0, 102, 0);
insert into `blog_general`
values (4, 0, 0, 0, 0, 934, 0);
insert into `blog_general`
values (5, 0, 0, 0, 0, 984, 0);
insert into `blog_general`
values (6, 0, 0, 0, 0, 958, 0);
insert into `blog_general`
values (7, 0, 0, 0, 0, 198, 0);
insert into `blog_general`
values (8, 0, 0, 0, 0, 998, 0);

-- ----------------------------
-- Table structure for blog_set_tag
-- ----------------------------
drop table if exists `blog_set_tag`;
create table `blog_set_tag`
(
    `id`      int(10) unsigned    not null auto_increment,
    `tag_id`  int(10) unsigned    not null,
    `blog_id` int(10) unsigned    not null,
    `deleted` tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    index `index_tag_blog` (`tag_id`, `blog_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blog_set_tag
-- ----------------------------

-- ----------------------------
-- Table structure for collect_blog
-- ----------------------------
drop table if exists `collect_blog`;
create table `collect_blog`
(
    `id`          int(10) unsigned    not null auto_increment,
    `blog_id`     int(10) unsigned    not null,
    `user_id`     int(10) unsigned    not null,
    `create_time` datetime            not null default current_timestamp,
    `deleted`     tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    index `index_blog_collection_user_1` (`user_id`) using btree,
    index `index_blog_collection_blog_1` (`blog_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of collect_blog
-- ----------------------------
insert into `collect_blog`
values (1, 1, 1, '2022-06-08 22:12:25', 0);

-- ----------------------------
-- Table structure for column
-- ----------------------------
drop table if exists `column`;
create table `column`
(
    `id` int(11) not null comment '专栏id',
    primary key (`id`) using btree
) engine = InnoDB
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of column
-- ----------------------------

-- ----------------------------
-- Table structure for config
-- ----------------------------
drop table if exists `config`;
create table `config`
(
    `id`     int(10) unsigned                                                     not null auto_increment,
    `param`  varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '参数',
    `result` varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '值',
    primary key (`id`) using btree,
    unique index `param` (`param`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of config
-- ----------------------------
insert into `config`
values (1, 'cover_url', 'http://image.scblogs.cn/');
insert into `config`
values (2, 'test', 'test');

-- ----------------------------
-- Table structure for like_blog
-- ----------------------------
drop table if exists `like_blog`;
create table `like_blog`
(
    `id`          int(10) unsigned    not null auto_increment comment '点赞id',
    `blog_id`     int(10) unsigned    not null comment '博客id',
    `user_id`     int(10) unsigned    not null comment '用户id',
    `create_time` datetime            not null default current_timestamp on update current_timestamp comment '创建时间',
    `deleted`     tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    index `index_blog_likes_user_1` (`user_id`) using btree,
    index `index_blog_likes_blog_1` (`blog_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of like_blog
-- ----------------------------
insert into `like_blog`
values (1, 1, 2, '2022-06-08 22:12:12', 0);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
drop table if exists `tag`;
create table `tag`
(
    `id`            int(10) unsigned                                                     not null auto_increment,
    `name`          varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  not null,
    `alias`         varchar(20) character set `utf8` collate `utf8_general_mysql500_ci`  null     default null,
    `description`   varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null,
    `create_time`   datetime                                                             not null default current_timestamp,
    `modified_time` datetime                                                             null     default null on update current_timestamp,
    `deleted`       tinyint(3) unsigned                                                  not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- View structure for blog_view
-- ----------------------------
drop view if exists `blog_view`;
create algorithm = undefined sql security definer view `blog_view` as
select `blog`.`id`                                     as `id`,
       `blog`.`author_id`                              as `author_id`,
       `blog`.`title`                                  as `title`,
       `blog`.`description`                            as `description`,
       `blog`.`school_code`                            as `school_code`,
       concat(`config`.`result`, `blog`.`cover_image`) as `cover_image`,
       `blog`.`create_time`                            as `create_time`,
       `blog`.`release_time`                           as `release_time`,
       `blog`.`modified_time`                          as `modified_time`,
       `blog`.`status`                                 as `status`,
       `blog`.`write_type`                             as `write_type`,
       `blog_general`.`view_num`                       as `view_num`,
       `blog_general`.`like_num`                       as `like_num`,
       `blog_general`.`comment_num`                    as `comment_num`,
       `blog_general`.`collection_num`                 as `collection_num`,
       `blog_general`.`score`                          as `score`
from ((`blog` left join `blog_general` on ((`blog`.`id` = `blog_general`.`blog_id`)))
    left join `config` on ((`config`.`param` = 'cover_url')))
where (`blog`.`deleted` = 0);

set FOREIGN_KEY_CHECKS = 1;


/*
 Create database `resource`

 Source Server         : scblogs
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Schema         : resource

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 25/02/2023 15:11:22
*/
drop database if exists `resource`;
create database `resource`;
use `resource`;

set names utf8mb4;
set FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for city
-- ----------------------------
drop table if exists `city`;
create table `city`
(
    `id`          int(10) unsigned                                                    not null auto_increment comment '城市id',
    `name`        varchar(20) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '名称',
    `province_id` int(10) unsigned                                                    not null comment '所属省份id',
    primary key (`id`) using btree,
    index `province_id` (`province_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of city
-- ----------------------------
insert into `city`
values (1, '北京市', 1);

-- ----------------------------
-- Table structure for image
-- ----------------------------
drop table if exists `image`;
create table `image`
(
    `id`            int(10) unsigned                                                    not null auto_increment comment '图片id',
    `url`           varchar(50) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '资料路径',
    `visit`         int(10) unsigned                                                    not null default 0 comment '访问量',
    `create_time`   datetime                                                            not null default current_timestamp comment '创建时间',
    `modified_time` datetime                                                            not null default current_timestamp on update current_timestamp comment '修改时间',
    primary key (`id`) using btree,
    unique index `img` (`url`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of image
-- ----------------------------


-- ----------------------------
-- Table structure for province
-- ----------------------------
drop table if exists `province`;
create table `province`
(
    `id`   int(11) unsigned                                                    not null auto_increment,
    `name` varchar(20) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '省份名称',
    primary key (`id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = compact;

-- ----------------------------
-- Records of province
-- ----------------------------
insert into `province`
values (1, '北京市');

-- ----------------------------
-- Table structure for university
-- ----------------------------
drop table if exists `university`;
create table `university`
(
    `id`         int(11) unsigned                                                    not null auto_increment,
    `name`       varchar(30) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '学校名称',
    `identifier` int(11) unsigned                                                    not null comment '学校标识码',
    `code`       int(11) unsigned                                                    not null comment '院校代码',
    `city_id`    int(11) unsigned                                                    not null comment '所属城市',
    `rank`       smallint(6) unsigned                                                not null comment '办学层次（1本科，2专科）',
    `remark`     varchar(20) character set `utf8` collate `utf8_general_mysql500_ci` null default null comment '备注',
    primary key (`id`) using btree,
    unique index `code` (`code`) using btree,
    unique index `identifier` (`identifier`) using btree,
    index `name` (`name`) using btree,
    index `city_id` (`city_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8mb4`
  collate = `utf8mb4_unicode_ci`
  row_format = compact;

-- ----------------------------
-- Records of university
-- ----------------------------
insert into `university`
values (1, '北京大学', 4111010001, 10001, 1, 1, null);
insert into `university`
values (2, '中国人民大学', 4111010002, 10002, 1, 1, null);
insert into `university`
values (3, '清华大学', 4111010003, 10003, 1, 1, null);
insert into `university`
values (4, '北京交通大学', 4111010004, 10004, 1, 1, null);
insert into `university`
values (5, '北京工业大学', 4111010005, 10005, 1, 1, null);
insert into `university`
values (6, '北京航空航天大学', 4111010006, 10006, 1, 1, null);
insert into `university`
values (7, '北京理工大学', 4111010007, 10007, 1, 1, null);

-- ----------------------------
-- Table structure for visit_log
-- ----------------------------
drop table if exists `visit_log`;
create table `visit_log`
(
    `id`          int(10) unsigned                                                     not null auto_increment comment '主键',
    `ip`          int(10) unsigned                                                     not null comment 'ip地址',
    `method`      varchar(10) character set `utf8` collate `utf8_general_mysql500_ci`  not null comment '请求方法',
    `uri`         varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` not null comment '请求资源路径',
    `query_param` varchar(255) character set `utf8` collate `utf8_general_mysql500_ci` null     default null comment '请求url参数',
    `status`      int(10) unsigned                                                     not null default 200 comment '请求状态码',
    `user_id`     int(10) unsigned                                                     null     default null comment '用户id',
    `creat_time`  datetime                                                             not null default current_timestamp comment '请求发起时间',
    primary key (`id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of visit_log
-- ----------------------------
insert into `visit_log`
values (1, 2130706433, 'GET', '/', 'id=1', 404, null, '2023-01-18 20:57:51');
insert into `visit_log`
values (2, 2130706433, 'GET', '/', null, 404, null, '2023-01-18 21:04:05');
insert into `visit_log`
values (3, 2130706433, 'GET', '/', null, 404, null, '2023-01-18 21:04:56');
insert into `visit_log`
values (4, 2130706433, 'GET', '/user', null, 200, 1, '2023-01-18 21:42:43');
insert into `visit_log`
values (5, 2130706433, 'GET', '/user', null, 200, 1, '2023-01-18 21:43:51');
insert into `visit_log`
values (6, 2130706433, 'GET', '/user', null, 400, 1, '2023-01-18 22:05:27');
insert into `visit_log`
values (7, 2130706433, 'GET', '/user', null, 503, 1, '2023-01-18 22:05:57');
insert into `visit_log`
values (8, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:29');
insert into `visit_log`
values (9, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:31');
insert into `visit_log`
values (10, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:31');
insert into `visit_log`
values (11, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:31');
insert into `visit_log`
values (12, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:32');
insert into `visit_log`
values (13, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:32');
insert into `visit_log`
values (14, 2130706433, 'GET', '/blog/list/recommend', 'page=1', 200, 1, '2023-01-18 22:31:54');

-- ----------------------------
-- View structure for university_view
-- ----------------------------
drop view if exists `university_view`;
create algorithm = undefined sql security definer view `university_view` as
select `university`.`id`         as `id`,
       `university`.`name`       as `name`,
       `university`.`identifier` as `identifier`,
       `university`.`code`       as `code`,
       `province`.`name`         as `province`,
       `city`.`name`             as `city`,
       `university`.`rank`       as `rank`,
       `university`.`remark`     as `remark`
from ((`city` join `province`)
    join `university`)
where ((`province`.`id` = `city`.`province_id`) and (`city`.`id` = `university`.`city_id`));

-- ----------------------------
-- View structure for visit_log_view
-- ----------------------------
drop view if exists `visit_log_view`;
create algorithm = undefined sql security definer view `visit_log_view` as
select `visit_log`.`id`            as `id`,
       inet_ntoa(`visit_log`.`ip`) as `ip`,
       `visit_log`.`method`        as `method`,
       `visit_log`.`uri`           as `uri`,
       `visit_log`.`query_param`   as `query_param`,
       `visit_log`.`status`        as `status`,
       `visit_log`.`user_id`       as `user_id`,
       `visit_log`.`creat_time`    as `creat_time`
from `visit_log`;

set FOREIGN_KEY_CHECKS = 1;


/*
 Create database `blink`

 Source Server         : scblogs
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Host           : default.server.sticki.scblogs.cn:3306
 Source Schema         : blink

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 25/02/2023 15:11:49
*/
drop database if exists `blink`;
create database `blink`;
use `blink`;

set names utf8mb4;
set FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blink
-- ----------------------------
drop table if exists `blink`;
create table `blink`
(
    `id`            int(11) unsigned                                          not null auto_increment comment '动态id',
    `user_id`       int(11) unsigned                                          not null comment '用户id',
    `school_code`   int(11) unsigned                                          null     default null comment '学校代码',
    `content`       text character set `utf8mb4` collate `utf8mb4_unicode_ci` not null comment '动态内容',
    `create_time`   datetime                                                  not null default current_timestamp comment '发表时间',
    `modified_time` datetime                                                  not null default current_timestamp on update current_timestamp comment '修改时间',
    `deleted`       tinyint(3) unsigned                                       not null default 0 comment '0为未删除，1为已删除',
    primary key (`id`) using btree,
    index `index_blink_user_id` (`user_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8mb4`
  collate = `utf8mb4_unicode_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blink
-- ----------------------------
insert into `blink`
values (1, 1, 10001, '动态内容测试', '2022-05-29 17:58:34', '2023-01-16 11:08:01', 0);
insert into `blink`
values (2, 1, 10001, '动态内容58', '2022-05-29 17:59:09', '2022-05-29 18:00:23', 0);

-- ----------------------------
-- Table structure for blink_general
-- ----------------------------
drop table if exists `blink_general`;
create table `blink_general`
(
    `blink_id`    int(11) unsigned    not null comment '动态id',
    `view_num`    int(11) unsigned    not null default 0 comment '浏览量',
    `likes_num`   int(11) unsigned    not null default 0 comment '点赞量',
    `comment_num` int(11) unsigned    not null default 0 comment '评论量',
    `score`       double unsigned     not null default 0 comment '评分',
    `deleted`     tinyint(3) unsigned not null default 0 comment '0为未删除，1为已删除',
    primary key (`blink_id`) using btree
) engine = InnoDB
  character set = `utf8mb4`
  collate = `utf8mb4_unicode_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of blink_general
-- ----------------------------
insert into `blink_general`
values (1, 0, 0, 0, 0, 0);
insert into `blink_general`
values (2, 0, 0, 0, 0, 0);

-- ----------------------------
-- View structure for blink_view
-- ----------------------------
drop view if exists `blink_view`;
create algorithm = undefined sql security definer view `blink_view` as
select `blink`.`id`                  as `id`,
       `blink`.`user_id`             as `user_id`,
       `blink`.`school_code`         as `school_code`,
       `blink`.`content`             as `content`,
       `blink`.`create_time`         as `create_time`,
       `blink`.`modified_time`       as `modified_time`,
       `blink_general`.`view_num`    as `view_num`,
       `blink_general`.`likes_num`   as `likes_num`,
       `blink_general`.`comment_num` as `comment_num`,
       `blink_general`.`score`       as `score`
from (`blink`
    left join `blink_general` on ((`blink`.`id` = `blink_general`.`blink_id`)))
where (`blink`.`deleted` = 0);

set FOREIGN_KEY_CHECKS = 1;


/*
 Create database `comment`

 Source Server         : scblogs
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Host           : default.server.sticki.scblogs.cn:3306
 Source Schema         : comment

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 25/02/2023 15:11:32
*/
drop database if exists `comment`;
create database `comment`;
use `comment`;

set names utf8mb4;
set FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
drop table if exists `comment`;
create table `comment`
(
    `id`             int(10) unsigned                                             not null auto_increment comment 'id',
    `user_id`        int(10) unsigned                                             not null comment '用户id',
    `blog_id`        int(10) unsigned                                             not null comment '博客id',
    `content`        text character set `utf8` collate `utf8_general_mysql500_ci` not null comment '评论内容',
    `parent_id`      int(10) unsigned                                             null     default null comment '父评论id',
    `parent_user_id` int(10) unsigned                                             null     default null comment '父评论用户id',
    `create_time`    datetime                                                     not null default current_timestamp comment '创建时间',
    `deleted`        tinyint(3) unsigned                                          null     default 0 comment '是否已经删除，0未删除，1已删除',
    primary key (`id`) using btree,
    index `index_comment_user_id` (`user_id`) using btree,
    index `index_comment_blog_id` (`blog_id`) using btree
) engine = InnoDB
  auto_increment = 1
  character set = `utf8`
  collate = `utf8_general_mysql500_ci`
  row_format = dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
insert into `comment`
values (1, 1, 1, 'comment test', null, null, '2022-05-04 16:05:44', 0);
insert into `comment`
values (2, 1, 1, 'emmm', 1, 1, '2022-05-04 16:47:46', 0);
insert into `comment`
values (3, 1, 1, 'comment test', null, null, '2022-05-04 17:30:49', 0);
insert into `comment`
values (4, 1, 1, 'comment test', 1, 1, '2022-05-05 11:12:53', 0);
insert into `comment`
values (5, 2, 1, '1231', 1, 1, '2022-05-05 16:57:14', 0);
insert into `comment`
values (6, 2, 1, '12321', 5, 2, '2022-05-05 16:59:16', 0);

set FOREIGN_KEY_CHECKS = 1;

-- Over
show databases;
