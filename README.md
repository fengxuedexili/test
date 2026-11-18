# site · 企业官网（前后端分离）

> 方案 B（从零搭建）的**正式工程**。基座：RuoYi 3.9.1 / Spring Boot 3.5.11 / JDK 17+。
> 规划文档在 `..\1117\docs\`，本工程按 `docs/11` 的「方案 B」执行；Day 1 = 基座与数据层，Day 2 = 内容模型与前台接口，Day 3 = 前端工程与联调，Day 4 = Banner 与可视化编辑，Day 5 = 线索模块，Day 6 = 图片链路修复，Day 7 = 产品看点，Day 8 = 文章与单页，Day 9 = SEO 收口，Day 10 = 上线收口（安全与权限）+ 首页资讯，Day 11 = 产品系列与门店查询，Day 12 = 后台看板 + 单页区块可视化，Day 13 = M1 上线准备（Git / 生产配置 / Nginx / 安全自检 / 冒烟）。

## 1. 当前状态（Day 1–13 已完成）

| 项 | 状态 |
|---|---|
| 后端工程（8 模块，含新增 `ruoyi-cms`） | ✅ `mvn clean package` 通过，产物 `ruoyi-admin/target/site-server.jar` |
| 数据库 `site` | ✅ 26 张表（18 张 `sys_*` + 8 张 `cms_*`）；4 栏目 / 5 产品演示数据 |
| 后台菜单 | ✅ 内容管理（看板/栏目/系列/产品/Banner/文章/单页/门店/线索）已入库，`menu_id` 3000 段 |
| 站点参数 / 字典 | ✅ `site.*` 9 个参数；5 类业务字典（16 条） |
| `cms` 领域模型 | ✅ 栏目 + 产品（domain / mapper / service / XML，含树形、软删除、JSON 解析） |
| 前台接口 | ✅ `/api/site/config`、`category/tree`、`product/{page,recommend,{id}}`、`banner`、`lead`、`article/{page,{id}}`、`page/{slug}`、`store`、`store/provinces`、`sitemap.xml`、`robots.txt` |
| 后台接口 | ✅ `/cms/{dashboard,category,series,product,banner,lead,article,page,store}/**`（`@PreAuthorize` + 操作日志） |
| **Banner 模块** | ✅ 后台 CRUD + 前台 `/api/site/banner`（时段过滤）+ 首页轮播组件 |
| **产品可视化编辑** | ✅ 图集多图上传、卖点/参数分组/下载资料动态行（提交时转 JSON） |
| **产品看点（批量详情图）** | ✅ 后台批量上传+说明+排序；前台详情页全宽长图展示（淘宝风格） |
| **线索模块（P0）** | ✅ 前台预约表单（验证码/蜜罐/幂等/频控）+ 后台跟进与 Excel 导出 |
| **文章模块** | ✅ 后台富文本编辑 + 前台 `/news` 列表与详情（DOMPurify 净化） |
| **单页模块** | ✅ `blocks` 渲染 `richText` / `imageText`；隐私政策 `/p/privacy`、关于我们 `/p/about` |
| **SEO 收口** | ✅ sitemap.xml / robots.txt / canonical / Open Graph / JSON-LD（Organization、Product、NewsArticle、Breadcrumb） |
| **富文本安全** | ✅ 服务端正则清洗（script/iframe/事件属性/javascript:）+ 前端 DOMPurify |
| **角色权限** | ✅ `content_ops`（内容）与 `lead_sales`（线索）角色 + 演示账号；越权已验证 403 |
| **首页资讯楼层** | ✅ 首页展示最新 3 篇资讯（日期/标题/摘要） |
| **产品系列模块** | ✅ 后台 `/cms/series` CRUD；产品表单可按栏目选择系列；演示系列 4 条（已实跑增改删） |
| **门店模块** | ✅ 后台 `/cms/store` CRUD（省/市/类型/状态筛选）；前台 `/store` 按省份+类型筛选、电话与高德导航（已实跑，含停用过滤） |
| **运营看板** | ✅ `/cms/dashboard/summary`：线索/产品/文章/门店计数 + 近 7 天线索趋势 + 最近线索与最新文章；`content_ops`/`lead_sales` 均可访问 |
| **单页区块可视化** | ✅ 正文（Quill）/ 图文（上传+左右互换）区块编辑器，上下移动/删除/校验；服务端清洗与 JSON 校验不变 |
| **后台前端 `site-admin`** | ✅ Vue2 + Element UI，5174；看板/栏目/系列/产品/Banner/线索/文章/单页/门店管理；`build:prod` 通过 |
| **官网前台 `site-web`** | ✅ Vue3 + Vite6 + Vuex，80；首页/产品/资讯/门店/预约/单页；`npm run build` 通过 |
| 安全基线 | ✅ 去掉了 demo 的匿名上传/越权白名单；DB 密码与 JWT secret 支持环境变量覆盖 |
| 图片/上传链路 | ✅ 上传目录改为项目根 `uploadPath`（`SITE_UPLOAD_PATH` 可覆盖）；路径反斜杠归一；前台/后台代理 `/profile` |
| **M1 上线准备** | ✅ Git（`1af3392` / `v0.1.0`）；`application-prod.yml`（关 Swagger/Druid、JWT fail-fast）；Nginx/systemd/env 模板；CORS 可配置；一键冒烟（29 项） |
| 上线待办（OSS / 域名证书 / 地图 / 预渲染） | ⏳ 需外部资源或单独排期 |

## 2. 目录结构

```
site/
├─ pom.xml                     父工程（8 modules）
├─ ruoyi-admin/                启动模块（controller/tool/common，产物 site-server.jar）
├─ ruoyi-common/               通用工具/BaseEntity/AjaxResult/@Anonymous
├─ ruoyi-framework/            安全/JWT/XSS/配置
├─ ruoyi-system/               用户/角色/菜单/字典/参数/日志
├─ ruoyi-generator/            代码生成
├─ ruoyi-quartz/               定时任务
├─ ruoyi-cms/                  ★ 官网业务模块（栏目/产品/[其余模块陆续]）
│  ├─ controller/admin/        后台 CRUD（@PreAuthorize）
│  ├─ controller/site/         前台只读（@Anonymous）
│  ├─ domain/ + domain/vo/     实体与前台 VO
│  ├─ mapper/ + service/impl/  MyBatis 接口与业务实现
│  └─ resources/mapper/cms/    XML 映射
├─ sql/                        01 基础库 → … → 15 看板菜单（详见 sql/README.md）
├─ deploy/                     ★ 生产部署：nginx / systemd / env 样例 / 安全加固 SQL / 部署手册
├─ bin/                        package/run/clean/stop + dev-server/dev-web/dev-admin + smoke（冒烟）
├─ site-admin/                 后台前端（Vue2 + Element UI，5174）
├─ site-web/                   官网前台（Vue3 + Vite6 + Vuex，80）
├─ docs/                       Day 1 ~ Day 13 施工记录
└─ logs/ uploadPath/           运行时（gitignore）
```

## 3. 环境要求（本机实测）

| 组件 | 版本 |
|---|---|
| JDK | 17+（本机 18.0.2.1，`java.version=17`） |
| Maven | 3.6.3+（本机 3.6.3） |
| MySQL | 8.0.33（服务 `MySQL80`） |
| Redis | 6379（无密码，服务 `Redis`） |
| Node | 20+（Day 2 前端用，本机 20.19.0） |

## 4. 首次初始化

### 4.1 建库与导入（按顺序）

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS site DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -uroot -p site < sql/01-sys-base-schema.sql
mysql -uroot -p site < sql/02-cms-schema.sql
mysql -uroot -p site < sql/03-cms-menu.sql
mysql -uroot -p site < sql/04-site-config.sql
mysql -uroot -p site < sql/05-cms-dict.sql
mysql -uroot -p site < sql/06-demo-data.sql      # 仅开发环境
mysql -uroot -p site < sql/07-banner-demo.sql    # 仅开发环境
mysql -uroot -p site < sql/08-lead-demo.sql      # 仅开发环境
mysql -uroot -p site < sql/09-fix-upload-paths.sql
mysql -uroot -p site < sql/10-product-detail-images.sql
mysql -uroot -p site < sql/11-article-page-demo.sql   # 仅开发环境
mysql -uroot -p site < sql/12-site-seo.sql
mysql -uroot -p site < sql/13-roles-demo.sql     # 仅开发环境（演示角色/账号）
mysql -uroot -p site < sql/14-store-series-demo.sql   # 仅开发环境（系列/门店演示）
mysql -uroot -p site < sql/15-dashboard-menu.sql      # 运营看板菜单与授权
```

> 说明：`01` 来自 RuoYi 3.9.1 运行库导出，只含 `sys_*`，已剔除先期 demo 的业务表/测试账号/自定义菜单；`06`~`08` 为演示数据（栏目/产品/Banner/线索），生产跳过；默认账号 `admin / admin123`。

### 4.2 数据库密码（不入库）

`ruoyi-admin/src/main/resources/application-druid.yml` 使用环境变量：

```powershell
$env:SITE_DB_PASSWORD='你的数据库密码'      # PowerShell
$env:SITE_DB_USERNAME='root'                # 可选，默认 root
```

```bash
export SITE_DB_PASSWORD='你的数据库密码'    # Linux/macOS
```

JWT 密钥同理：`SITE_TOKEN_SECRET`（有开发默认值，生产必须覆盖）。

上传目录：`ruoyi.profile` 默认 `D:/VUE-WorkSpace/2027/site/uploadPath`（项目根，`mvn clean` 不会删除）；换机器或生产用 `SITE_UPLOAD_PATH` 覆盖。前台/后台开发代理已加入 `/profile`，图片可直接预览。

### 4.3 构建与启动

```bash
bin\package.bat
bin\run.bat            # 需要先设置 SITE_DB_PASSWORD
```

或手动：

```bash
mvn -DskipTests clean package
java -jar ruoyi-admin/target/site-server.jar
```

启动后：
- 接口根：http://localhost:8080
- Swagger：http://localhost:8080/swagger-ui.html
- Druid：http://localhost:8080/druid（ruoyi / 123456，生产必改）

### 4.4 前端启动（Day 3+）

| 单元 | 目录 | 端口 | 开发 | 生产构建 |
|---|---|---|---|---|
| 后台 | `site-admin` | 5174 | `cd site-admin && npm install && npm run dev` | `npm run build:prod` → `dist/` |
| 前台 | `site-web` | 80 | `cd site-web && npm install && npm run dev` | `npm run build` → `dist/` |

一键开发脚本（独立窗口，终端关闭不会杀掉进程）：

```powershell
bin\dev-server.bat 你的数据库密码   # 后端 8080
bin\dev-web.bat                     # 前台 80
bin\dev-admin.bat                   # 后台 5174
```

- 后台代理：`/dev-api` → `http://localhost:8080`（`vue.config.js`），登录 `admin / admin123`。
- 前台：`.env.development` 的 `VITE_API_BASE=http://localhost:8080`；`vite.config.js` 另配了 `/api` 代理（把 `VITE_API_BASE` 留空即可走代理）。
- 菜单来自后端 `sys_menu`（3000 段），页面路径 `views/cms/category/index.vue`、`views/cms/product/index.vue`。

## 5. 验收接口（Day 1–2）

| 请求 | 期望 |
|---|---|
| `GET /captchaImage` | `code=200`，返回 `img`/`uuid`，`captchaEnabled=true` |
| `GET /api/site/config` | `code=200`，`data.name="site 官网"` |
| `GET /api/site/category/tree` | `code=200`，1 个根（厨房电器）+ 3 个子栏目 |
| `GET /api/site/product/page?pageSize=2` | `total=4`、`rows=2`（停用产品不返回） |
| `GET /api/site/product/page?categorySlug=range-hood` | `total=2` |
| `GET /api/site/product/recommend?limit=2` | 2 条推荐产品 |
| `GET /api/site/product/1` | 详情含 `images/sellingPoints/paramGroups/downloads/detailImages/related`，浏览量 +1 |
| `GET /api/site/banner` | 2 条（已过期条目不返回） |
| `GET /api/site/banner?position=product` | 1 条 |
| `GET /cms/banner/list`（admin token） | `total=4` |
| `POST /cms/banner`（视频类型缺 `videoUrl`） | `code=500 视频 Banner 必须填写视频地址` |
| `POST /api/site/lead`（合法 + 验证码） | `code=200 提交成功`，线索 +1 |
| 同手机号立即重复提交 | `code=4003 提交过于频繁，请 60 秒后再试` |
| 错误验证码 | `code=4001 验证码错误或已过期` |
| 手机号格式错误 | `code=4002 手机号格式不正确` |
| 蜜罐 `website` 非空 | `code=200`，不入库 |
| 重复 `requestId` | `code=200`，仅入库 1 条 |
| `POST /cms/lead/export`（admin token） | HTTP 200，返回 xlsx |
| `GET /api/site/article/page` | `code=200`，`total=2`，置顶优先 |
| `GET /api/site/article/1` | `code=200`，正文与相关阅读正常 |
| `GET /api/site/page/privacy` | `code=200`，`blocks=1` |
| `GET /api/site/page/about` | `code=200`，`blocks=2`（richText + imageText） |
| 前台页面 | `/news`、`/news/1`、`/p/privacy`、`/p/about` 均 200 |
| `GET /api/site/sitemap.xml` | 200，`application/xml`，**16 个 `<loc>`**（含栏目/产品/文章/单页） |
| `GET /api/site/robots.txt` | 200，含 `Disallow: /admin/` 与 Sitemap 行 |
| 富文本 `<script>`/`onerror` 入库 | 前台返回内容已移除（服务端清洗生效） |
| `content` 账号访问产品/线索/系统 | 产品 200；线索 403；系统用户 403 |
| `sales` 账号访问线索/产品 | 线索 200；产品列表/编辑 403 |
| `GET /cms/product/list`（无 token） | 业务码 `401` |
| `GET /cms/product/list`（admin token） | `total=5`，含 `categoryName` |
| `GET /system/user/list`（无 token） | 业务码 `401`（RuoYi 以 HTTP 200 + code 401 返回） |
| `POST /common/upload`（无 token） | 业务码 `401`（demo 的匿名上传漏洞已修复） |
| `POST /login`（无验证码） | `code=500`，`msg=验证码已失效` |
| `POST /login`（带正确验证码） | `code=200`，返回 `token` |
| `GET http://localhost/`（site-web） | 200，首页可渲染 |
| `GET http://localhost/api/site/config`（site-web 代理） | `code=200` |
| `GET http://localhost:5174/`（site-admin） | 200，标题 `site 官网管理后台` |
| `GET http://localhost:5174/dev-api/captchaImage`（site-admin 代理） | `code=200` |

> 后台产品 JSON 字段的**请求名是 `paramsJson`**（对应 DB 列 `params`），`images/sellingPoints/downloads` 仍为 JSON 字符串。原因：`BaseEntity` 已有 `params`（查询扩展 Map），同名会编译冲突。

## 6. 约定

- 业务代码只进 `ruoyi-cms`；`ruoyi-framework`/`ruoyi-system` 除配置外不改。
- 后台接口 `/cms/**`（`@PreAuthorize('cms:xxx:action')`），前台接口 `/api/site/**`（`@Anonymous` + 限流）。
- 数据表 `cms_` 前缀；通用字段沿用若依（`create_by/create_time/...`）。
- **JSON 列约定**：DB 列 `params` 对应实体 `paramsJson`（避开 `BaseEntity.params`）；`images` / `sellingPoints` / `downloads` 保持同名 JSON 字符串，由 service 解析为数组。
- 删除统一软删除（`del_flag='2'`）；前台查询必须带 `status='0' + del_flag='0' + 发布时间`。
- 前端端口约定：后台 5174（`/dev-api` 代理）、前台 80（`/api` 代理或 `VITE_API_BASE`）；前台不引 Element Plus / Tailwind / Pinia。
- 后台产品 JSON 字段（图集/卖点/参数/下载）已改为可视化编辑，提交时自动转 JSON；后端仍做 JSON 语法校验。
- 本地密钥/密码只走环境变量或 `application-local.yml`（已在 `.gitignore`）。

## 7. 生产部署（M1 已完成）

完整步骤见 **`deploy/README.md`**，要点：

| 步骤 | 文件/命令 |
|---|---|
| 环境变量 | `deploy/env.example` → `/etc/site/site.env`；`SPRING_PROFILES_ACTIVE=druid,prod` |
| 数据库 | `sql/01~05、10、12、15`（跳过 06/07/08/11/13/14 演示数据）→ `deploy/security-hardening.sql` |
| 后端服务 | `deploy/systemd/site-server.service` |
| Nginx | `deploy/nginx/site.conf`（前台 + `admin.` 子域 + `/prod-api/` 反代） |
| 冒烟 | `bin\smoke.ps1 -BaseUrl <api> -SiteUrl <site>`（验证码开启时先取 `logs\smoke-captcha.png`） |

生产 profile 覆盖：关闭 Swagger 与 Druid 控制台、`SITE_TOKEN_SECRET` 缺失即启动失败、CORS 白名单、独立上传/日志目录、日志按天滚动。

## 8. 文档索引

| 文档 | 内容 |
|---|---|
| `..\1117\docs\11-基座沿用评估与决策.md` | 为什么选方案 B |
| `..\1117\docs\02-技术架构.md` | 架构与分层 |
| `..\1117\docs\03-数据库设计.md` | 8 张 `cms_*` 表设计 |
| `..\1117\docs\04-接口清单.md` | 后台/前台接口 |
| `docs\day-01-基座与数据层.md` | Day 1 施工与验证记录 |
| `docs\day-02-内容模型与前台接口.md` | Day 2 施工与验证记录（栏目/产品/字典/接口） |
| `docs\day-03-前端工程与联调.md` | Day 3 施工与验证记录（site-admin / site-web） |
| `docs\day-04-banner与可视化编辑.md` | Day 4 施工与验证记录（Banner + 产品可视化） |
| `docs\day-05-线索模块.md` | Day 5 施工与验证记录（线索 + 防刷 + 导出） |
| `docs\day-06-图片链路修复与CMS功能核对.md` | Day 6：图片不更新的根因与修复 + CMS 功能核对矩阵 |
| `docs\day-07-产品看点.md` | Day 7：产品看点（批量详情图）设计与验证 |
| `docs\day-08-文章与单页.md` | Day 8：文章与单页（富文本/blocks/DOMPurify） |
| `docs\day-09-SEO收口.md` | Day 9：sitemap / robots / canonical / JSON-LD |
| `docs\day-10-上线收口与首页资讯.md` | Day 10：富文本清洗 / 角色权限 / 首页资讯 |
| `docs\day-11-系列与门店.md` | Day 11：产品系列 / 门店 CRUD 与前台 `/store` |
| `docs\day-12-看板与区块编辑器.md` | Day 12：运营看板 / 单页 blocks 可视化 |
| `docs\day-13-M1上线准备.md` | Day 13：Git / 生产配置 / Nginx / 安全自检 / 冒烟 |
| `deploy\README.md` | 生产部署手册（环境变量、systemd、Nginx、发布回滚） |
