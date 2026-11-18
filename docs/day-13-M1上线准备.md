# Day 13 · M1 上线准备（Git / 生产配置 / Nginx / 安全自检 / 冒烟）

> 目标：把「能跑」变成「可上线」。本轮全部不依赖外部资源，完成后可直接进入 OSS 接入与服务器部署（M2）。

## 1. 交付物

| 类别 | 文件 | 说明 |
|---|---|---|
| Git | `.git/`、`.gitignore` | 首次初始化，`main` 分支，首个提交与 `v0.1.0` 标签 |
| 生产配置 | `ruoyi-admin/src/main/resources/application-prod.yml` | 端口/上传/日志/DB/Redis 环境变量化；关闭 Swagger 与 Druid 控制台；JWT 密钥 fail-fast |
| 框架改造 | `ResourcesConfig.java`、`application.yml` | CORS 来源由硬编码 `*` 改为 `cors.allowed-origins`（默认 `*`，生产用 `SITE_CORS_ORIGINS`） |
| 日志 | `logback.xml` | 日志目录支持 `SITE_LOG_PATH`，默认 `./logs` |
| 部署 | `deploy/nginx/site.conf` | 前台 + 后台（`/prod-api/`）+ `/api/`、`/profile/` 反代、缓存与安全头、Swagger/Druid 404 兜底 |
| 部署 | `deploy/systemd/site-server.service` | systemd 服务（环境文件、重启策略、最小权限） |
| 部署 | `deploy/env.example`、`deploy/env.example.ps1` | Linux / Windows 环境变量样例 |
| 安全 | `deploy/security-hardening.sql` | 禁用演示账号、确认验证码、核对 `site.baseUrl` |
| 冒烟 | `bin/smoke.ps1`、`bin/smoke.bat` | 一键检查前台接口/页面 + 后台登录与 10 个核心接口 + 未授权 401 |
| 文档 | `deploy/README.md` | 产物目录、环境变量、数据库、systemd、Nginx、发布回滚 |

## 2. 关键决策

1. **profile 顺序 `druid,prod`**：`application-druid.yml` 定义本机连接串，`prod` 覆盖 host/端口/库名并关闭监控台；顺序反了会被 druid 覆盖回 `localhost`。
2. **JWT 密钥 fail-fast**：`prod` 中 `token.secret: ${SITE_TOKEN_SECRET}`（无默认值），未配置则启动失败，避免生产误用开发默认密钥。
3. **关闭 Swagger / Druid**：`springdoc.*.enabled=false`、`spring.datasource.druid.statViewServlet.enabled=false`；Nginx 对 `/swagger-ui`、`/v3/api-docs`、`/druid` 再返回 404。
4. **CORS 收紧**：默认 `*` 仅用于开发；生产 `SITE_CORS_ORIGINS=https://www.example.com`。验证：允许来源回显 `Access-Control-Allow-Origin`，非法来源被 Spring 直接 403。
5. **RefererFilter 默认关闭**：若依该过滤器会拒绝**无 Referer** 的请求（curl、健康检查、监控探针），生产开启需确认所有入口均携带 Referer，故 `prod` 保持 `false` 并在配置中注明。
6. **冒烟脚本编码**：Windows PowerShell 5.1 执行含中文的 `.ps1` 必须带 UTF-8 BOM，否则解析报 `Unexpected token '}'`；文件已存为 UTF-8 BOM。
7. **验证码友好**：脚本在验证码开启且未提供 `-CaptchaCode` 时，会把验证码图片落到 `logs/smoke-captcha.png` 并以退出码 2 结束，不会误报失败。

## 3. 安全自检结果

