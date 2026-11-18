-- =====================================================================
-- 02-cms-schema.sql  企业官网内容表结构
-- 数据库：MySQL 8.0+（需支持 json 字段）
-- 前置  ：已执行 01-sys-base-schema.sql（sys_* 系统表）
-- 执行  ：mysql -uroot -p site < 02-cms-schema.sql
-- 说明  ：字符集 utf8mb4；通用字段与若依 BaseEntity 对齐；CREATE TABLE IF NOT EXISTS 可重复执行
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 1. 栏目表（产品分类 / 资讯分类，树形）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_category` (
  `category_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '栏目ID',
  `parent_id`       bigint       NOT NULL DEFAULT 0      COMMENT '父栏目ID（0为顶级）',
  `name`            varchar(64)  NOT NULL                COMMENT '栏目名称',
  `slug`            varchar(64)  NOT NULL                COMMENT 'URL别名',
  `type`            char(1)      NOT NULL DEFAULT '1'    COMMENT '类型（1产品 2资讯）',
  `cover`           varchar(255)          DEFAULT NULL   COMMENT '栏目封面',
  `summary`         varchar(500)          DEFAULT NULL   COMMENT '栏目简介',
  `sort`            int          NOT NULL DEFAULT 0      COMMENT '显示顺序',
  `seo_title`       varchar(128)          DEFAULT NULL   COMMENT 'SEO标题',
  `seo_keywords`    varchar(255)          DEFAULT NULL   COMMENT 'SEO关键词',
  `seo_description` varchar(255)          DEFAULT NULL   COMMENT 'SEO描述',
  `status`          char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `del_flag`        char(1)      NOT NULL DEFAULT '0'    COMMENT '删除标志（0存在 2删除）',
  `create_by`       varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time`     datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`       varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time`     datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`          varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_slug` (`slug`),
  KEY `idx_category_parent` (`parent_id`),
  KEY `idx_category_type` (`type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS栏目表';

-- ---------------------------------------------------------------------
-- 2. 系列表（同栏目下的产品分组，如“魔尔套系 Pro”）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_series` (
  `series_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '系列ID',
  `category_id` bigint       NOT NULL                COMMENT '所属栏目ID',
  `name`        varchar(64)  NOT NULL                COMMENT '系列名称',
  `subtitle`    varchar(128)          DEFAULT NULL   COMMENT '副标题',
  `cover`       varchar(255)          DEFAULT NULL   COMMENT '系列封面',
  `sort`        int          NOT NULL DEFAULT 0      COMMENT '显示顺序',
  `status`      char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `del_flag`    char(1)      NOT NULL DEFAULT '0'    COMMENT '删除标志（0存在 2删除）',
  `create_by`   varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time` datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time` datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`      varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`series_id`),
  KEY `idx_series_category` (`category_id`,`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS系列表';

-- ---------------------------------------------------------------------
-- 3. 产品表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_product` (
  `product_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `category_id`     bigint       NOT NULL                COMMENT '栏目ID',
  `series_id`       bigint                DEFAULT NULL   COMMENT '系列ID',
  `model`           varchar(64)  NOT NULL                COMMENT '型号',
  `name`            varchar(128) NOT NULL                COMMENT '产品名称',
  `subtitle`        varchar(255)          DEFAULT NULL   COMMENT '一句话卖点',
  `cover`           varchar(255)          DEFAULT NULL   COMMENT '主图',
  `images`          json                  DEFAULT NULL   COMMENT '图集 ["url1","url2"]',
  `video_url`       varchar(255)          DEFAULT NULL   COMMENT '产品视频',
  `selling_points`  json                  DEFAULT NULL   COMMENT '卖点 [{"icon":"","title":"","desc":""}]',
  `params`          json                  DEFAULT NULL   COMMENT '参数分组 [{"group":"","items":[{"label":"","value":"","unit":""}]}]',
  `downloads`       json                  DEFAULT NULL   COMMENT '下载 [{"name":"","url":"","size":""}]',
  `tags`            varchar(255)          DEFAULT NULL   COMMENT '标签（逗号分隔）',
  `is_recommend`    char(1)      NOT NULL DEFAULT '0'    COMMENT '是否推荐（0否 1是）',
  `sort`            int          NOT NULL DEFAULT 0      COMMENT '显示顺序',
  `publish_time`    datetime              DEFAULT NULL   COMMENT '发布时间',
  `view_count`      int          NOT NULL DEFAULT 0      COMMENT '浏览量',
  `status`          char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `del_flag`        char(1)      NOT NULL DEFAULT '0'    COMMENT '删除标志（0存在 2删除）',
  `seo_title`       varchar(128)          DEFAULT NULL   COMMENT 'SEO标题',
  `seo_keywords`    varchar(255)          DEFAULT NULL   COMMENT 'SEO关键词',
  `seo_description` varchar(255)          DEFAULT NULL   COMMENT 'SEO描述',
  `create_by`       varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time`     datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`       varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time`     datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`          varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`product_id`),
  KEY `idx_product_category` (`category_id`,`status`,`sort`),
  KEY `idx_product_series` (`series_id`),
  KEY `idx_product_recommend` (`is_recommend`,`status`,`sort`),
  KEY `idx_product_model` (`model`),
  KEY `idx_product_publish` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS产品表';

