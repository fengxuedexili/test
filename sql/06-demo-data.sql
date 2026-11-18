-- =====================================================================
-- 06-demo-data.sql  开发环境演示数据（仅用于联调与验收，生产库禁止执行）
-- 前置：01-sys-base-schema.sql、02-cms-schema.sql
-- 执行：mysql -uroot -p site < 06-demo-data.sql
-- 说明：固定主键 + INSERT IGNORE，可重复执行；图片/PDF 为占位路径，Day3 前端补齐
-- =====================================================================
SET NAMES utf8mb4;

-- 栏目：1 个根 + 3 个产品子栏目
INSERT IGNORE INTO `cms_category` (`category_id`, `parent_id`, `name`, `slug`, `type`, `cover`, `summary`, `sort`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
(1, 0, '厨房电器', 'kitchen',        '1', '', '厨房电器总览', 1, '0', '0', 'admin', NOW()),
(2, 1, '吸油烟机', 'range-hood',     '1', '', '大风量 · 静音 · 易清洁', 1, '0', '0', 'admin', NOW()),
(3, 1, '燃气灶具', 'cooktop',        '1', '', '猛火 · 稳焰 · 安全熄火保护', 2, '0', '0', 'admin', NOW()),
(4, 1, '热水器',   'water-heater',   '1', '', '恒温 · 节能 · 大水量', 3, '0', '0', 'admin', NOW());

-- 产品（5 条，其中 5 号为停用状态，用于验证前台过滤）
INSERT IGNORE INTO `cms_product`
(`product_id`, `category_id`, `model`, `name`, `subtitle`, `cover`, `images`, `selling_points`, `params`, `downloads`, `tags`, `is_recommend`, `sort`, `publish_time`, `view_count`, `status`, `del_flag`, `seo_title`, `seo_description`, `create_by`, `create_time`)
VALUES
(1, 2, 'CXW-260-J6019HS', '欧式顶吸烟机 J6019HS', '24m³/min 大风量 · 蒸汽洗',
 '/images/demo/p1-cover.jpg', '["/images/demo/p1-1.jpg","/images/demo/p1-2.jpg"]',
 '[{"icon":"wind","title":"24m³ 大风量","desc":"爆炒不跑烟"}]',
 '[{"group":"主要参数","items":[{"label":"风量","value":"24","unit":"m³/min"},{"label":"最大静压","value":"1000","unit":"Pa"}]}]',
 '[{"name":"产品说明书","url":"/files/demo/p1.pdf","size":"2.1MB"}]',
 '顶吸,大吸力', '1', 1, '2026-11-01 10:00:00', 128, '0', '0',
 'J6019HS 欧式顶吸烟机 - site 官网', '24m³/min 大风量，蒸汽自清洁', 'admin', NOW()),
(2, 2, 'CXW-200-J6058', '侧吸式烟机 J6058', '低空近吸 · 不碰头',
 '/images/demo/p2-cover.jpg', '["/images/demo/p2-1.jpg"]',
 '[{"icon":"smoke","title":"低空近吸","desc":"油烟不过脸"}]',
 '[{"group":"主要参数","items":[{"label":"风量","value":"21","unit":"m³/min"}]}]',
 '[]', '侧吸,近吸', '1', 2, '2026-11-02 10:00:00', 86, '0', '0',
 'J6058 侧吸式烟机 - site 官网', '低空近吸设计，油烟不过脸', 'admin', NOW()),
(3, 3, 'JZT-B863B', '嵌入式燃气灶 B863B', '5.0kW 猛火 · 一级能效',
 '/images/demo/p3-cover.jpg', '["/images/demo/p3-1.jpg"]',
 '[{"icon":"fire","title":"5.0kW 猛火","desc":"大火爆炒"}]',
 '[{"group":"主要参数","items":[{"label":"额定热负荷","value":"5.0","unit":"kW"},{"label":"能效等级","value":"一级","unit":""}]}]',
 '[]', '嵌入,灶具', '1', 3, '2026-11-03 10:00:00', 64, '0', '0',
 'B863B 嵌入式燃气灶 - site 官网', '5.0kW 猛火，一级能效', 'admin', NOW()),
(4, 4, 'JSQ30-16WD', '燃气热水器 16WD', '16L 大水量 · 恒温',
 '/images/demo/p4-cover.jpg', '["/images/demo/p4-1.jpg"]',
 '[{"icon":"water","title":"16L 大水量","desc":"多点用水不降温"}]',
 '[{"group":"主要参数","items":[{"label":"额定产热水能力","value":"16","unit":"L/min"}]}]',
 '[]', '热水器,恒温', '0', 4, '2026-11-04 10:00:00', 42, '0', '0',
 '16WD 燃气热水器 - site 官网', '16L 大水量，智能恒温', 'admin', NOW()),
(5, 3, 'JZT-TEST01', '停用测试灶具', '仅用于验证前台过滤',
 '', '[]', '[]', '[]', '[]', '测试', '0', 99, '2026-11-05 10:00:00', 0, '1', '0',
 '', '', 'admin', NOW());

-- 产品看点/详情长图（仅当为空时写入，避免覆盖运营上传）
UPDATE `cms_product`
SET `detail_images` = JSON_ARRAY(
    JSON_OBJECT('url', '/images/detail/p1-1.jpg', 'caption', '24m³/min 大风量，爆炒不跑烟'),
    JSON_OBJECT('url', '/images/detail/p1-2.jpg', 'caption', '蒸汽洗自清洁，内腔免拆洗'),
    JSON_OBJECT('url', '/images/detail/p1-3.jpg', 'caption', '低噪运行，厨房更安静')
)
WHERE `product_id` = 1 AND (`detail_images` IS NULL OR JSON_LENGTH(`detail_images`) = 0);

UPDATE `cms_product`
SET `detail_images` = JSON_ARRAY(
    JSON_OBJECT('url', '/images/detail/p2-1.jpg', 'caption', '低空近吸，油烟不过脸')
)
WHERE `product_id` = 2 AND (`detail_images` IS NULL OR JSON_LENGTH(`detail_images`) = 0);

