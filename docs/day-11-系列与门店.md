# Day 11 · 产品系列与门店查询

> 目标：补齐 `cms_series` / `cms_store` 两个已有表对应的业务模块（后台 CRUD + 前台展示），并让产品可以挂到系列下。菜单与权限在 Day 1 的 `03-cms-menu.sql` 已入库，本次只补代码与演示数据。

## 1. 范围

| 模块 | 后台 | 前台 | 说明 |
|---|---|---|---|
| 产品系列 | `/cms/series`：列表、新增、修改、删除（软删） | 产品详情暂不展示（预留 `seriesId`） | 系列按产品栏目归属；产品表单可选系列 |
| 门店 | `/cms/store`：分页列表、增删改、按省/市/类型/状态筛选 | `/store`：省份 + 类型筛选、电话、高德导航 | 无地图 Key，先用「导航前往」跳高德 URI |

不做：门店地图 Canvas、系列聚合页（依赖 IP 定位与更多内容，排后续）。

## 2. 数据模型（复用 02 已建表）

`cms_series`

| 字段 | 类型 | 说明 |
|---|---|---|
| `series_id` | bigint PK | 系列 ID |
| `category_id` | bigint | 所属产品栏目（`cms_category.type='1'`） |
| `name` / `subtitle` / `cover` | varchar | 名称、副标题、封面 |
| `sort` / `status` / `del_flag` | int / char | 排序、状态（0 正常 1 停用）、软删 |

`cms_store`

| 字段 | 类型 | 说明 |
|---|---|---|
| `store_id` | bigint PK | 门店 ID |
| `name` | varchar(128) | 门店名称 |
| `province` / `city` / `district` / `address` | varchar | 行政区与详细地址 |
| `lng` / `lat` | decimal(10,6) | 高德坐标系，用于导航链接 |
| `phone` | varchar(32) | 门店电话 |
| `type` | char(1) | 1 旗舰店 / 2 专卖店 / 3 服务网点 |
| `sort` / `status` / `del_flag` | int / char | 排序、状态、软删 |

`cms_product.series_id` 早已存在（`idx_product_series`），本次仅补后台表单与演示数据。

## 3. 后端实现（`ruoyi-cms`）

新增文件：

```
domain/CmsSeries.java、CmsStore.java
domain/vo/SiteStoreVo.java
mapper/CmsSeriesMapper.java、CmsStoreMapper.java
resources/mapper/cms/CmsSeriesMapper.xml、CmsStoreMapper.xml
service/ICmsSeriesService.java、ICmsStoreService.java
service/impl/CmsSeriesServiceImpl.java、CmsStoreServiceImpl.java
controller/admin/CmsSeriesController.java、CmsStoreController.java
controller/site/SiteStoreController.java
```