-- ---------------------------------------------------------------------
-- 4. Banner 轮播表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_banner` (
  `banner_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT 'BannerID',
  `position`    varchar(32)  NOT NULL DEFAULT 'home' COMMENT '位置（home/product/brand）',
  `media_type`  char(1)      NOT NULL DEFAULT '1'    COMMENT '媒体类型（1图片 2视频）',
  `image_url`   varchar(255)          DEFAULT NULL   COMMENT '图片/视频封面',
  `video_url`   varchar(255)          DEFAULT NULL   COMMENT '视频地址',
  `title`       varchar(128)          DEFAULT NULL   COMMENT '主标题',
  `subtitle`    varchar(255)          DEFAULT NULL   COMMENT '副标题',
  `btn_text`    varchar(64)           DEFAULT NULL   COMMENT '按钮文案',
  `link_url`    varchar(255)          DEFAULT NULL   COMMENT '跳转链接',
  `sort`        int          NOT NULL DEFAULT 0      COMMENT '显示顺序',
  `start_time`  datetime              DEFAULT NULL   COMMENT '开始时间（空为不限）',
  `end_time`    datetime              DEFAULT NULL   COMMENT '结束时间（空为不限）',
  `status`      char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `create_by`   varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time` datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time` datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`      varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`banner_id`),
  KEY `idx_banner_pos` (`position`,`status`,`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS轮播表';

