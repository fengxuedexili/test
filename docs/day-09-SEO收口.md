# Day 9 · SEO 收口（2026-11-18）

> 方案 B 第 9 天：sitemap、robots、canonical、Open Graph、JSON-LD（Organization / Product / NewsArticle / Breadcrumb）。
> 验收：`/api/site/sitemap.xml` 与 `/api/site/robots.txt` 可访问且内容正确；前台页面注入 canonical / og / JSON-LD；404 页面 noindex。

## 1. 交付物

### 1.1 后端
| 文件 | 说明 |
|---|---|
| `SiteSeoController` | `GET /api/site/sitemap.xml`（application/xml）、`GET /api/site/robots.txt`（text/plain） |
| `sql/12-site-seo.sql` | 新增站点参数 `site.baseUrl`（默认 `http://localhost`，生产填正式域名） |

**sitemap 收录范围**（只含 `status=0`、`del_flag=0`、已发布）：
- 静态页：`/`、`/product`、`/news`
- 栏目：`/product?categoryId={id}`（`cms_category` 正常状态）
- 产品：`/product/d/{id}`
- 文章：`/news/{id}`
- 单页：`/p/{slug}`

**robots.txt**
```
User-agent: *
Disallow: /admin/
Disallow: /api/
Allow: /

Sitemap: http://localhost/api/site/sitemap.xml
```

### 1.2 前台（`site-web`）
| 文件 | 变更 |
|---|---|
| `utils/seo.js` | 新增 canonical、`robots`、`og:title/og:description/og:image/og:url`；新增 `setJsonLd()`、`absoluteUrl()` |
| `HomeView` | 注入 `Organization` JSON-LD |
| `ProductDetailView` | 注入 `Product`（sku=型号、image=图集、availability）+ `BreadcrumbList`；og:image 取首图 |
| `NewsDetailView` | 注入 `NewsArticle`（headline/datePublished/author/image）；og:image 取封面 |
| `NotFoundView` | `robots: noindex,nofollow` |

## 2. 验证记录

| # | 用例 | 结果 |
|---|---|---|
| 1 | `GET /api/site/sitemap.xml` | HTTP 200，`application/xml;charset=UTF-8`，**16 个 `<loc>`** |
| 2 | sitemap 内容 | `/`、`/product`、`/news`、5 个栏目、4 个产品、2 篇文章、2 个单页，全部为 `site.baseUrl` 绝对地址 |
| 3 | `GET /api/site/robots.txt` | HTTP 200，`text/plain`，含 `Disallow: /admin/`、`Disallow: /api/` 与 Sitemap 行 |
| 4 | 经前台代理（80）访问 sitemap/robots | 均 200，URL 数一致（16） |
| 5 | 前台构建 | `built in 1.20s`（SEO 工具与 JSON-LD 编译通过） |
| 6 | 页面 | `/`、`/product`、`/news`、`/news/1`、`/p/privacy` 均 200 |

> JSON-LD 由浏览器端注入，可用「查看网页源代码」看不到（SPA），需在开发者工具 Elements 中查看 `script[type="application/ld+json"]`；若后续上预渲染，JSON-LD 会出现在构建产物中。

## 3. 配置与运维

| 项 | 说明 |
|---|---|
| `site.baseUrl` | 后台「系统管理 → 参数设置」可改；生产务必改为 `https://正式域名` |
| 缓存 | 当前 sitemap 每次实时查询（数据量小）；上量后可加 Redis 5~10 分钟缓存 |
| 分片 | 内容 < 5 万条无需分片；超出时改为 `sitemap-1.xml` + 索引 |
| Nginx | 生产建议把 `/sitemap.xml`、`/robots.txt` 反代到后端，或在发布时静态化 |

## 4. 遗留与 Day 10 计划

| 项 | 状态 | Day 10 |
|---|---|---|
| 门店 / 系列 | 未实现 | `cms_store`（省市区、地图）、`cms_series` |
| 首页「最新资讯」楼层 | 未实现 | 复用 `/api/site/article/page?pageSize=3` |
| 单页 blocks 可视化编辑 | JSON 文本域 | 区块表单化（richText/imageText） |
| 预渲染 | 未做 | `vite-ssg` 兼容性 spike（SEO 加分项） |
| OSS / 角色 / 看板 | 未做 | 上线前收口 |
| 富文本服务端清洗 | 仅前端 DOMPurify | 引入 Jsoup 白名单 |
