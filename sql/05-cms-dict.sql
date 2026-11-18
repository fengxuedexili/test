-- =====================================================================
-- 05-cms-dict.sql  官网业务字典（sys_dict_type / sys_dict_data）
-- 前置：01-sys-base-schema.sql
-- 执行：mysql -uroot -p site < 05-cms-dict.sql
-- 说明：幂等（按 dict_type / dict_value 判重）
-- =====================================================================
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1. 字典类型
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.name, t.type, '0', 'admin', NOW(), t.remark
FROM (
    SELECT '栏目类型' AS name, 'cms_category_type' AS type, '官网栏目类型' AS remark
    UNION ALL SELECT '线索类型', 'cms_lead_type', '官网表单线索类型'
    UNION ALL SELECT '线索状态', 'cms_lead_status', '线索跟进状态'
    UNION ALL SELECT '门店类型', 'cms_store_type', '线下门店类型'
    UNION ALL SELECT 'Banner 位置', 'cms_banner_position', '轮播位所属页面'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` d WHERE d.`dict_type` = t.type);

-- ---------------------------------------------------------------------
-- 2. 栏目类型 cms_category_type
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.sort, t.label, t.value, 'cms_category_type', '', t.cls, t.def, '0', 'admin', NOW(), ''
FROM (
    SELECT 1 AS sort, '产品栏目' AS label, '1' AS value, 'primary' AS cls, 'Y' AS def
    UNION ALL SELECT 2, '资讯栏目', '2', 'info', 'N'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` d WHERE d.`dict_type` = 'cms_category_type' AND d.`dict_value` = t.value);

-- ---------------------------------------------------------------------
-- 3. 线索类型 cms_lead_type
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.sort, t.label, t.value, 'cms_lead_type', '', t.cls, t.def, '0', 'admin', NOW(), ''
FROM (
    SELECT 1 AS sort, '预约体验' AS label, '1' AS value, 'primary' AS cls, 'Y' AS def
    UNION ALL SELECT 2, '招商加盟', '2', 'success', 'N'
    UNION ALL SELECT 3, '招聘', '3', 'info', 'N'
    UNION ALL SELECT 4, '售后服务', '4', 'warning', 'N'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` d WHERE d.`dict_type` = 'cms_lead_type' AND d.`dict_value` = t.value);

-- ---------------------------------------------------------------------
-- 4. 线索状态 cms_lead_status
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.sort, t.label, t.value, 'cms_lead_status', '', t.cls, t.def, '0', 'admin', NOW(), ''
FROM (
    SELECT 1 AS sort, '待处理' AS label, '0' AS value, 'warning' AS cls, 'Y' AS def
    UNION ALL SELECT 2, '已联系', '1', 'primary', 'N'
    UNION ALL SELECT 3, '无效', '2', 'info', 'N'
    UNION ALL SELECT 4, '已成交', '3', 'success', 'N'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` d WHERE d.`dict_type` = 'cms_lead_status' AND d.`dict_value` = t.value);

-- ---------------------------------------------------------------------
-- 5. 门店类型 cms_store_type
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.sort, t.label, t.value, 'cms_store_type', '', t.cls, t.def, '0', 'admin', NOW(), ''
FROM (
    SELECT 1 AS sort, '旗舰店' AS label, '1' AS value, 'danger' AS cls, 'Y' AS def
    UNION ALL SELECT 2, '专卖店', '2', 'primary', 'N'
    UNION ALL SELECT 3, '服务网点', '3', 'info', 'N'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` d WHERE d.`dict_type` = 'cms_store_type' AND d.`dict_value` = t.value);

-- ---------------------------------------------------------------------
-- 6. Banner 位置 cms_banner_position
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT t.sort, t.label, t.value, 'cms_banner_position', '', t.cls, t.def, '0', 'admin', NOW(), ''
FROM (
    SELECT 1 AS sort, '首页' AS label, 'home' AS value, 'primary' AS cls, 'Y' AS def
    UNION ALL SELECT 2, '产品中心', 'product', 'info', 'N'
    UNION ALL SELECT 3, '品牌页', 'brand', 'success', 'N'
) t
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` d WHERE d.`dict_type` = 'cms_banner_position' AND d.`dict_value` = t.value);
