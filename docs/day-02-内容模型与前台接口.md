# Day 2 · 内容模型与前台接口（2026-11-18）

> 方案 B 第 2 天：在 `ruoyi-cms` 中落地「栏目 + 产品」领域模型、后台 CRUD、前台只读接口，并补齐字典与演示数据。
> 验收：前台匿名可读、后台需权限、JSON 字段正确解析、软删除生效。

## 1. 交付物

| # | 交付 | 位置 |
|---|---|---|
| 1 | 栏目领域模型 | `CmsCategory` + `CmsCategoryMapper(.xml)` + `ICmsCategoryService(Impl)` |
| 2 | 产品领域模型 | `CmsProduct` + `CmsProductMapper(.xml)` + `ICmsProductService(Impl)` |
| 3 | 前台 VO | `SiteCategoryVo`、`SiteProductVo`（不含 `delFlag/createBy/remark`） |
| 4 | 后台接口 | `CmsCategoryController`（`/cms/category/**`）、`CmsProductController`（`/cms/product/**`） |
| 5 | 前台接口 | `SiteCategoryController`（`/api/site/category/tree`）、`SiteProductController`（`page` / `recommend` / `{id}`） |
| 6 | 字典 | `sql/05-cms-dict.sql`（5 类字典，16 条数据） |
| 7 | 演示数据 | `sql/06-demo-data.sql`（4 栏目 / 5 产品，含 1 条停用用于过滤验证） |
| 8 | 规范修正 | `sql/02-cms-schema.sql` 去掉内嵌演示数据，改为纯 DDL |

## 2. 接口清单（本期实现）

### 2.1 前台（`@Anonymous`，只读）

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
| GET | `/api/site/category/tree` | `type`（默认 1） | 栏目树，仅 `status='0'` |
| GET | `/api/site/product/page` | `pageNum`、`pageSize`、`categoryId`、`categorySlug`、`keyword` | 分页；仅已发布 + 正常状态 |
| GET | `/api/site/product/recommend` | `limit`（默认 6，上限 50） | 首页明星产品 |
| GET | `/api/site/product/{productId}` | — | 详情（图集/卖点/参数分组/下载/相关推荐），浏览量 +1 |
| GET | `/api/site/config` | — | Day 1 已交付（站点参数） |

返回统一 `AjaxResult`；分页结构：`data = { total, pageNum, pageSize, rows: SiteProductVo[] }`。

