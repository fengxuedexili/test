-- =====================================================================
-- 11-article-page-demo.sql  开发环境：资讯栏目 + 文章 + 单页演示数据
-- 前置：01、02
-- 执行：mysql -uroot -p site < 11-article-page-demo.sql
-- 说明：固定主键 + INSERT IGNORE；含隐私政策（P0）与关于我们单页
-- =====================================================================
SET NAMES utf8mb4;

-- 资讯栏目
INSERT IGNORE INTO `cms_category`
(`category_id`, `parent_id`, `name`, `slug`, `type`, `cover`, `summary`, `sort`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES (10, 0, '新闻动态', 'news', '2', '', '品牌与产品资讯', 10, '0', '0', 'admin', NOW());

-- 文章：1 条置顶 + 1 条普通
INSERT IGNORE INTO `cms_article`
(`article_id`, `category_id`, `title`, `cover`, `summary`, `content`, `author`, `source`,
 `view_count`, `is_top`, `publish_time`, `status`, `del_flag`, `seo_title`, `seo_description`, `create_by`, `create_time`)
VALUES
(1, 10, 'site 2026 新品发布会圆满举行', '/images/news/n1.jpg',
 '以“让厨房更懂生活”为主题，发布 J6019HS 等 6 款新品。',
 '<h2>新品发布</h2><p>2026 年 11 月，site 在中山举办新品发布会，围绕“大风量、低噪音、易清洁”发布 6 款厨房电器。</p><p>本次发布的 J6019HS 欧式顶吸烟机，风量达到 24m³/min，并配备蒸汽自清洁。</p><img src="/images/news/n1-1.jpg" alt="发布会现场" />',
 'site 市场部', 'site 官网', 128, '1', '2026-11-15 10:00:00', '0', '0',
 'site 2026 新品发布会 - site 官网', 'site 2026 新品发布会现场报道', 'admin', NOW()),
(2, 10, '厨房油烟治理指南：大风量还是低空近吸？', '/images/news/n2.jpg',
 '从风量、风压、安装高度三个维度拆解油烟机选购。',
 '<h2>先看厨房结构</h2><p>开放式厨房优先考虑大风量与高静压；封闭小厨房则更适合低空近吸。</p><h2>再看使用习惯</h2><p>重油爆炒的家庭建议 21m³/min 以上风量，并关注噪声指标。</p>',
 'site 产品团队', 'site 官网', 86, '0', '2026-11-10 09:30:00', '0', '0',
 '厨房油烟治理指南 - site 官网', '油烟机选购指南：风量、风压与安装高度', 'admin', NOW());

-- 单页：隐私政策（P0）、关于我们
INSERT IGNORE INTO `cms_page`
(`page_id`, `slug`, `title`, `blocks`, `status`, `seo_title`, `seo_description`, `create_by`, `create_time`)
VALUES
(1, 'privacy', '隐私政策',
 JSON_ARRAY(
   JSON_OBJECT('type', 'richText', 'props', JSON_OBJECT('html',
     '<h2>隐私政策</h2><p>我们重视您的个人信息保护。本页面说明我们在您使用预约、咨询等功能时收集的信息范围与用途。</p><h3>我们收集的信息</h3><p>姓名、手机号、所在地区、需求描述，以及您主动填写的其他内容。</p><h3>信息用途</h3><p>仅用于与您沟通产品咨询、预约到店与售后服务，不会用于与本服务无关的用途。</p><h3>信息保存与删除</h3><p>我们按最小必要原则保存信息；您可以随时联系我们删除。</p>'))
 ),
 '0', '隐私政策 - site 官网', 'site 官网隐私政策', 'admin', NOW()),
(2, 'about', '关于我们',
 JSON_ARRAY(
   JSON_OBJECT('type', 'richText', 'props', JSON_OBJECT('html',
     '<h2>关于 site</h2><p>site 专注厨房电器研发与制造，产品覆盖吸油烟机、燃气灶具与热水器。</p>')),
   JSON_OBJECT('type', 'imageText', 'props', JSON_OBJECT(
     'image', '/images/about/a1.jpg',
     'title', '品牌故事',
     'text', '从第一台烟机到今天，我们坚持“大风量、低噪音、易清洁”的产品理念。',
     'reverse', false))
 ),
 '0', '关于我们 - site 官网', 'site 品牌介绍', 'admin', NOW());
