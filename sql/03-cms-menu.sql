-- =====================================================================
-- 03-cms-menu.sql  官网内容管理 菜单与按钮权限
-- 前置：已执行 01-sys-base-schema.sql（sys_menu）+ 02-cms-schema.sql
-- 说明：
--   1) 菜单 ID 使用 3000~3299 段；站点库现有最大 menu_id < 3000，无冲突
--   2) admin 超级管理员自动拥有全部权限，其他角色需在“角色管理”中勾选
--   3) 执行后必须退出重新登录，前端路由与按钮权限才会刷新
--   4) 菜单 component 需与前端 src/views/cms/xxx/index.vue 对应，否则 404
-- =====================================================================
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 一级目录：内容管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3000,'内容管理',0,5,'cms',NULL,1,0,'M','0','0','','documentation','admin',NOW(),'官网内容管理目录');

-- ---------------------------------------------------------------------
-- 栏目管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3010,'栏目管理',3000,1,'category','cms/category/index',1,0,'C','0','0','cms:category:list','list','admin',NOW(),''),
(3011,'栏目查询',3010,1,'',NULL,1,0,'F','0','0','cms:category:query','#','admin',NOW(),''),
(3012,'栏目新增',3010,2,'',NULL,1,0,'F','0','0','cms:category:add','#','admin',NOW(),''),
(3013,'栏目修改',3010,3,'',NULL,1,0,'F','0','0','cms:category:edit','#','admin',NOW(),''),
(3014,'栏目删除',3010,4,'',NULL,1,0,'F','0','0','cms:category:remove','#','admin',NOW(),''),
(3015,'栏目导出',3010,5,'',NULL,1,0,'F','0','0','cms:category:export','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 系列管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3040,'系列管理',3000,2,'series','cms/series/index',1,0,'C','0','0','cms:series:list','tree','admin',NOW(),''),
(3041,'系列查询',3040,1,'',NULL,1,0,'F','0','0','cms:series:query','#','admin',NOW(),''),
(3042,'系列新增',3040,2,'',NULL,1,0,'F','0','0','cms:series:add','#','admin',NOW(),''),
(3043,'系列修改',3040,3,'',NULL,1,0,'F','0','0','cms:series:edit','#','admin',NOW(),''),
(3044,'系列删除',3040,4,'',NULL,1,0,'F','0','0','cms:series:remove','#','admin',NOW(),''),
(3045,'系列导出',3040,5,'',NULL,1,0,'F','0','0','cms:series:export','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 产品管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3070,'产品管理',3000,3,'product','cms/product/index',1,0,'C','0','0','cms:product:list','goods','admin',NOW(),''),
(3071,'产品查询',3070,1,'',NULL,1,0,'F','0','0','cms:product:query','#','admin',NOW(),''),
(3072,'产品新增',3070,2,'',NULL,1,0,'F','0','0','cms:product:add','#','admin',NOW(),''),
(3073,'产品修改',3070,3,'',NULL,1,0,'F','0','0','cms:product:edit','#','admin',NOW(),''),
(3074,'产品删除',3070,4,'',NULL,1,0,'F','0','0','cms:product:remove','#','admin',NOW(),''),
(3075,'产品导出',3070,5,'',NULL,1,0,'F','0','0','cms:product:export','#','admin',NOW(),''),
(3076,'产品导入',3070,6,'',NULL,1,0,'F','0','0','cms:product:import','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- Banner 管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3100,'Banner管理',3000,4,'banner','cms/banner/index',1,0,'C','0','0','cms:banner:list','picture','admin',NOW(),''),
(3101,'Banner查询',3100,1,'',NULL,1,0,'F','0','0','cms:banner:query','#','admin',NOW(),''),
(3102,'Banner新增',3100,2,'',NULL,1,0,'F','0','0','cms:banner:add','#','admin',NOW(),''),
(3103,'Banner修改',3100,3,'',NULL,1,0,'F','0','0','cms:banner:edit','#','admin',NOW(),''),
(3104,'Banner删除',3100,4,'',NULL,1,0,'F','0','0','cms:banner:remove','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 文章管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3130,'文章管理',3000,5,'article','cms/article/index',1,0,'C','0','0','cms:article:list','edit','admin',NOW(),''),
(3131,'文章查询',3130,1,'',NULL,1,0,'F','0','0','cms:article:query','#','admin',NOW(),''),
(3132,'文章新增',3130,2,'',NULL,1,0,'F','0','0','cms:article:add','#','admin',NOW(),''),
(3133,'文章修改',3130,3,'',NULL,1,0,'F','0','0','cms:article:edit','#','admin',NOW(),''),
(3134,'文章删除',3130,4,'',NULL,1,0,'F','0','0','cms:article:remove','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 单页管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3160,'单页管理',3000,6,'page','cms/page/index',1,0,'C','0','0','cms:page:list','files','admin',NOW(),''),
(3161,'单页查询',3160,1,'',NULL,1,0,'F','0','0','cms:page:query','#','admin',NOW(),''),
(3162,'单页新增',3160,2,'',NULL,1,0,'F','0','0','cms:page:add','#','admin',NOW(),''),
(3163,'单页修改',3160,3,'',NULL,1,0,'F','0','0','cms:page:edit','#','admin',NOW(),''),
(3164,'单页删除',3160,4,'',NULL,1,0,'F','0','0','cms:page:remove','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 门店管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3190,'门店管理',3000,7,'store','cms/store/index',1,0,'C','0','0','cms:store:list','location','admin',NOW(),''),
(3191,'门店查询',3190,1,'',NULL,1,0,'F','0','0','cms:store:query','#','admin',NOW(),''),
(3192,'门店新增',3190,2,'',NULL,1,0,'F','0','0','cms:store:add','#','admin',NOW(),''),
(3193,'门店修改',3190,3,'',NULL,1,0,'F','0','0','cms:store:edit','#','admin',NOW(),''),
(3194,'门店删除',3190,4,'',NULL,1,0,'F','0','0','cms:store:remove','#','admin',NOW(),''),
(3195,'门店导出',3190,5,'',NULL,1,0,'F','0','0','cms:store:export','#','admin',NOW(),'');

-- ---------------------------------------------------------------------
-- 线索管理
-- ---------------------------------------------------------------------
INSERT INTO `sys_menu`
(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
(3220,'线索管理',3000,8,'lead','cms/lead/index',1,0,'C','0','0','cms:lead:list','peoples','admin',NOW(),''),
(3221,'线索查询',3220,1,'',NULL,1,0,'F','0','0','cms:lead:query','#','admin',NOW(),''),
(3222,'线索修改',3220,2,'',NULL,1,0,'F','0','0','cms:lead:edit','#','admin',NOW(),''),
(3223,'线索删除',3220,3,'',NULL,1,0,'F','0','0','cms:lead:remove','#','admin',NOW(),''),
(3224,'线索导出',3220,4,'',NULL,1,0,'F','0','0','cms:lead:export','#','admin',NOW(),'');

-- =====================================================================
-- 校验：应返回 8 个内容子菜单
-- =====================================================================
-- SELECT menu_id, menu_name, menu_type, perms FROM sys_menu WHERE parent_id = 3000 ORDER BY order_num;

-- =====================================================================
-- 附：给「内容运营」角色授权（示例）
-- 前提：先有 role_id（示例用 100），把下列行插入 sys_role_menu
-- =====================================================================
-- INSERT INTO sys_role_menu (role_id, menu_id)
-- SELECT 100, menu_id FROM sys_menu WHERE menu_id = 3000 OR parent_id = 3000 OR parent_id IN (3010,3040,3070,3100,3130,3160,3190);


