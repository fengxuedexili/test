# 部署手册（Linux + Nginx，Windows 见文末）

> 适用：Day 13 起的生产部署。示例路径统一为 `/data/site`，域名 `www.example.com` / `admin.example.com`，按实际替换。

## 1. 产物与目录

| 产物 | 来源 | 服务器位置 |
|---|---|---|
| 后端 jar | `mvn -DskipTests package` → `ruoyi-admin/target/site-server.jar` | `/data/site/site-server.jar` |
| 官网前台 | `site-web` → `npm run build` → `dist/` | `/data/www/site-web/dist` |
| 管理后台 | `site-admin` → `npm run build:prod` → `dist/` | `/data/www/site-admin/dist` |
| 上传文件 | 运行时 | `/data/site/uploadPath`（Nginx 反代 `/profile/`） |
| 日志 | 运行时 | `/data/site/logs` |

```bash
sudo mkdir -p /data/site/logs /data/site/uploadPath /data/www/site-web /data/www/site-admin
sudo chown -R www-data:www-data /data/site /data/www
```

## 2. 环境变量（必读）

```bash
sudo mkdir -p /etc/site
sudo cp deploy/env.example /etc/site/site.env
sudo chmod 600 /etc/site/site.env
sudo vi /etc/site/site.env      # 改数据库、JWT、域名
```

关键项：

| 变量 | 说明 |
|---|---|
| `SPRING_PROFILES_ACTIVE=druid,prod` | **顺序必须是 druid,prod**（prod 覆盖 druid 的本机地址与监控台开关） |
| `SITE_TOKEN_SECRET` | 必填；缺失会导致启动失败（fail-fast） |
| `SITE_DB_*` | 生产库连接（建议独立 `site` 账号，仅授权本库） |
| `SITE_UPLOAD_PATH` / `SITE_LOG_PATH` | 独立可写目录，便于备份 |
| `SITE_ALLOWED_DOMAINS` / `SITE_CORS_ORIGINS` | 防盗链与 CORS 白名单 |

## 3. 数据库初始化

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS site DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
cd sql
for f in 01-*.sql 02-*.sql 03-*.sql 04-*.sql 05-*.sql 10-*.sql 12-*.sql 15-*.sql; do
  mysql -uroot -p site < "$f"
done
# 06/07/08/11/13/14 为演示数据，生产禁止执行
```

生产执行 `deploy/security-hardening.sql`：禁用 `content`/`sales` 演示账号、确认验证码开启、核对 `site.baseUrl`。

> 上线前把 `site.baseUrl` 改为正式域名（sitemap/robots/分享链接使用）。

## 4. 启动后端（systemd）

```bash
sudo cp deploy/systemd/site-server.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now site-server
sudo systemctl status site-server --no-pager
sudo journalctl -u site-server -f
```

Windows 可用：

```powershell
.\deploy\env.example.ps1    # 修改后 dot-source
java -Xms512m -Xmx1024m -jar .\ruoyi-admin\target\site-server.jar
```

## 5. Nginx

```bash
sudo cp deploy/nginx/site.conf /etc/nginx/conf.d/site.conf
sudo vi /etc/nginx/conf.d/site.conf   # 域名、目录、证书
sudo nginx -t && sudo systemctl reload nginx
```

- 前台：`/` 静态 + history 回退；`/api/` 与 `/profile/` 反代后端。
- 后台：`admin.example.com`，`/prod-api/` 反代（对应 `VUE_APP_BASE_API`）。
- 后端已关闭 Swagger 与 Druid 控制台，Nginx 再兜底 404。

## 6. 验证

```powershell
# 冒烟（前台匿名部分）
.\bin\smoke.ps1 -BaseUrl http://127.0.0.1:8080 -SiteUrl http://www.example.com -SkipAdmin

# 冒烟（含后台；验证码开启时先看图再带 code）
.\bin\smoke.ps1 -BaseUrl http://127.0.0.1:8080 -SiteUrl http://www.example.com
```

另外人工确认：后台能登录、图片可上传/预览、线索提交与导出、sitemap/robots 为正式域名。

## 7. 发布与回滚

**发布**：保留上一版 `site-server.jar` 与两个 `dist`：

```bash
sudo cp site-server.jar site-server.jar.bak.$(date +%Y%m%d%H%M)
sudo systemctl restart site-server
```

**回滚**：

```bash
sudo cp site-server.jar.bak.YYYYMMDDHHMM site-server.jar
sudo systemctl restart site-server
# 前端：把上一版 dist 目录换回（建议按版本号保留 dist-<tag>）
```

数据库变更必须先在备份库演练：`mysqldump --single-transaction site > site-$(date +%F).sql`。

## 8. 待接入（外部资源）

- OSS：配置 AK/SK/Bucket/自定义域名后，上传改为 OSS 并迁移 `uploadPath` 存量文件；Nginx `/profile/` 改为 CDN。
- HTTPS：证书就绪后启用 `deploy/nginx/site.conf` 中的 443 段并开启 HSTS。
- 地图：高德/腾讯 Key 用于门店地图组件。
