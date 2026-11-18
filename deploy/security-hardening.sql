-- =====================================================================
-- 生产上线前安全加固（手动执行，仅生产库；执行前先备份）
-- 执行：mysql -uroot -p site < deploy/security-hardening.sql
-- =====================================================================
SET NAMES utf8mb4;

-- 1) 禁用演示账号（content / sales）；admin 请在后台「个人中心」修改密码
UPDATE `sys_user`
SET `status` = '1', `update_by` = 'admin', `update_time` = NOW()
WHERE `user_name` IN ('content', 'sales');

-- 2) 确认图形验证码开启
UPDATE `sys_config`
SET `config_value` = 'true', `update_by` = 'admin', `update_time` = NOW()
WHERE `config_key` = 'sys.account.captchaEnabled';

-- 3) 可选：清理演示线索（执行前务必确认/备份）
-- DELETE FROM `cms_lead` WHERE `lead_id` IN (1, 2);

-- 4) 核对结果
SELECT `user_id`, `user_name`, `status`, `del_flag`, `pwd_update_date`
FROM `sys_user` WHERE `user_name` IN ('admin', 'content', 'sales');

SELECT `config_key`, `config_value`
FROM `sys_config`
WHERE `config_key` IN ('sys.account.captchaEnabled', 'site.baseUrl');
