# Day 4 · Banner 模块与可视化编辑（2026-11-18）

> 方案 B 第 4 天：Banner 全链路（后端 + 后台 + 前台轮播），并把后台产品的 JSON 字段换成可视化编辑器。
> 验收：Banner 增删改查 + 前台轮播（含时间窗过滤）；产品图集/卖点/参数/下载可可视化编辑并正确落库。

## 1. 交付物

### 1.1 Banner 后端（`ruoyi-cms`）
| 文件 | 说明 |
|---|---|
| `domain/CmsBanner.java` | 字段与 `cms_banner` 对齐（含 `startTime/endTime`，**无 del_flag**） |
| `domain/vo/SiteBannerVo.java` | 前台出参（id/媒体/图片/视频/文案/链接） |
| `mapper/CmsBannerMapper.java` + `resources/mapper/cms/CmsBannerMapper.xml` | 列表（后台）、时段过滤（前台）、增改、**物理删除** |
| `service/ICmsBannerService` + `impl/CmsBannerServiceImpl` | 校验：图片必须有图、视频必须有地址；默认 `position=home`、`mediaType=1` |
| `controller/admin/CmsBannerController` | `/cms/banner/**`，权限 `cms:banner:*`，带 `@Log` |
| `controller/site/SiteBannerController` | `GET /api/site/banner?position=home`（`@Anonymous`） |
| `sql/07-banner-demo.sql` | 4 条演示：2 条首页有效、1 条已过期（验证过滤）、1 条产品中心位置 |

**前台过滤 SQL**（与 `docs/09` 一致，不依赖 Quartz）：
```sql
where position = #{position} and status = '0'
  and (start_time is null or start_time <= sysdate())
  and (end_time   is null or end_time   >= sysdate())
order by sort asc, banner_id asc
```

### 1.2 后台（`site-admin`）
| 文件 | 说明 |
|---|---|
| `src/api/cms/banner.js` | list/get/add/update/del |
| `src/views/cms/banner/index.vue` | 列表（位置/封面/标题/类型/按钮/投放时间/排序/状态）+ 新增/修改弹窗（位置、媒体类型、图片/视频、文案、跳转、`datetimerange`、状态） |
| `src/views/cms/product/index.vue` | **可视化编辑器**：图集（`image-upload` ≤9）、卖点（标题/描述行）、参数分组（分组 + 参数行：名称/值/单位）、下载资料（名称/地址/大小） |

> JSON 转换在提交前完成：`images` → JSON 数组、`sellingPoints` / `paramsJson` / `downloads` → JSON 字符串；编辑时反向解析（解析失败降级为空数组，不阻断编辑）。

### 1.3 前台（`site-web`）
| 文件 | 说明 |
|---|---|
| `src/api/site.js` | 新增 `getBanners(position)` |
| `src/components/BannerCarousel.vue` | 6s 自动轮播、hover 暂停、左右箭头、圆点；图片/视频双支持（视频 `muted loop playsinline` + poster）；无数据时回退静态 Hero |
| `src/views/HomeView.vue` | 并行拉取 banner / 分类树 / 推荐产品 |
| `src/styles/main.css` | 轮播样式（460px 高、渐变遮罩、响应式 360px） |

## 2. 验证记录（实跑）

| # | 用例 | 结果 |
|---|---|---|
| 1 | `GET /api/site/banner` | `code=200`，**2 条**（`让厨房更懂生活`、`大风量 · 净烟体验`），**过期条目不返回** |
| 2 | `GET /api/site/banner?position=product` | 1 条（`产品中心 Banner`） |
| 3 | 后台 `GET /cms/banner/list`（token） | `total=4, rows=4` |
| 4 | 校验：视频类型不带 `videoUrl` | `code=500 视频 Banner 必须填写视频地址` |
| 5 | 新增 Banner（品牌页） | `code=200`；测试后已删除 |
| 6 | 后台构建 | `DONE Build complete`（0 个编译错误，含 Banner/产品新页面） |
| 7 | 站点构建 | `built in 1.12s`，含 `BannerCarousel` |
| 8 | 前台运行 | `/` 200；首页已使用接口 Banner |
| 9 | 后台运行 | 5174 200；Banner 页面在「内容管理 → Banner 管理」可访问 |
| 10 | 产品可视化 | 详情接口返回 `images=2 / paramGroups=1`，与编辑器数据一致 |

> 测试期间临时关闭过验证码用于取 token，完成后已恢复 `sys.account.captchaEnabled=true` 并重启后端；当前 8080/5174/80 三端运行中。

## 3. 关键决策

| # | 决策 | 原因 |
|---|---|---|
| 1 | Banner **物理删除** | `cms_banner` 表设计无 `del_flag`，与 `docs/03` 一致 |
| 2 | 时间窗在 SQL 过滤 | 免定时任务，后台保存即生效 |
| 3 | 图片/视频双类型：视频用 `imageUrl` 作封面 | 与表字段语义一致（`image_url` = 图片/视频封面） |
| 4 | 图集用 `image-upload` 多图，提交时转 JSON | 复用若依组件；DB 仍是 `json` 列 |
| 5 | 参数分组可视化（分组 → 参数行） | 运营不需要手写 JSON；后端仍做 JSON 校验兜底 |
| 6 | 轮播无数据回退静态 Hero | 新环境未配 Banner 也不空屏 |

## 4. 遗留与 Day 5 计划

| 项 | 状态 | Day 5 |
|---|---|---|
| 线索模块（预约/加盟） | 未实现 | `CmsLead` + `/api/site/lead` + 图形验证码 + 60s 频控 + 后台跟进/导出 |
| 文章 / 单页 / 门店 / 系列 | 未实现 | 按页面优先级实现（文章列表 + 单页 blocks 最小实现） |
| 首页其余楼层 | 未实现 | 品牌故事 / 影像馆（可先用单页/文章数据） |
| SEO | title/meta + 轮播 | `sitemap.xml` / `robots.txt` / JSON-LD |
| OSS | 本地 `uploadPath` | 接 OSS + CDN，替换占位图 |
| Git | 未安装 | 安装后 `git init` + 提交 |
