# Day 3 · 前端工程与联调（2026-11-18）

> 方案 B 第 3 天：落地 `site-admin`（Vue2 后台，5174）与 `site-web`（Vue3 前台，80），打通「后台录入 → 前台展示」。
> 验收：两个前端可构建、可启动、可代理到后端；前台首页/产品列表/详情读取真实接口数据。

## 1. 交付物

### 1.1 site-admin（后台）
| 项 | 内容 |
|---|---|
| 来源 | 复制 RuoYi-Vue 3.9.1 的 `ruoyi-ui`（Vue 2.6 + Element UI 2.15 + vue-cli 4） |
| 裁剪 | 删除 demo 的 `views/system/product|productCategory|customer|vatti`、对应 `api/system/*`、`router` 里客户管理静态路由、`api/system/productSpec.js` |
| 品牌 | `.env.*` → `VUE_APP_TITLE = site 官网管理后台`；`public/index.html` 标题；`package.json` name/description |
| 新增 API | `src/api/cms/category.js`、`src/api/cms/product.js` |
| 新增页面 | `src/views/cms/category/index.vue`（树表 + 上级栏目 + 封面上传 + SEO + 删除保护）<br>`src/views/cms/product/index.vue`（分页列表 + 栏目筛选 + 主图/JSON 字段 + 推荐/状态/发布时间 + SEO） |
| 端口/代理 | 5174（`vue.config.js` 默认），`/dev-api` → `http://localhost:8080` |

> 菜单由后端 `sys_menu`（3000 段）动态生成，`component` 为 `cms/category/index`、`cms/product/index`，与文件路径一致。

### 1.2 site-web（官网前台）
| 文件 | 说明 |
|---|---|
| `package.json` / `vite.config.js` | Vue 3.5 + Vite 6 + Vue Router 4 + Vuex 4 + axios；端口 80，`/api` 代理到 8080，别名 `@` |
| `.env.development` / `.env.production` | `VITE_API_BASE`（开发直连 8080，生产同域） |
| `src/api/request.js` | axios 实例，统一解包 `AjaxResult.data`，`code!==200` 抛错 |
| `src/api/site.js` | config / category tree / product page / recommend / detail |
| `src/store/modules/config.js` | Vuex 站点配置（**不用 Pinia**） |
| `src/components/` | `AppHeader`、`AppFooter`、`ProductCard`（hover 换图 + 图片兜底） |
| `src/views/` | `HomeView`（Hero + 分类 + 明星产品）、`ProductListView`（分类侧栏 + 搜索 + 分页）、`ProductDetailView`（图集/卖点/参数/下载/相关）、`NotFoundView` |
| `src/utils/` | `seo.js`（title/meta）、`image.js`（占位图 + onerror 兜底） |
| `src/styles/main.css` | 设计 Token + 布局 + 卡片/详情样式（**无 Tailwind**） |

## 2. 关键决策

| # | 决策 | 原因 |
|---|---|---|
| 1 | 后台沿用 **Vue2 + Element UI**（复制 `ruoyi-ui`） | `docs/00`/`docs/11` 已确认；与 RuoYi 3.9.1 后端、代码生成器、权限指令一致 |
| 2 | 前台 **新工程**、Vue3 + Vuex + 原生 CSS | 方案 B 要求干净；不用 Element Plus、不引 Tailwind、暂不预渲染 |
| 3 | 产品 JSON 字段（图片/卖点/参数/下载）**先用文本域编辑** | 后端已做 JSON 语法校验；可视化编辑器排 Day 4，避免 Day 3 范围膨胀 |
| 4 | 前台图片统一 `onerror` 兜底占位图 | 演示数据图片是占位路径，避免破图 |
| 5 | 列表/详情使用 history 路由，Vite dev 自带 SPA fallback | 与规划 `docs/06` 一致 |
| 6 | 页面权限用 `v-hasPermi`，与菜单 `cms:*` 对应 | 与 `sql/03` 的权限串一致 |

## 3. 验证记录（实跑）

| # | 用例 | 结果 |
|---|---|---|
| 1 | `site-admin` 安装 | `npm install` 成功，1477 包 / 27s |
| 2 | `site-admin` 生产构建 | `DONE Build complete`（含新增 `views/cms/*`） |
| 3 | `site-admin` 开发启动 | `Compiled successfully in 17627ms`，http://localhost:5174 → **200**，标题 `site 官网管理后台` |
| 4 | 后台代理 | `GET /dev-api/captchaImage` → `code=200，captchaEnabled=true` |
| 5 | `site-web` 安装 | `npm install` 成功，60 包 / 4s |
| 6 | `site-web` 生产构建 | 92 modules，`index.js 145.29KB / gzip 55.8KB`（首屏预算内） |
| 7 | `site-web` 开发启动 | `VITE v6.4.1 ready in 761ms`，http://localhost/ → **200** |
| 8 | 前台代理 | `GET /api/site/config` → `code=200，data.name="site 官网"` |
| 9 | SPA 路由 | `GET /product` → 200（返回 index.html） |
| 10 | 后端 | 启动正常，无 ERROR；8080/80/5174 三进程并存 |

> 验证完成后已停止三个进程（netstat 无 80/5174/8080 监听），并清理构建日志与 `dist/`。

## 4. 当前可用的完整链路

```
后台（5174，admin/admin123）
  └─ 登录 → 内容管理 → 栏目/产品（新增/修改/删除，图片上传，JSON 字段）
前台（80）
  ├─ 首页：站点名/分类 chips/明星产品（/api/site/config + category/tree + product/recommend）
  ├─ 产品中心：分类筛选 + 搜索 + 分页（/api/site/product/page）
  └─ 详情：图集/卖点/参数/下载/相关（/api/site/product/{id}）
```

## 5. 遗留与 Day 4 计划

| 项 | 状态 | Day 4 |
|---|---|---|
| 后台页面人工点检 | 仅构建/启动级验证 | 用 admin 账号走一遍栏目/产品增改删（含图片上传） |
| 产品 JSON 可视化编辑 | 文本域 | 参数分组动态表格 + 图集上传（仍存 JSON） |
| Banner/文章/单页/门店/线索 | 未实现 | 按页面优先级补后端 + 后台页面（先 Banner、线索） |
| 官网首页 Banner | Hero 静态背景 | 接 `cms_banner` + 轮播组件 |
| 表单（预约/加盟） | 未实现 | `/api/site/lead` + 图形验证码 + 60s 频控 + 蜜罐 |
| SEO | title/meta 已做 | `sitemap.xml`/`robots.txt` + JSON-LD；预渲染 spike |
| OSS | 本地 `uploadPath` | 接 OSS + CDN，替换演示图片路径 |
| Git | 未安装 | 安装后 `git init` + 首次提交 |
