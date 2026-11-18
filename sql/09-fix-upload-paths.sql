-- =====================================================================
-- 09-fix-upload-paths.sql  修正 Windows 上传路径的反斜杠
-- 背景：Windows 下 FileUploadUtils 生成的 /profile 路径含反斜杠，
--       浏览器会把 \ 当非法字符，导致图片 404。
-- 执行：mysql -uroot -p site < 09-fix-upload-paths.sql
-- 说明：幂等；已由 CommonController 在上传时统一为正斜杠，本脚本修复历史数据
-- =====================================================================
SET NAMES utf8mb4;

UPDATE `cms_product` SET `cover` = REPLACE(`cover`, '\\', '/') WHERE `cover` LIKE '%\\\\%';
UPDATE `cms_product` SET `images` = REPLACE(`images`, '\\', '/') WHERE `images` LIKE '%\\\\%';
UPDATE `cms_category` SET `cover` = REPLACE(`cover`, '\\', '/') WHERE `cover` LIKE '%\\\\%';
UPDATE `cms_banner` SET `image_url` = REPLACE(`image_url`, '\\', '/') WHERE `image_url` LIKE '%\\\\%';
UPDATE `cms_banner` SET `video_url` = REPLACE(`video_url`, '\\', '/') WHERE `video_url` LIKE '%\\\\%';