| 项 | 现状 | 结论 |
|---|---|---|
| 图形验证码 | `sys.account.captchaEnabled=true`（验证期间临时关闭后已恢复） | ✅ |
| 演示账号 | `content` / `sales` 存在，密码 `admin123` | ⚠️ 上线前执行 `deploy/security-hardening.sql` 禁用 |
| admin 口令 | 仍为 `admin123` | ⚠️ 上线前后台改密；`pwd_update_date` 为空 |
| JWT 密钥 | 开发有默认值；生产强制环境变量 | ✅ |
| DB 密码 | 开发用环境变量注入；生产独立账号 | ✅ |
| Druid 控制台 | 生产 profile 已关闭（`No static resource druid`） | ✅ |
| Swagger | 生产 profile 已关闭 | ✅ |
| CORS | 可配置，生产白名单 | ✅ |
| 上传限制 | multipart 10MB/20MB；扩展名白名单（图片/文档/音视频） | ✅ |
| 富文本 | 服务端正则清洗 + 前端 DOMPurify | ✅ |
| 越权 | 未登录访问 `/cms/**` 返回 `code:401`；`content` 访问线索 403（Day10 已验证） | ✅ |
| 日志 | 支持独立目录、按天滚动、保留 60 天 | ✅ |
| HTTPS/HSTS | Nginx 模板已备，待证书 | ⏳ |
| OSS | 未接入，图片仍落本地 | ⏳ |

## 4. 冒烟脚本

```powershell
# 前台 + 页面（验证码开启也可跑）
.\bin\smoke.ps1 -SkipAdmin

# 全量（验证码开启时：先跑一次生成 logs\smoke-captcha.png，再带识别结果）
.\bin\smoke.ps1
.\bin\smoke.ps1 -CaptchaCode 12

# 指定环境
.\bin\smoke.ps1 -BaseUrl https://api.example.com -SiteUrl https://www.example.com -SkipAdmin
```

覆盖：前台 9 个 API + sitemap/robots + 6 个页面；后台登录 + `/getInfo`、看板、栏目/系列/产品/Banner/文章/单页/门店/线索列表 + 未授权 401。
退出码：`0` 全通过；`1` 有失败；`2` 验证码待人工识别（后台部分跳过）。

## 5. 本轮实跑记录（2026-11-19）

| # | 场景 | 结果 |
|---|---|---|
| 1 | `mvn -DskipTests package`（CORS/日志/prod 配置改动后） | 成功，jar 86.9 MB |
| 2 | 8080 开发实例冒烟（验证码临时关闭） | **PASS 29 / FAIL 0 / WARN 0**，exit 0 |
| 3 | 8081 生产 profile 实例冒烟（`druid,prod`，独立上传/日志目录与 JWT） | **PASS 23 / FAIL 0 / WARN 0**，exit 0 |
| 4 | 生产实例启动日志 | `The following 2 profiles are active: "druid", "prod"`；5.7s 启动 |
| 5 | `/druid/`、`/swagger-ui.html`、`/v3/api-docs` | 已无对应资源（返回 `No static resource ...`，Nginx 再 404） |
| 6 | CORS | `Origin: http://localhost` 回显；`http://evil.example` 被拒 403 |
| 7 | 生产日志目录 | `logs/prod/sys-info.log` 正常写入 |
| 8 | 验证码恢复 | 未带验证码登录返回「验证码已失效」 |
| 9 | 常规冒烟（验证码开启，`-SkipAdmin`） | **PASS 17 / FAIL 0 / WARN 0**，exit 0 |
| 10 | 日志文件命名 | 增加毫秒，连续运行不再互相覆盖 |

## 6. 发布与回滚（摘要，详见 `deploy/README.md`）

1. 备份：`mysqldump --single-transaction site > site-$(date +%F).sql`，保留上一版 jar 与两个 `dist`。
2. 发布：替换 `site-server.jar` → `systemctl restart site-server`；`dist` 目录整体替换（建议按 tag 保留 `dist-<tag>`）。
3. 回滚：恢复上一版 jar/dist → 重启服务；数据库回滚需停服后导入备份。
4. 每次发布后执行 `bin\smoke.ps1`（生产带域名参数）并人工抽查图片上传、线索提交、导出。

## 7. 遗留（M2 及以后）

- OSS：AK/SK、Bucket、自定义域名；上传改 OSS 并迁移存量 `uploadPath`，Nginx `/profile/` 改 CDN。
- 服务器/域名/备案/证书：按 `deploy/README.md` 执行；HTTPS 段启用后开 HSTS。
- 门店地图 Key、短信服务（可选）。
- 功能增强：区块 `video`/`faq`、系列聚合页、相关产品推荐、站内搜索。
- 性能：`vite-ssg` 预渲染、图片 WebP/懒加载、CDN 缓存策略。
