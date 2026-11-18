-- =====================================================================
-- 07-banner-demo.sql  开发环境 Banner 演示数据（生产禁止执行）
-- 前置：01、02
-- 执行：mysql -uroot -p site < 07-banner-demo.sql
-- 说明：固定主键 + INSERT IGNORE；含 1 条已过期（验证前台时段过滤）、1 条非首页位置
-- =====================================================================
SET NAMES utf8mb4;

INSERT IGNORE INTO `cms_banner`
(`banner_id`, `position`, `media_type`, `image_url`, `video_url`, `title`, `subtitle`,
 `btn_text`, `link_url`, `sort`, `start_time`, `end_time`, `status`, `create_by`, `create_time`)
VALUES
(1, 'home', '1', '/images/banner/b1.jpg', NULL, '让厨房更懂生活', '2026 新品系列',
 '了解更多', '/product', 1, '2026-01-01 00:00:00', '2027-12-31 23:59:59', '0', 'admin', NOW()),
(2, 'home', '1', '/images/banner/b2.jpg', NULL, '大风量 · 净烟体验', 'J6019HS 新品上市',
 '查看产品', '/product/d/1', 2, NULL, NULL, '0', 'admin', NOW()),
(3, 'home', '1', '/images/banner/b3.jpg', NULL, '已过期 Banner', '不应在前台出现',
 '', '', 3, '2025-01-01 00:00:00', '2025-12-31 23:59:59', '0', 'admin', NOW()),
(4, 'product', '1', '/images/banner/p1.jpg', NULL, '产品中心 Banner', '仅产品中心展示',
 '去首页', '/', 1, NULL, NULL, '0', 'admin', NOW());
