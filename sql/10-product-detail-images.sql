-- =====================================================================
-- 10-product-detail-images.sql  cms_product 增加「产品看点/详情图」字段
-- 说明：JSON 数组 [{"url":"/profile/...","caption":"卖点说明"}]
-- 执行：mysql -uroot -p site < 10-product-detail-images.sql
-- 幂等：已存在该列时跳过
-- =====================================================================
SET NAMES utf8mb4;

SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cms_product' AND COLUMN_NAME = 'detail_images'
);

SET @ddl := IF(@col_exists = 0,
  'ALTER TABLE `cms_product` ADD COLUMN `detail_images` json DEFAULT NULL COMMENT ''产品看点/详情长图 [{"url":"","caption":""}]'' AFTER `images`',
  'SELECT ''detail_images already exists'' AS msg');

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
