# Day 10 · 上线收口（安全与权限）+ 首页资讯（2026-11-18）

> 方案 B 第 10 天：富文本服务端清洗、角色权限与越权自查、首页「最新资讯」楼层。
> 验收：危险 HTML 入库前后被清洗；内容运营/线索专员权限隔离；首页展示资讯卡片。

## 1. 交付物

### 1.1 富文本服务端清洗（CmsHtmlSanitizer）
| 项 | 说明 |
|---|---|
| 位置 | `ruoyi-cms/.../util/CmsHtmlSanitizer.java` |
| 策略 | 移除 `script/style/iframe/object/embed/form/input/button/textarea/select/link/meta`；移除 `on*` 事件属性；替换 `javascript:`/`vbscript:` 协议；屏蔽 CSS `expression()` |
| 生效点 | 文章新增/修改（`content`）；单页新增/修改（`blocks[].props.html`） |
| 说明 | 本地 Maven 仓库没有 Jsoup 且离线无法下载，先用正则白名单兜底（非完整解析器）；前端 DOMPurify 仍是主防线，后续可平滑替换为 Jsoup |

### 1.2 角色与权限（`sql/13-roles-demo.sql`）
| 角色 | role_key | 菜单范围 |
|---|---|---|
| 内容运营 | `content_ops`（role_id=100） | 内容管理 + 栏目/系列/产品/Banner/文章/单页/门店（41 条菜单/按钮），**无线索、无系统管理** |
| 线索专员 | `lead_sales`（role_id=101） | 内容管理 + 线索（列表/查询/修改/删除/导出，6 条），**无产品/文章等** |

演示账号：`content / admin123`、`sales / admin123`（密码复用 admin 的 bcrypt，仅开发环境）。

### 1.3 首页「最新资讯」楼层
- `HomeView` 并行拉取 `getArticlePage({ pageSize: 3 })`
- 3 列卡片：日期 / 标题 / 摘要（两行截断），点击进入 `/news/{id}`；移动端单列

## 2. 验证记录

### 2.1 富文本清洗
| # | 用例 | 结果 |
|---|---|---|
| 1 | 新增文章，正文含 `<script>` 与 `onerror` | 保存成功；前台详情返回 `<h2>标题</h2><p>正常</p><img src="/x.jpg">`，**script/onerror 已被移除** |
| 2 | 新增单页，`blocks[0].props.html` 含 `<script>` | 前台 `/api/site/page/test-xss` 返回 `<p>ok</p>`，script 已移除 |
| 3 | 测试数据清理 | 测试文章/单页已删除（库中保持 2 文章 / 2 单页） |

### 2.2 权限与越权
| 账号 | 请求 | 期望 | 实际 |
|---|---|---|---|
| content | `GET /cms/product/list` | 200 | **200** |
| content | `GET /cms/lead/list` | 403 | **403** |
| content | `GET /system/user/list` | 403 | **403** |
| sales | `GET /cms/lead/list` | 200 | **200** |
| sales | `GET /cms/product/list` | 403 | **403** |
| sales | `PUT /cms/product` | 403 | **403** |

### 2.3 首页资讯与页面
| # | 用例 | 结果 |
|---|---|---|
| 1 | `GET /api/site/article/page?pageSize=3` | 200 |
| 2 | 前台 `/`、`/news`、`/news/1`、`/p/privacy` | 全部 200 |
| 3 | 后台 `5174/` | 200 |
| 4 | `site-web` 构建 | `built in 1.13s` |

## 3. 关键决策

| # | 决策 | 原因 |
|---|---|---|
| 1 | 清洗器用正则而非 Jsoup | 离线环境无法下载 Jsoup；先保证"script/事件属性/javascript: 协议"三类高危被拦截 |
| 2 | 单页只清洗 `richText.props.html` | `imageText` 的文本字段是纯文本，前台按文本渲染 |
| 3 | 角色菜单按"最小权限"分配 | 内容运营不碰线索与系统；线索专员不碰内容编辑 |
| 4 | 演示账号密码复用 admin 的 bcrypt | 避免引入明文密码；生产必须改密或禁用演示账号 |

## 4. 遗留与 Day 11 计划

| 项 | 状态 | Day 11 |
|---|---|---|
| Jsoup 正式版 | 正则是兜底 | 有网络后替换为 Jsoup 白名单 + 单元测试 |
| 门店 / 系列 | 未实现 | 最后两个内容模块 |
| blocks 可视化编辑 | JSON 文本域 | 区块表单化 |
| OSS 接入 | 未做 | 需 AK/SK 与 Bucket |
| 部署 / Git | 未做 | 需服务器与 Git 安装 |
| 后台看板 | 未做 | 线索/产品/文章统计卡片 |
| 预渲染 | 未做 | `vite-ssg` 兼容性 spike |
| 演示账号 | 已建 | 上线前禁用或改密 |