接口与权限：

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/cms/series/list` | `cms:series:list` | 不分页（系列数量少），支持名称/栏目/状态过滤 |
| GET | `/cms/series/{id}` | `cms:series:query` | 详情 |
| POST | `/cms/series` | `cms:series:add` | 校验栏目与名称，默认排序 0 / 状态 0 |
| PUT | `/cms/series` | `cms:series:edit` | 同上 |
| DELETE | `/cms/series/{ids}` | `cms:series:remove` | 软删 `del_flag='2'` |
| GET | `/cms/store/list` | `cms:store:list` | 分页（`startPage`），按名称/省/市/类型/状态过滤 |
| GET | `/cms/store/{id}` | `cms:store:query` | 详情 |
| POST/PUT/DELETE | `/cms/store` | `cms:store:add/edit/remove` | 校验名称，默认类型 2 / 排序 0 / 状态 0 |
| GET | `/api/site/store` | 匿名 | 只返回 `status='0' and del_flag='0'`；可选 `province`/`city`/`type` |
| GET | `/api/site/store/provinces` | 匿名 | 去重省份，供前台筛选 |

实现要点：

- 门店前台返回 `SiteStoreVo`（id/name/province/city/district/address/phone/type/lng/lat），不暴露后台审计字段。
- 前台列表排序 `sort asc, store_id asc`；`selectProvinces` 只取启用门店，避免空筛选项。
- 删除均为软删，与栏目/产品/Banner 一致。
- 系列列表不分页，直接 `AjaxResult.success(list)`，后台表格一次加载。
- 门店后台用 `TableDataInfo` 分页，与线索/文章一致。

## 4. 后台前端（`site-admin`）

| 文件 | 内容 |
|---|---|
| `src/api/cms/series.js` | `listSeries/getSeries/addSeries/updateSeries/delSeries` |
| `src/api/cms/store.js` | `listStore/getStore/addStore/updateStore/delStore` |
| `src/views/cms/series/index.vue` | 列表 + 弹窗；所属栏目下拉取 `listCategoryTree('1')` 并拍平；封面 `image-upload` |
| `src/views/cms/store/index.vue` | 分页列表 + 弹窗；省/市/区文本、经度/纬度、类型下拉（静态 1/2/3）、状态字典 |
| `src/views/cms/product/index.vue` | 表单新增「所属系列」下拉：`listSeries({})` 全量加载，按 `form.categoryId` 前端过滤；`seriesId` 随保存提交 |

菜单路径与 `03-cms-menu.sql` 对齐：`cms/series/index`（menu 3040 段）、`cms/store/index`（menu 3190 段）；`content_ops` 角色已包含这些按钮权限。

## 5. 前台（`site-web`）

| 文件 | 内容 |
|---|---|
| `src/api/site.js` | 新增 `getStores(params)`、`getStoreProvinces()` |
| `src/views/StoreView.vue` | 省份 chips + 类型 chips（全部/旗舰店/专卖店/服务网点）；卡片网格；`tel:` 电话；有经纬度时显示「导航前往」（高德 URI） |
| `src/router/index.js` | `/store` → `StoreView`，标题「门店查询」 |
| `src/components/AppHeader.vue` | 导航新增「门店查询」（资讯与预约之间） |
| `src/styles/main.css` | `.store-page/.store-filter/.chips/.chip/.store-grid/.store-card` 等；≤900px 单列 |

交互：`watch([province, type])` 触发 `getStores`，无「查询」按钮；空态区分「加载中」与「该地区暂无门店」。

## 6. 演示数据（`sql/14-store-series-demo.sql`）

- 4 个系列：魔尔套系 Pro、星厨静音套系（烟机）、猛火燃烧系列（灶具）、恒温舒适系列（热水器）。
- 演示产品 1–4 挂到对应系列（仅当 `series_id IS NULL`）。
- 6 家门店：中山/成都旗舰店、佛山/杭州专卖店、深圳/南京服务网点；南京店 `status='1'` 用于验证前台过滤。
- 固定主键 + `INSERT IGNORE`，可重复执行。

## 7. 验证记录

### 7.1 构建

```powershell
cd D:\VUE-WorkSpace\2027\site
mvn -B -q -DskipTests compile          # 后端编译通过
mvn -B -q -DskipTests package          # site-server.jar 91.1 MB
cd site-web; npm run build             # 生成 dist/assets/StoreView-*.js
cd ../site-admin; npm run build:prod    # Build complete（仅有体积警告）
```

### 7.2 数据库与接口实跑（2026-11-18）

| # | 检查项 | 结果 |
|---|---|---|
| 1 | 导入 `14-store-series-demo.sql` | 成功；`cms_series=4`、`cms_store=6` |
| 2 | `GET /api/site/store` | 200，5 家；南京店（`status='1'`）被过滤 |
| 3 | `GET /api/site/store/provinces` | 四川省 / 广东省 / 浙江省（江苏因唯一门店停用而不出现） |
| 4 | `GET /api/site/store?province=广东省&type=1` | 1 家：中山旗舰店 |
| 5 | `GET /api/site/sitemap.xml` | 含 `/store` |
| 6 | 后台登录（admin/admin123，验证码临时关闭） | code=200，token 203 字符 |
| 7 | `GET /cms/series/list` | 4 条（1/2/3/4） |
| 8 | `GET /cms/store/list` | total=6，rows=6 |
| 9 | 系列新增 → 名称过滤 → 修改 → 删除 | 全部 200；修改后名称/排序生效；删除后列表不可见 |
| 10 | 门店新增 → 前台按省可见 → 修改类型 → 删除 | 全部 200；新增后 `/api/site/store?province=测试省`=1，删除后=0 |
| 11 | 测试数据清理 | 硬删 series 5/6、store 7；恢复 `cms_series=4`、`cms_store=6` |
| 12 | 验证码开关恢复 | `sys.account.captchaEnabled=true`，重启后未带验证码登录返回 `验证码已失效` |
| 13 | 页面 | `http://localhost/`、`/store`、`http://localhost:5174/` 均 200 |
| 14 | 后端日志 | `Started RuoYiApplication in 5.984 seconds`；无新增 ERROR（仅验证码失效的预期异常） |

实跑命令（含中文参数，PowerShell 需按 UTF-8 解码响应）：

```powershell
# 前台
$all = Invoke-RestMethod 'http://localhost:8080/api/site/store'
$pv  = Invoke-RestMethod 'http://localhost:8080/api/site/store/provinces'
$gd  = Invoke-RestMethod ('http://localhost:8080/api/site/store?province=' + [uri]::EscapeDataString('广东省') + '&type=1')

# 后台（验证码开启时需带 code/uuid；本次验证期间临时关闭，完成后已恢复）
$r = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/login' -ContentType 'application/json' -Body (@{username='admin';password='admin123'} | ConvertTo-Json)
$h = @{ Authorization = 'Bearer ' + $r.token }
Invoke-RestMethod -Uri 'http://localhost:8080/cms/series/list' -Headers $h
Invoke-RestMethod -Uri 'http://localhost:8080/cms/store/list?pageNum=1&pageSize=10' -Headers $h
```

### 7.3 踩坑记录

- 旧后端占用 `site-server.jar` 时 `mvn package` 报 `Unable to rename ... -> .original`：先停 8080 端口进程再打包。
- PowerShell 5.1 直接 `-Body` 传中文 JSON 会落库成 `??????`：测试脚本需 `[Text.Encoding]::UTF8.GetBytes($json)` + `ContentType 'application/json; charset=utf-8'`。
- PowerShell 5.1 的 `Invoke-RestMethod` 会把 UTF-8 响应按 Latin-1 解码（中文显示为 `ä¸­å±±`）：用 `Invoke-WebRequest` + `RawContentStream` 按 UTF-8 解码即可，数据本身正确。
- 验证后台接口需绕过图形验证码：临时 `update sys_config set config_value='false' where config_key='sys.account.captchaEnabled'` → 重启生效 → 验证后改回 `true` 再重启（已恢复）。


## 8. 遗留与下一步

- 门店地图（需高德/腾讯地图 Key 与坐标系确认）暂缓；当前 `lng/lat` 已入库可直接接。
- 产品详情未展示「所属系列」；若要展示，需在 `SiteProductVo` 增 `seriesName` 并在 XML 左连 `cms_series`。
- 系列聚合页（`/series/:id`）排后续，需与栏目页共用产品卡片。
- Day 12 计划：后台看板（待处理线索/产品数/最新文章）+ 单页 blocks 可视化编辑器。

