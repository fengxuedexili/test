# Day 1 · 基座与数据层（2026-11-18）

> 方案 B（从零搭建）第 1 天施工记录。目标：干净后端基座 + `site` 库 + `ruoyi-cms` 模块骨架 + 第一个可验证接口。
> 验收口径：`mvn clean package` 通过、服务能启动、`/api/site/config` 匿名可读、匿名写接口返回 401。

## 1. 交付物

| # | 交付 | 位置 | 说明 |
|---|---|---|---|
| 1 | 父工程（8 modules） | `site/pom.xml` | 新增 `ruoyi-cms`，`name=site`，dependencyManagement 补充 cms 版本 |
| 2 | 后端框架模块 | `ruoyi-common/framework/system/quartz/generator` | 由 RuoYi 3.9.1 复制；无 demo 业务代码 |
| 3 | 启动模块 | `ruoyi-admin` | 产物改名为 **`site-server.jar`**；上传接口恢复鉴权 |
| 4 | 业务模块骨架 | `ruoyi-cms` | `controller/{admin,site}`、`domain(+vo)`、`mapper`、`service(+impl)`、`resources/mapper/cms` |
| 5 | 第一个接口 | `SiteConfigController` | `GET /api/site/config`（`@Anonymous`，读 `sys_config.site.*`） |
| 6 | 数据库 | `site`（MySQL 8.0.33） | 26 表：18 `sys_*` + 8 `cms_*` |
| 7 | SQL 脚本 | `sql/01~04 + README` | 基础库 → cms 表 → 菜单 → 站点参数 |
| 8 | 启停脚本 | `bin/package.bat`、`bin/run.bat` | 启动前校验 `SITE_DB_PASSWORD` |
| 9 | 工程文档 | `README.md`、`docs/day-01-*.md`、`.gitignore` | 含验收命令与约定 |

## 2. 关键决策（Day 1 定稿）

| # | 决策 | 理由 |
|---|---|---|
| 1 | **新建 `ruoyi-cms` 模块**（而非塞进 `ruoyi-system`） | 方案 B 是干净重建，按 `docs/09` 原设计与规划一致；业务与框架解耦，便于二期拆分 |
| 2 | 依赖方向 `ruoyi-admin → ruoyi-cms → {ruoyi-common, ruoyi-system}` | cms 复用 `sys_config`；无循环依赖（system 不依赖 cms） |
| 3 | 产物名 `site-server.jar` | 与规划中的部署单元 `site-server` 对齐；`bin/*.bat` 同步 |
| 4 | DB 密码 / JWT secret 走环境变量 | `application-druid.yml` 用 `${SITE_DB_PASSWORD:}`；避免重蹈 demo 明文凭据问题 |
| 5 | 基础库从**运行库导出**而非手写 | 无 Git/无外网环境，导出 `sys_*` 结构+数据最可靠；已核对与 3.9.1 一致 |
| 6 | 清理 demo 数据 | 菜单 2013~2018（文件上载/产品中心/客户管理）、用户 100~105、角色 100 全部删除 |
| 7 | 不保留 demo 的任何业务类 | `SysProduct*`、`VattiCustomer*`、`VerificationCode*`、`SysFileInfo*`、`MailService` 均未迁入 |

## 3. 数据层明细（实测）

| 项 | 结果 |
|---|---|
| 表总数 | **26**（18 `sys_*` + 8 `cms_*`） |
| `sys_user` | 2 条（`admin`=1、`ry`=2），无测试账号 |
| `sys_role` | 2 条（`admin`、`common`） |
| `sys_menu` | 131 条；无 2013~2018；「内容管理」3000~3224（8 个子模块 + 按钮） |
| `cms_*` | `cms_category` / `cms_series` / `cms_product` / `cms_banner` / `cms_article` / `cms_page` / `cms_store` / `cms_lead` |
| `sys_config.site.*` | 9 个键：`site.name`（`site 官网`）、`shortName`、`logo`、`phone`、`email`、`icp`、`copyright`、`keywords`、`description` |

## 4. 验证记录

### 4.1 构建

