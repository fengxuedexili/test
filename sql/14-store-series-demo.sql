-- =====================================================================
-- 14-store-series-demo.sql  系列与门店演示数据（开发环境，禁止生产执行）
-- 前置：02-cms-schema.sql、06-demo-data.sql
-- 执行：mysql -uroot -p site < 14-store-series-demo.sql
-- 说明：固定主键 + INSERT IGNORE，可重复执行
-- =====================================================================
SET NAMES utf8mb4;

-- 产品系列（col 2 烟机 / 3 灶具 / 4 热水器）
INSERT IGNORE INTO `cms_series` (`series_id`, `category_id`, `name`, `subtitle`, `cover`, `sort`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
(1, 2, '魔尔套系 Pro', '一体化厨房美学 · 大吸力', '', 1, '0', '0', 'admin', NOW()),
(2, 2, '星厨静音套系', '低噪运行 · 不打扰家人', '', 2, '0', '0', 'admin', NOW()),
(3, 3, '猛火燃烧系列', '5.0kW 大火力 · 一级能效', '', 1, '0', '0', 'admin', NOW()),
(4, 4, '恒温舒适系列', '智能恒温 · 多点用水不降温', '', 1, '0', '0', 'admin', NOW());

-- 演示产品挂到系列（仅在未设置时写入）
UPDATE `cms_product` SET `series_id` = 1 WHERE `product_id` = 1 AND `series_id` IS NULL;
UPDATE `cms_product` SET `series_id` = 2 WHERE `product_id` = 2 AND `series_id` IS NULL;
UPDATE `cms_product` SET `series_id` = 3 WHERE `product_id` = 3 AND `series_id` IS NULL;
UPDATE `cms_product` SET `series_id` = 4 WHERE `product_id` = 4 AND `series_id` IS NULL;

-- 门店（1 旗舰店 / 2 专卖店 / 3 服务网点）
INSERT IGNORE INTO `cms_store`
(`store_id`, `name`, `province`, `city`, `district`, `address`, `lng`, `lat`, `phone`, `type`, `sort`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
VALUES
(1, '中山旗舰店', '广东省', '中山市', '小榄镇', '菊城大道 88 号', 113.253000, 22.671000, '0760-8888888', '1', 1, '0', '0', 'admin', NOW(), '旗舰体验店，含全系产品'),
(2, '佛山专卖店', '广东省', '佛山市', '顺德区', '北滘镇工业大道 12 号', 113.117000, 22.929000, '0757-8666666', '2', 2, '0', '0', 'admin', NOW(), NULL),
(3, '深圳服务网点', '广东省', '深圳市', '南山区', '科技园南区 6 栋 101', 113.945000, 22.540000, '0755-8333333', '3', 3, '0', '0', 'admin', NOW(), '安装与售后'),
(4, '杭州专卖店', '浙江省', '杭州市', '滨江区', '江南大道 500 号', 120.211000, 30.208000, '0571-8777777', '2', 4, '0', '0', 'admin', NOW(), NULL),
(5, '成都旗舰店', '四川省', '成都市', '高新区', '天府大道中段 1 号', 104.066000, 30.572000, '028-8555555', '1', 5, '0', '0', 'admin', NOW(), NULL),
(6, '南京服务网点', '江苏省', '南京市', '建邺区', '江东中路 200 号', 118.725000, 32.011000, '025-8444444', '3', 6, '1', '0', 'admin', NOW(), '暂停营业演示');
