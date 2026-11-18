# Day 5 · 线索模块（2026-11-18）

> 方案 B 第 5 天：最后一个 P0 功能——线索（预约/加盟/招聘/售后）从提交到跟进、导出的全链路。
> 验收：前台表单带验证码可提交、频控与蜜罐生效、后台可查看/跟进/导出。

## 1. 交付物

### 1.1 后端（`ruoyi-cms`）
| 文件 | 说明 |
|---|---|
| `domain/CmsLead.java` | 与 `cms_lead` 对齐；带 `@Excel` 导出注解；额外承载 `code/uuid/website/requestId`（不入库） |
| `mapper/CmsLeadMapper` + `resources/mapper/cms/CmsLeadMapper.xml` | 列表（类型/状态/手机号/来源/时间范围）、详情、新增、跟进（状态/处理人/备注）、删除 |
| `service/ICmsLeadService` + `impl/CmsLeadServiceImpl` | **验证码 + 频控 + 蜜罐 + 幂等**（见第 2 节） |
| `exception/SiteBizException` + `SiteExceptionHandler` | 业务码 4001/4002/4003 原样返回（`@Order(HIGHEST_PRECEDENCE)` 优先于若依全局异常处理） |
| `controller/admin/CmsLeadController` | `/cms/lead/**`：list/get/edit/remove/export（权限 `cms:lead:*`） |
| `controller/site/SiteLeadController` | `POST /api/site/lead`（`@Anonymous` + `@RateLimiter(60s, 30次)`） |
| `sql/08-lead-demo.sql` | 2 条演示线索（待处理 / 已联系） |

### 1.2 后台（`site-admin`）
| 文件 | 说明 |
|---|---|
| `src/api/cms/lead.js` | list/get/update/del |
| `src/views/cms/lead/index.vue` | 列表（类型/状态/手机号/时间范围）、详情+跟进弹窗（状态/处理人/备注）、删除、**导出 Excel** |

### 1.3 前台（`site-web`）
| 文件 | 说明 |
|---|---|
| `src/api/site.js` | `getCaptcha()`、`submitLead()` |
| `src/api/request.js` | 业务错误挂载 `error.code`，便于按 4001/4003 分流 |
| `src/views/BookingView.vue` | 预约表单：类型/姓名/手机/省市区/需求、图形验证码（点击刷新）、蜜罐隐藏域、`requestId`、UTM 采集、4003 倒计时 |
| `src/router/index.js`、`components/AppHeader.vue` | 新增 `/booking` 路由与导航入口 |
| `src/styles/main.css` | 表单样式（含移动端单列） |

## 2. 防刷设计（实测项）

| 机制 | 规则 | 错误码 |
|---|---|---|
| 图形验证码 | 复用若依 `/captchaImage`；Redis 校验后**立即删除**（一次性） | 4001 |
| 同手机号 | 60 秒锁 + 24 小时 5 次 | 4003 |
| 同 IP | 1 小时 10 次 + 24 小时 30 次 | 4003 |
| 蜜罐 | `website` 非空 → 返回成功但**不入库** | — |
| 幂等 | `requestId` 5 分钟内重复 → 直接成功，不重复入库 | — |
| 接口级 | `@RateLimiter(time=60, count=30)` | 429/500 提示 |

## 3. 验证记录（实跑，验证码为真实验证码）

> 验证方式：从 Redis `captcha_codes:{uuid}` 读取本次验证码结果后提交，全程未关闭验证码。

| # | 用例 | 结果 |
|---|---|---|
| 1 | 合法提交（手机 13700000011） | `code=200 提交成功`，线索数 +1 |
| 2 | 同手机号立即再提交 | `code=4003 提交过于频繁，请 60 秒后再试` |
| 3 | 错误验证码 | `code=4001 验证码错误或已过期` |
| 4 | 手机号格式错误（`123`） | `code=4002 手机号格式不正确` |
| 5 | 蜜罐 `website` 非空 | `code=200`，**入库数不变** |
| 6 | 同 `requestId` 重复提交 | 两次均 `200`，**只入库 1 条** |
| 7 | 后台登录（真实验证码） | `code=200`，拿到 token |
| 8 | 后台列表 | `total=5`（含提交数据） |
| 9 | 跟进：状态/处理人/备注 | `code=200`，DB `status=1 / handler=admin / handle_time` 已写入 |
| 10 | 导出 Excel | HTTP 200，`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`，4595 字节 |
| 11 | `site-web` 构建 / `site-admin` 构建 | 均成功（`built in 1.10s` / `DONE Build complete`） |
| 12 | `/booking` 页面 | HTTP 200（开发服务器） |

> 测试数据已清理（只保留 2 条演示线索）；测试产生的 Redis 频控 key 已删除；验证码保持开启。

## 4. 过程中修复的三个问题

| 问题 | 原因 | 处理 |
|---|---|---|
| 业务码被转成 500 | 若依 `GlobalExceptionHandler` 兜底 `RuntimeException`，且与自定义 Advice 同序 | `SiteExceptionHandler` 加 `@Order(Ordered.HIGHEST_PRECEDENCE)` |
| 用 Redis 读验证码总是不匹配 | 若依 `RedisCache` 用 JSON 序列化，值为带引号的 `"24"` | 仅影响测试脚本（去掉引号）；应用内走 `getCacheObject` 无此问题 |
| 重复提交被限流拦住 | `@RateLimiter` 10 次/60s 对同一出口 IP 偏紧 | 调整为 30 次/60s；真正的精细限制在 service（手机号/IP） |

## 5. 遗留与 Day 6 计划

| 项 | 状态 | Day 6 |
|---|---|---|
| 文章 / 单页 / 门店 / 系列 | 未实现 | 文章列表+详情、单页 blocks 最小实现（隐私政策 P0） |
| SEO | title/meta | `sitemap.xml` / `robots.txt` / JSON-LD |
| OSS | 本地 `uploadPath` | 接 OSS + CDN |
| 后台首页看板 | 默认若依页 | 待处理线索 / 产品数 / 最新文章 |
| 权限角色 | 仅 admin | 建 `content_ops`、`lead_sales` 角色并验证越权 |
| Git | 未安装 | 安装后 `git init` + 首次提交 |