```
[INFO] Reactor Summary for site 3.9.1:
[INFO] site ............................................... SUCCESS
[INFO] ruoyi-common ....................................... SUCCESS
[INFO] ruoyi-system ....................................... SUCCESS
[INFO] ruoyi-framework .................................... SUCCESS
[INFO] ruoyi-quartz ....................................... SUCCESS
[INFO] ruoyi-generator .................................... SUCCESS
[INFO] ruoyi-cms .......................................... SUCCESS
[INFO] ruoyi-admin ........................................ SUCCESS
[INFO] BUILD SUCCESS  (产物 ruoyi-admin/target/site-server.jar)
```

### 4.2 启动

```
Started RuoYiApplication in 6.238 seconds (process running for 6.805)
```

### 4.3 接口冒烟（HTTP + 业务码）

| # | 请求 | 结果 |
|---|---|---|
| 1 | `GET /captchaImage` | `{"code":200,...,"captchaEnabled":true}`（返回 base64 图形） |
| 2 | `GET /api/site/config` | `{"code":200,"data":{"name":"site 官网","shortName":"site",...}}` |
| 3 | `GET /system/user/list`（无 token） | `{"code":401,"msg":"请求访问：/system/user/list，认证失败..."}` |
| 4 | `POST /common/upload`（无 token） | `{"code":401,...}` ← **demo 的匿名上传漏洞已消除** |
| 5 | `POST /login`（无验证码） | `{"code":500,"msg":"验证码已失效"}` |
| 6 | `POST /login`（临时关闭验证码） | `{"code":200,"token":"..."}` + 带 token 访问 `/system/user/list` 返回 `total=2` |

> 说明：第 6 项为验证登录链路，临时把 `sys.account.captchaEnabled` 置为 `false` 并清理 Redis 缓存；验证后已恢复 `true`，第 5 项即恢复后的结果。

## 5. 过程中修复/规避的问题

| 问题 | 处理 |
|---|---|
| 父工程未声明 `ruoyi-cms` 版本，Maven 报 `dependency.version is missing` | 在根 `pom.xml` 的 `dependencyManagement` 增加 cms 版本 |
| demo 的 `SecurityConfig` 硬编码 `/vatti/**`、`/system/product*` 匿名白名单 | 重置为官方最小白名单（登录/注册/验证码/静态资源/swagger/druid） |
| demo 给 `/common/upload(s)` 加了 `@Anonymous` | 移除注解与 import，恢复"上传需登录" |
| `ruoyi-framework` 多出的 `spring-boot-starter-mail` | 删除依赖（Day 1 不接邮件） |
| demo 明文 DB 密码/SMTP 授权码 | 新工程不使用：DB 密码走 `SITE_DB_PASSWORD`；未引入 mail 配置 |
| `bin/run.bat`、`package.bat` 为 GBK 且指向旧 jar | 重写为 ASCII + `site-server.jar` + 密码校验 |

## 6. 遗留与 Day 2 计划

| 项 | 状态 | Day 2 动作 |
|---|---|---|
| `site-admin`（后台前端） | 未创建 | 复制 RuoYi-Vue 3.9.1 的 `ruoyi-ui`（Vue2）并裁剪，dev 5174 |
| `site-web`（官网前台） | 未创建 | 新建 Vite 6 + Vue 3.5 + Vue Router 4 + Vuex 4 工程（或迁移 `vatti` 素材），dev 80 |
| `cms` 领域模型 | 仅骨架 | `CmsCategory` + `CmsProduct` domain/mapper/service + XML；`/api/site/category/tree` |
| 字典 | 未建 | `cms_category_type`、`cms_lead_type`、`cms_lead_status`、`cms_store_type`、`cms_banner_position`（`sql/05-cms-dict.sql`） |
| 菜单可见性 | 后台前端未就绪时点「内容管理」会 404 | 属预期，Day 2 前端完成后消失 |
| Git | 未安装 | `git init` 后首次提交（`bin` 脚本、SQL、源码；不含 `target/logs/uploadPath`） |
| 验证码/限流/OSS | 未接 | 按 `docs/02` 第 7 节在业务阶段实施 |
