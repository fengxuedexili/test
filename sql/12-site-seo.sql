-- =====================================================================
-- 12-site-seo.sql  站点 SEO 基础配置（sys_config）
-- 说明：site.baseUrl 用于 sitemap/robots/分享链接的绝对地址
-- 执行：mysql -uroot -p site < 12-site-seo.sql
-- =====================================================================
SET NAMES utf8mb4;

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`)
SELECT '站点访问地址', 'site.baseUrl', 'http://localhost', 'N', 'admin', NOW(), 'sitemap/robots 使用；生产填 https://www.example.com'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_config` WHERE `config_key` = 'site.baseUrl');
