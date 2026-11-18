-- =====================================================================
-- 15-dashboard-menu.sql  后台「运营看板」菜单
-- 前置：01、03（内容管理目录 3000）、13（可选，content_ops / lead_sales 角色）
-- 执行：mysql -uroot -p site < 15-dashboard-menu.sql
-- 说明：
--   1) 菜单 ID 3250，挂在「内容管理」下，顺序最前
--   2) 幂等：已存在则跳过；角色授权使用 INSERT IGNORE
--   3) 执行后需退出重新登录，侧边栏才会出现
-- =====================================================================
SET NAMES utf8mb4;

INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
SELECT 3250,'运营看板',3000,0,'dashboard','cms/dashboard/index',1,0,'C','0','0','cms:dashboard:view','dashboard','admin',NOW(),'CMS 首页看板（线索/内容/门店概览）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 3250);

-- 内容运营 / 线索专员均可看看板
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, 3250 FROM `sys_role` r WHERE r.role_id IN (100, 101);
