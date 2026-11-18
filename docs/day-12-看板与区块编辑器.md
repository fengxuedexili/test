# Day 12 · 后台看板与单页区块可视化

> 目标：① 后台首页看板（待处理线索 / 产品 / 文章 / 门店 + 7 日线索趋势 + 最近线索）；② 把单页 `blocks` 从手写 JSON 换成可视化编辑器（富文本 + 左右图文）。

## 1. 范围

| 项 | 内容 |
|---|---|
| 看板后端 | `/cms/dashboard/summary`，聚合 6 项计数 + 最近 5 条线索 + 最新 5 篇文章 + 近 7 天线索趋势 |
| 看板前端 | `views/cms/dashboard/index.vue`：4 张统计卡、趋势柱状、最新文章、最近线索表；卡片可点进对应模块 |
| 菜单 | `15-dashboard-menu.sql`：`menu_id=3250`，`cms/dashboard/index`，权限 `cms:dashboard:view`，授权 `content_ops(100)`、`lead_sales(101)` |
| 单页区块 | `views/cms/page/index.vue` 改为可视化：正文区块（Quill）、图文区块（上传/标题/正文/左右互换）、上下移动/删除、`/p/{slug}` 预览 |
| 小重构 | 门店后台类型下拉改用字典 `cms_store_type`（与字典表统一，去掉硬编码） |

不做：图表库（ECharts）接入、看板自定义时间范围、区块更多类型（video/faq）。

## 2. 看板后端

新增文件：

```
domain/vo/CmsDashboardVo.java
mapper/CmsDashboardMapper.java
resources/mapper/cms/CmsDashboardMapper.xml
service/ICmsDashboardService.java
service/impl/CmsDashboardServiceImpl.java
controller/admin/CmsDashboardController.java
```

统计口径：

| 字段 | SQL 口径 |
|---|---|
| `productCount` | `cms_product where del_flag='0'` |
| `articleCount` | `cms_article where del_flag='0' and status='0'` |
| `pendingLeadCount` | `cms_lead where status='0'` |
| `todayLeadCount` | `cms_lead where create_time >= curdate()` |
| `storeCount` | `cms_store where del_flag='0' and status='0'` |
| `pageCount` | `cms_page where status='0'` |
| `recentLeads` | 按 `lead_id desc` 取 5 条（姓名/手机/类型/地区/状态/时间） |
| `latestArticles` | `del_flag='0' and status='0'`，`is_top desc, publish_time desc` 取 5 条 |
| `leadTrend` | 近 7 天按 `date_format(create_time,'%m-%d')` 分组计数 |

实现要点：

- MyBatis 返回 `HashMap` 的 key 大小写不稳定，Service 内按 `entry.getKey().toLowerCase()` 匹配 `d`/`c`，避免取不到值。
- 趋势在 Java 侧补齐 7 天（含 0），前端柱状图不会出现「只有有数据的日期」。
- 接口受 `@PreAuthorize("@ss.hasPermi('cms:dashboard:view')")` 保护；两类业务角色均已授权。
- RuoYi 认证失败时返回 **HTTP 200 + `{"code":401}`**（框架约定），验证脚本需看 body 的 `code`。

## 3. 看板前端

| 区域 | 说明 |
|---|---|
| 统计卡 | 待处理线索（红）、产品总数（蓝）、已发布文章（绿）、启用门店（橙）；底部补充「今日新增 / 启用单页」，点击跳转对应列表 |
| 近 7 天线索 | 纯 CSS 柱状图，高度按 `count / max * 100%`，最小 4%，避免 0 值不可见；显示总数 |
| 最新文章 | 标题（置顶红点）+ 发布时间，点击进入文章管理 |
| 最近线索 | `dict-tag` 渲染 `cms_lead_type` / `cms_lead_status`，底部「进入线索管理」 |

## 4. 单页区块可视化

区块数据结构（与前台 `PageView.vue` 完全一致）：

```json
[
  { "type": "richText",  "props": { "html": "<h2>标题</h2><p>正文</p>" } },
  { "type": "imageText", "props": { "image": "/profile/upload/...", "title": "标题", "text": "描述", "reverse": false } }
]
```

编辑器行为：

- 「正文区块」使用全局注册的 `<editor>`（Quill 2，自带图片上传到 `/common/upload`）。
- 「图文区块」使用 `<image-upload>` + 标题/正文 + `el-switch` 左右互换。
- 每块可上移/下移/删除；列表页新增「区块」列显示区块数量。
- 保存前逐块校验：正文区块去掉标签后不能为空；图文区块图片/标题/正文至少一项。为空时提示「第 N 个区块内容为空」并阻止提交。
- 序列化前克隆对象并删除内部 `_key`（Vue 渲染用），不污染入库 JSON；未知类型的区块原样保留，仅提示不可视化编辑。
- 打开旧数据时解析失败给出警告并按空处理，不影响其它字段保存。
- 服务端 `CmsPageServiceImpl.sanitizeBlocks()` 仍对 `richText.props.html` 做正则清洗（script/iframe/事件属性/javascript:），前端编辑器不是安全边界。

## 5. 验证记录（2026-11-19）

| # | 检查项 | 结果 |
|---|---|---|
| 1 | `mvn -B -q -DskipTests compile` | COMPILE OK |
| 2 | `site-admin npm run build:prod` | Build complete（无编译错误） |
| 3 | 导入 `15-dashboard-menu.sql` | `sys_menu=1` 条（3250）；`sys_role_menu` 授权 100、101 |
| 4 | `GET /cms/dashboard/summary`（admin） | pending=1、products=5、articles=2、stores=5、pages=2；recentLeads=2、latestArticles=2、trend=7 天 |
| 5 | 趋势数据 | `11-13:0 … 11-18:2、11-19:0`（补齐 7 天，含 0） |
| 6 | `content` / `sales` 登录后 `getRouters` | 均包含 `cms/dashboard/index`，summary 均返回 200 |
| 7 | 单页 blocks 写入（含 `<script>` / `onerror` / `onmouseover`） | 保存 200；前台返回 `<h2>区块测试</h2><p>正文保留</p><img src=x>`，危险内容被清除 |
| 8 | blocks 还原 | 还原后 `blocks` 长度 250 与原值完全一致 |
| 9 | 验证码恢复 | `sys.account.captchaEnabled=true`，重启后未带验证码登录返回 `验证码已失效` |
| 10 | 未登录访问看板 | body `{"code":401,"msg":"…认证失败…"}`（HTTP 200 为 RuoYi 包装约定） |
| 11 | 回归 | `/api/site/store` 仍 5 家；`/api/site/page/about` 仍 2 个区块 |

## 6. 踩坑记录

- 看板趋势 SQL 直接用 `resultType="HashMap"` 时列名大小写不稳定 → Service 统一小写匹配。
- Element UI 的 `<el-form-item>` 内再嵌 `<el-form-item>` 会继承 `label-width` 导致错位 → 区块内部字段改用 `.field-row` + `<label>` 布局。
- Quill 组件在 `v-for` 中必须给稳定 `key`（本次用 `_key`），否则上下移动后内容可能错位；保存时要剔除 `_key`。
- 认证失败不会抛 HTTP 401（RuoYi 统一 200 + code 401），脚本判断要看 body。

## 7. 下一步

- 看板：追加「近 30 天线索来源/类型分布」「内容待办（草稿/停用）」，如需图表再引入 ECharts。
- 区块：新增 `video`（视频）与 `faq`（问答）类型，前台同步渲染。
- 上线前：删除/改密演示账号、关闭验证码临时改动已恢复、配置 OSS 与 Nginx。
