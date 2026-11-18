# sql · 数据库脚本

按顺序执行到 `site` 库（MySQL 8.0+，utf8mb4）。

| 顺序 | 脚本 | 内容 | 幂等 |
|---|---|---|---|
| 1 | `01-sys-base-schema.sql` | RuoYi 3.9.1 `sys_*` 基线（18 表）+ 基础数据 + 清理 demo 遗留 | 是（含 `DROP TABLE IF EXISTS`） |
| 2 | `02-cms-schema.sql` | 官网业务 8 表（`cms_category/series/product/banner/article/page/store/lead`），**纯 DDL** | 是（`CREATE TABLE IF NOT EXISTS`） |
| 3 | `03-cms-menu.sql` | 「内容管理」菜单与按钮权限（`menu_id` 3000~3299） | 否（重复执行会主键冲突，需先删 3000 段） |
| 4 | `04-site-config.sql` | 站点参数 `site.*`（9 个键） | 是（`NOT EXISTS` 判断） |
| 5 | `05-cms-dict.sql` | 业务字典 5 类 / 16 条（栏目类型、线索类型/状态、门店类型、Banner 位置） | 是（`NOT EXISTS` 判断） |
| 6 | `06-demo-data.sql` | **仅开发环境**演示数据：4 栏目 + 5 产品（含 1 条停用） | 是（固定主键 + `INSERT IGNORE`） |
| 7 | `07-banner-demo.sql` | **仅开发环境**演示 Banner：2 条首页有效 + 1 条已过期 + 1 条产品中心 | 是（固定主键 + `INSERT IGNORE`） |
| 8 | `08-lead-demo.sql` | **仅开发环境**演示线索：1 条待处理 + 1 条已联系 | 是（固定主键 + `INSERT IGNORE`） |
| 9 | `09-fix-upload-paths.sql` | 修正历史图片路径中的 Windows 反斜杠（`\` → `/`） | 是 |
| 10 | `10-product-detail-images.sql` | `cms_product` 增加 `detail_images`（产品看点/详情长图 JSON） | 是（列存在则跳过） |
| 11 | `11-article-page-demo.sql` | **仅开发环境**：资讯分类 + 2 篇文章 + 隐私政策/关于我们单页 | 是（固定主键 + `INSERT IGNORE`） |
| 12 | `12-site-seo.sql` | 站点 SEO 参数 `site.baseUrl`（sitemap/robots 使用） | 是（`NOT EXISTS` 判断） |
| 13 | `13-roles-demo.sql` | **仅开发环境**：内容运营/线索专员角色、菜单分配与演示账号 | 是（`INSERT IGNORE`） |
| 14 | `14-store-series-demo.sql` | **仅开发环境**：4 个产品系列 + 6 家门店（含 1 家停用）+ 产品挂系列 | 是（固定主键 + `INSERT IGNORE`） |
| 15 | `15-dashboard-menu.sql` | 「运营看板」菜单（3250）与 `content_ops`/`lead_sales` 授权 | 是（`NOT EXISTS` + `INSERT IGNORE`） |

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS site DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -uroot -p site < 01-sys-base-schema.sql
mysql -uroot -p site < 02-cms-schema.sql
mysql -uroot -p site < 03-cms-menu.sql
mysql -uroot -p site < 04-site-config.sql
mysql -uroot -p site < 05-cms-dict.sql
mysql -uroot -p site < 06-demo-data.sql      # 生产环境跳过
mysql -uroot -p site < 07-banner-demo.sql    # 生产环境跳过
mysql -uroot -p site < 08-lead-demo.sql      # 生产环境跳过
mysql -uroot -p site < 09-fix-upload-paths.sql
mysql -uroot -p site < 10-product-detail-images.sql
mysql -uroot -p site < 11-article-page-demo.sql   # 生产环境跳过
mysql -uroot -p site < 12-site-seo.sql
mysql -uroot -p site < 13-roles-demo.sql     # 生产环境跳过
mysql -uroot -p site < 14-store-series-demo.sql   # 生产环境跳过
mysql -uroot -p site < 15-dashboard-menu.sql
```

说明：
- `01` 由 RuoYi 3.9.1 运行库导出（仅 `sys_*`），已剔除先期 demo 的 `sys_product*`、`vatti_customer`、测试账号与自定义菜单；`sys_oper_log`/`sys_logininfor`/`sys_job_log` 仅保留结构。
- `02` 只建表，不再内嵌演示数据（演示数据统一在 `06`）。
- 默认账号 `admin / admin123`，首次登录后请修改。
- 生产库执行前先备份；`03` 重复执行前先清理 3000 段菜单与 `sys_role_menu` 关联；`06` 禁止在生产执行。
