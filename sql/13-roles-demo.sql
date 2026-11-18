-- =====================================================================
-- 13-roles-demo.sql  开发环境：内容运营 / 线索专员 角色与演示账号
-- 前置：01、03（菜单 3000 段）
-- 执行：mysql -uroot -p site < 13-roles-demo.sql
-- 说明：
--   1) 角色：content_ops（内容运营，role_id=100）、lead_sales（线索专员，role_id=101）
--   2) 账号：content / sales，密码与 admin 相同（默认 admin123）
--   3) 仅开发/演示环境使用，生产请按 docs/05 手工建角色
-- =====================================================================
SET NAMES utf8mb4;

INSERT IGNORE INTO `sys_role`
(`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
VALUES
(100, '内容运营', 'content_ops', 3, '1', 1, 1, '0', '0', 'admin', NOW(), '栏目/产品/Banner/文章/单页/门店'),
(101, '线索专员', 'lead_sales', 4, '1', 1, 1, '0', '0', 'admin', NOW(), '线索查看/跟进/导出');

-- 内容运营：内容管理目录 + 各内容模块（不含线索）
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 100, menu_id FROM `sys_menu`
WHERE menu_id = 3000
   OR menu_id BETWEEN 3010 AND 3015
   OR menu_id BETWEEN 3040 AND 3045
   OR menu_id BETWEEN 3070 AND 3076
   OR menu_id BETWEEN 3100 AND 3104
   OR menu_id BETWEEN 3130 AND 3134
   OR menu_id BETWEEN 3160 AND 3164
   OR menu_id BETWEEN 3190 AND 3195;

-- 线索专员：内容管理目录 + 线索模块
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 101, menu_id FROM `sys_menu`
WHERE menu_id = 3000 OR menu_id BETWEEN 3220 AND 3224;

-- 演示账号（密码复用 admin 的 bcrypt）
INSERT IGNORE INTO `sys_user`
(`user_id`, `dept_id`, `user_name`, `nick_name`, `email`, `phonenumber`, `sex`, `avatar`, `password`,
 `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 100, 103, 'content', '内容运营', '', '', '0', '', `password`,
       '0', '0', '', NULL, NULL, 'admin', NOW(), '', NULL, '演示账号 content / admin123（内容运营）'
FROM `sys_user` WHERE `user_id` = 1;

INSERT IGNORE INTO `sys_user`
(`user_id`, `dept_id`, `user_name`, `nick_name`, `email`, `phonenumber`, `sex`, `avatar`, `password`,
 `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 101, 103, 'sales', '线索专员', '', '', '0', '', `password`,
       '0', '0', '', NULL, NULL, 'admin', NOW(), '', NULL, '演示账号 sales / admin123（线索专员）'
FROM `sys_user` WHERE `user_id` = 1;

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`) VALUES (100, 100), (101, 101);
