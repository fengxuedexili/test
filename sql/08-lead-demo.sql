-- =====================================================================
-- 08-lead-demo.sql  开发环境线索演示数据（生产禁止执行）
-- 前置：01、02
-- 执行：mysql -uroot -p site < 08-lead-demo.sql
-- 说明：固定主键 + INSERT IGNORE；1 条待处理、1 条已联系，便于后台列表与导出演示
-- =====================================================================
SET NAMES utf8mb4;

INSERT IGNORE INTO `cms_lead`
(`lead_id`, `type`, `name`, `phone`, `province`, `city`, `district`, `product_id`,
 `content`, `source`, `utm`, `ip`, `status`, `handler`, `handle_time`,
 `create_by`, `create_time`, `remark`)
VALUES
(1, '1', '张先生', '13800138000', '广东省', '中山市', '小榄镇', 1,
 '想预约到店体验 J6019HS', '/booking', 'utm_source=baidu&utm_campaign=brand', '127.0.0.1',
 '0', NULL, NULL, 'seed', NOW(), '开发演示数据'),
(2, '2', '李女士', '13900139000', '广东省', '佛山市', '顺德区', NULL,
 '咨询加盟政策与门店支持', '/join', '', '127.0.0.1',
 '1', 'admin', NOW(), 'seed', NOW(), '已电话联系');
