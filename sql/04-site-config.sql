-- =====================================================================
-- 04-site-config.sql  站点基础参数（sys_config，键前缀 site.）
-- 前置：已执行 01-sys-base-schema.sql
-- 执行：mysql -uroot -p site < 04-site-config.sql
-- 说明：幂等（已存在同 config_key 时跳过）；后台「系统管理 → 参数设置」可维护
-- =====================================================================
SET NAMES utf8mb4;

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '站点名称', 'site.name', 'site 官网', 'N', 'admin', NOW(), '官网品牌名称'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.name');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '站点简称', 'site.shortName', 'site', 'N', 'admin', NOW(), '用于标题与 Logo 文案'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.shortName');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '站点 Logo', 'site.logo', '', 'N', 'admin', NOW(), '图片 URL（OSS/CDN）'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.logo');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '客服电话', 'site.phone', '', 'N', 'admin', NOW(), '页脚展示'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.phone');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '联系邮箱', 'site.email', '', 'N', 'admin', NOW(), '页脚展示'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.email');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT 'ICP 备案号', 'site.icp', '', 'N', 'admin', NOW(), '工信部备案号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.icp');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '版权信息', 'site.copyright', '© 2026 site', 'N', 'admin', NOW(), '页脚版权'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.copyright');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT 'SEO 关键词', 'site.keywords', '', 'N', 'admin', NOW(), '默认 meta keywords'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.keywords');

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT 'SEO 描述', 'site.description', '', 'N', 'admin', NOW(), '默认 meta description'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.description');