### 2.2 后台（`@PreAuthorize('cms:*')`）

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/cms/category/list` / `/tree` / `/{id}` | `cms:category:list` / `list` / `query` |
| POST / PUT / DELETE | `/cms/category` / `/{ids}` | `cms:category:add` / `edit` / `remove` |
| GET | `/cms/product/list` / `/{id}` | `cms:product:list` / `query` |
| POST / PUT / DELETE | `/cms/product` / `/{ids}` | `cms:product:add` / `edit` / `remove` |

产品列表返回 `TableDataInfo`（`rows/total`），并带 `categoryName`（联表）。

## 3. 关键实现决策

| # | 决策 | 原因 |
|---|---|---|
| 1 | 产品 JSON 列在实体中命名为 **`paramsJson`**（DB 列仍是 `params`） | `BaseEntity` 已有 `getParams(): Map`，同名会编译失败（返回类型不兼容）；`images/sellingPoints/downloads` 无冲突，保持原名 |
| 2 | 后台 JSON 字段用 `String` 承载，前台由 service 解析成 `List<String>` / `List<Map>` | 避免 MyBatis JSON 类型处理器与 `json` 列方言差异；解析失败降级为空数组，不抛 500 |
| 3 | 前台过滤在 SQL 完成：`status='0'` + `del_flag='0'` + `publish_time is null or <= sysdate()` | 与 `docs/09` 的"定时上下线不依赖 Quartz"一致 |
| 4 | 删除为**软删除**（`del_flag='2'`）；栏目删除前校验子栏目与产品 | 防误删；与 `docs/03` 约定一致 |
| 5 | 分页用 `startPage()` + `PageInfo` 取 total，再转 VO | PageHelper 在 mapper 查询处拦截，转 VO 不影响 total |
| 6 | VO 与实体隔离；前台不返回 `delFlag/createBy/updateBy/remark` | `docs/04` §3.1 的隔离要求 |
| 7 | 栏目/产品写入时 `paramsJson` 先做 JSON 语法校验，报"xx 不是合法的 JSON" | 后台表单填错时给可读错误，而不是 MySQL 1452/3140 |
| 8 | 详情接口浏览量 +1 用独立 `update ... view_count = view_count + 1` | 避免并发覆盖 |

## 4. 数据与字典

| 项 | 结果 |
|---|---|
| `cms_category` | 4 条：厨房电器（根）→ 吸油烟机 / 燃气灶具 / 热水器 |
| `cms_product` | 5 条：4 条正常（3 条推荐）+ 1 条停用（`JZT-TEST01`，验证前台过滤） |
| 字典 | `cms_category_type`(2)、`cms_lead_type`(4)、`cms_lead_status`(4)、`cms_store_type`(3)、`cms_banner_position`(3) |
| 脚本顺序 | `01` 基础库 → `02` cms 表 → `03` 菜单 → `04` 站点参数 → `05` 字典 → `06` 演示数据 |

> 修正：`02-cms-schema.sql` 原先内嵌了 2 条演示栏目 + 1 条 Banner + 1 条单页，导致 `06` 的固定主键被 `INSERT IGNORE` 跳过（表现为栏目树根节点错误）。已把演示数据从 DDL 中移除并清库重导。

## 5. 验证记录（实跑）

| # | 用例 | 结果 |
|---|---|---|
| 1 | `GET /api/site/category/tree` | `roots=1`，根 `厨房电器`，`children=3`（range-hood / cooktop / water-heater） |
| 2 | `GET /api/site/product/page?pageSize=2` | `total=4`、`rows=2`，`hoverImage=/images/demo/p1-2.jpg`（图集第二张） |
| 3 | `GET /api/site/product/page?categorySlug=range-hood` | `total=2`（J6019HS / J6058） |
| 4 | `GET /api/site/product/page?keyword=J6019` | `total=1` |
| 5 | `GET /api/site/product/recommend?limit=2` | 返回 id `1,2` |
| 6 | `GET /api/site/product/1` | `images=2`、`sellingPoints=1`、`paramGroups=1`、`downloads=1`、`related=1`，SEO 标题正确 |
| 7 | `GET /api/site/product/5`（停用） | `code=500`，`msg=产品不存在或已下线` |
| 8 | `GET /cms/product/list`（无 token） | `code=401` |
| 9 | `GET /cms/product/list`（admin token） | `total=5`，`categoryName=吸油烟机` |
| 10 | `POST /cms/category`（新增）→ `PUT`（改名）→ `DELETE` | 均 `code=200`；删除后列表 0 条 |
| 11 | `POST /cms/category` 重复 slug `kitchen` | `code=500`，`msg=栏目别名已存在：kitchen` |
| 12 | `POST /cms/product` 非法 JSON（`paramsJson:"not-json"`） | `code=500`，`msg=参数不是合法的 JSON：...` |
| 13 | `POST /cms/product`（合法 JSON）→ 前台详情 | 参数分组解析正确（`参数组=1`，`label=热负荷`，`unit=kW`）；`DELETE` 后列表 `total=0` |
| 14 | 浏览量 | 连续 2 次匿名详情：`128 → 130` |

> 测试完成后已清理：软删除的测试栏目/产品物理删除、浏览量复位、验证码开关恢复 `true`、应用停止。

## 6. 遗留与 Day 3 计划

| 项 | 状态 | Day 3 |
|---|---|---|
| 后台前端 `site-admin` | 未创建 | 复制 RuoYi-Vue `ruoyi-ui`（Vue2 + Element UI），新增 `views/cms/{category,product}`，菜单 3000 段可点 |
| 官网前台 `site-web` | 未创建 | Vite 6 + Vue 3.5 + Vue Router 4 + Vuex 4；首页/产品列表/详情接 `/api/site/**` |
| 其余 6 个 cms 模块 | 未实现 | `cms_banner`、`cms_article`、`cms_page`、`cms_store`、`cms_lead`、`cms_series` |
| 前台限流 | 未加 | 表单类接口加 `@RateLimiter`（读接口暂不加） |
| 产品导入/导出 | 未加 | `cms:product:import/export`（POI 已具备） |
| 图片/附件 | 占位路径 | 接 OSS 后替换 `06-demo-data.sql` 的占位 URL |