-- ---------------------------------------------------------------------
-- 5. 文章表（资讯 / 品牌故事）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_article` (
  `article_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `category_id`     bigint                DEFAULT NULL   COMMENT '资讯分类ID',
  `title`           varchar(200) NOT NULL                COMMENT '标题',
  `cover`           varchar(255)          DEFAULT NULL   COMMENT '封面',
  `summary`         varchar(500)          DEFAULT NULL   COMMENT '摘要',
  `content`         longtext              DEFAULT NULL   COMMENT '正文HTML（入库前过滤）',
  `author`          varchar(64)           DEFAULT NULL   COMMENT '作者',
  `source`          varchar(64)           DEFAULT NULL   COMMENT '来源',
  `view_count`      int          NOT NULL DEFAULT 0      COMMENT '浏览量',
  `is_top`          char(1)      NOT NULL DEFAULT '0'    COMMENT '是否置顶（0否 1是）',
  `publish_time`    datetime              DEFAULT NULL   COMMENT '发布时间',
  `status`          char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `del_flag`        char(1)      NOT NULL DEFAULT '0'    COMMENT '删除标志（0存在 2删除）',
  `seo_title`       varchar(128)          DEFAULT NULL   COMMENT 'SEO标题',
  `seo_keywords`    varchar(255)          DEFAULT NULL   COMMENT 'SEO关键词',
  `seo_description` varchar(255)          DEFAULT NULL   COMMENT 'SEO描述',
  `create_by`       varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time`     datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`       varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time`     datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`          varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`article_id`),
  KEY `idx_article_category` (`category_id`,`status`,`publish_time`),
  KEY `idx_article_top` (`is_top`,`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS文章表';

-- ---------------------------------------------------------------------
-- 6. 单页表（品牌 / 隐私 / 服务说明，区块化）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_page` (
  `page_id`         bigint       NOT NULL AUTO_INCREMENT COMMENT '单页ID',
  `slug`            varchar(64)  NOT NULL                COMMENT 'URL别名（about/privacy）',
  `title`           varchar(128) NOT NULL                COMMENT '页面标题',
  `blocks`          json                  DEFAULT NULL   COMMENT '区块数组 [{"type":"richText","props":{}}]',
  `status`          char(1)      NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `seo_title`       varchar(128)          DEFAULT NULL   COMMENT 'SEO标题',
  `seo_keywords`    varchar(255)          DEFAULT NULL   COMMENT 'SEO关键词',
  `seo_description` varchar(255)          DEFAULT NULL   COMMENT 'SEO描述',
  `create_by`       varchar(64)           DEFAULT ''     COMMENT '创建者',
  `create_time`     datetime              DEFAULT NULL   COMMENT '创建时间',
  `update_by`       varchar(64)           DEFAULT ''     COMMENT '更新者',
  `update_time`     datetime              DEFAULT NULL   COMMENT '更新时间',
  `remark`          varchar(500)          DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`page_id`),
  UNIQUE KEY `uk_page_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS单页表';

-- ---------------------------------------------------------------------
-- 7. 门店表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_store` (
  `store_id`    bigint        NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `name`        varchar(128)  NOT NULL                COMMENT '门店名称',
  `province`    varchar(32)           DEFAULT NULL    COMMENT '省',
  `city`        varchar(32)           DEFAULT NULL    COMMENT '市',
  `district`    varchar(32)           DEFAULT NULL    COMMENT '区/县',
  `address`     varchar(255)          DEFAULT NULL    COMMENT '详细地址',
  `lng`         decimal(10,6)         DEFAULT NULL    COMMENT '经度（高德坐标系）',
  `lat`         decimal(10,6)         DEFAULT NULL    COMMENT '纬度（高德坐标系）',
  `phone`       varchar(32)           DEFAULT NULL    COMMENT '电话',
  `type`        char(1)       NOT NULL DEFAULT '2'    COMMENT '类型（1旗舰店 2专卖店 3服务网点）',
  `sort`        int           NOT NULL DEFAULT 0      COMMENT '显示顺序',
  `status`      char(1)       NOT NULL DEFAULT '0'    COMMENT '状态（0正常 1停用）',
  `del_flag`    char(1)       NOT NULL DEFAULT '0'    COMMENT '删除标志（0存在 2删除）',
  `create_by`   varchar(64)            DEFAULT ''     COMMENT '创建者',
  `create_time` datetime               DEFAULT NULL   COMMENT '创建时间',
  `update_by`   varchar(64)            DEFAULT ''     COMMENT '更新者',
  `update_time` datetime               DEFAULT NULL   COMMENT '更新时间',
  `remark`      varchar(500)           DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`store_id`),
  KEY `idx_store_area` (`province`,`city`,`status`),
  KEY `idx_store_type` (`type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS门店表';

-- ---------------------------------------------------------------------
-- 8. 线索表（预约/加盟/招聘/售后）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_lead` (
  `lead_id`     bigint        NOT NULL AUTO_INCREMENT COMMENT '线索ID',
  `type`        char(1)       NOT NULL DEFAULT '1'    COMMENT '类型（1预约 2加盟 3招聘 4售后）',
  `name`        varchar(64)            DEFAULT NULL   COMMENT '姓名',
  `phone`       varchar(20)   NOT NULL                COMMENT '手机号',
  `province`    varchar(32)            DEFAULT NULL   COMMENT '省',
  `city`        varchar(32)            DEFAULT NULL   COMMENT '市',
  `district`    varchar(32)            DEFAULT NULL   COMMENT '区/县',
  `product_id`  bigint                 DEFAULT NULL   COMMENT '关联产品ID',
  `content`     varchar(1000)          DEFAULT NULL   COMMENT '留言/需求',
  `source`      varchar(64)            DEFAULT NULL   COMMENT '来源页面',
  `utm`         varchar(500)           DEFAULT NULL   COMMENT '投放参数',
  `ip`          varchar(64)            DEFAULT NULL   COMMENT '提交IP',
  `status`      char(1)       NOT NULL DEFAULT '0'    COMMENT '状态（0待处理 1已联系 2无效 3已成交）',
  `handler`     varchar(64)            DEFAULT NULL   COMMENT '处理人',
  `handle_time` datetime               DEFAULT NULL   COMMENT '处理时间',
  `create_by`   varchar(64)            DEFAULT ''     COMMENT '创建者',
  `create_time` datetime               DEFAULT NULL   COMMENT '创建时间',
  `update_by`   varchar(64)            DEFAULT ''     COMMENT '更新者',
  `update_time` datetime               DEFAULT NULL   COMMENT '更新时间',
  `remark`      varchar(500)           DEFAULT NULL   COMMENT '备注',
  PRIMARY KEY (`lead_id`),
  KEY `idx_lead_phone` (`phone`),
  KEY `idx_lead_status` (`status`,`create_time`),
  KEY `idx_lead_type` (`type`),
  KEY `idx_lead_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CMS线索表';

SET FOREIGN_KEY_CHECKS = 1;

