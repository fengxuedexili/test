# Day 8 · 文章与单页（2026-11-18）

> 方案 B 第 8 天：补齐资讯（列表/详情/富文本）与单页（隐私政策/关于我们，blocks 渲染），并补上 P0 的隐私政策页面。
> 验收：后台可维护文章与单页；前台 `/news`、`/news/:id`、`/p/:slug` 正常渲染；富文本经 DOMPurify 净化。

## 1. 交付物

### 1.1 后端（`ruoyi-cms`）
| 文件 | 说明 |
|---|---|
| `CmsArticle` / `CmsPage` | 与 `cms_article`、`cms_page` 对齐（继承 `BaseEntity`） |
| `CmsArticleMapper(.xml)` | 后台列表（不含正文）、前台分页（置顶优先）、详情、相关推荐、软删除、浏览量 |
| `CmsPageMapper(.xml)` | 列表、详情、按 slug（仅正常状态）、slug 唯一校验、增改删 |
| `CmsArticleServiceImpl` | 置顶/发布过滤、详情浏览量 +1、相关推荐、默认值 |
| `CmsPageServiceImpl` | blocks JSON 解析、slug 唯一、JSON 合法性校验 |
| `SiteArticleVo` / `SitePageVo` | 前台 VO（列表不带正文） |
| `/cms/article/**`、`/cms/page/**` | 后台 CRUD（`cms:article:*`、`cms:page:*` + 操作日志） |
| `/api/site/article/{page,{id}}`、`/api/site/page/{slug}` | 前台匿名接口 |
| `sql/11-article-page-demo.sql` | 资讯分类 + 2 篇文章（1 置顶）+ 隐私政策 + 关于我们 |

### 1.2 后台（`site-admin`）
| 页面 | 能力 |
|---|---|
| 内容管理 → 文章管理 | 列表（封面/置顶/分类/发布时间/浏览/状态）、筛选、分页；新增/修改（分类、封面、摘要、**富文本 Editor**、作者/来源、置顶、发布时间、SEO） |
| 内容管理 → 单页管理 | 列表（别名/标题/状态/更新时间/前台链接）；新增/修改（slug、标题、**blocks JSON**、状态、SEO），带格式示例提示 |

### 1.3 前台（`site-web`）
| 文件 | 能力 |
|---|---|
| `NewsListView.vue` | 资讯列表：封面 + 标题 + 摘要 + 置顶标记 + 分页 |
| `NewsDetailView.vue` | 资讯详情：标题/时间/作者/浏览、封面、富文本正文、相关阅读、返回 |
| `PageView.vue` | 单页：按 blocks 渲染 `richText`、`imageText`（支持左右图文） |
| `utils/sanitize.js` | DOMPurify 净化富文本（新增依赖 `dompurify`） |
| 路由/导航 | `/news`、`/news/:id`、`/p/:slug`；顶部新增「新闻资讯」 |

> 单页地址：隐私政策 http://localhost/p/privacy ，关于我们 http://localhost/p/about

## 2. 关键决策

| # | 决策 | 原因 |
|---|---|---|
| 1 | 后台文章列表**不返回正文** | `longtext` 字段大，列表仅需卡片字段；编辑时按 ID 取详情 |
| 2 | 前台富文本用 **DOMPurify** 净化 | 后端未做服务端清洗，前端必须兜底；`v-html` 禁止直接渲染原始 HTML |
| 3 | 单页 blocks 用 JSON 编辑（一期） | 先保证隐私政策等 P0 页面可上线；可视化区块编辑器排后续 |
| 4 | blocks 只渲染 `richText` / `imageText` | 与 `docs/03` 的 type 约定一致，未知类型静默跳过 |
| 5 | 资讯分类复用 `cms_category(type=2)` | 不新增表；后台下拉展示层级 |

## 3. 验证记录

| # | 用例 | 结果 |
|---|---|---|
| 1 | `GET /api/site/article/page` | `code=200`，`total=2`，置顶文章 `top=true` 排第一 |
| 2 | `GET /api/site/article/1` | `code=200`，正文 172 字符，`related=1`，SEO 标题正确 |
| 3 | `GET /api/site/page/privacy` | `code=200`，`blocks=1`（richText） |
| 4 | `GET /api/site/page/about` | `code=200`，`blocks=2`（richText + imageText） |
| 5 | `GET /api/site/page/not-exist` | `code=500`，`页面不存在或已下线` |
| 6 | 前台页面 | `/news`、`/news/1`、`/p/privacy`、`/p/about` 全部 **200** |
| 7 | 后台页面 | `5174/` **200**（文章/单页菜单已存在） |
| 8 | 构建 | 后端 `BUILD SUCCESS`；`site-web built in 1.16s`；`site-admin DONE Build complete` |

## 4. 运行方式补充

新增三个开发脚本（独立窗口启动，避免终端关闭导致进程被杀）：

```powershell
bin\dev-server.bat 你的数据库密码   # 后端 8080
bin\dev-web.bat                     # 前台 80
bin\dev-admin.bat                   # 后台 5174
```

## 5. 遗留与 Day 9 计划

| 项 | 状态 | Day 9 |
|---|---|---|
| SEO 收口 | 页面 title/meta 已有 | `sitemap.xml`、`robots.txt`、JSON-LD、前台预渲染评估 |
| 门店 / 系列 | 未实现 | `cms_store`（省市区 + 地图）、`cms_series` |
| 首页资讯楼层 | 未实现 | 首页加「最新资讯」区块（复用 `/api/site/article/page`） |
| 单页区块可视化编辑 | JSON 文本域 | 区块编辑器（richText/imageText 表单化） |
| 富文本服务端清洗 | 未做 | 引入 Jsoup 白名单（与前端 DOMPurify 双层） |
| OSS / 角色 / 看板 | 未做 | Day 9~10 |
