# Day 6 · 图片链路修复与 CMS 功能核对（2026-11-18）

> 触发问题：后台把 `CXW-260-J6019HS` 的主图换成新上传图片后，前台首页没有变化。
> 结论：**数据已保存成功**，问题出在「上传文件存放位置 + Windows 路径分隔符 + 前台未代理 `/profile`」三处链路。

## 1. 根因（按影响排序）

| # | 根因 | 证据 |
|---|---|---|
| 1 | **上传文件存放在 `ruoyi-admin/target/uploadPath`**，而 `mvn clean package` 会删除 `target` | 配置为 `ruoyi.profile: ./uploadPath`，`bin/run.bat` 在 `target` 目录启动；构建后目录为空，DB 里的封面路径指向不存在的文件 |
| 2 | **Windows 下路径含反斜杠**：`/profile/upload/2026\11\18\xxx.jpg` | `FileUploadUtils.getPathFileName()` 取的是 Windows 路径片段；浏览器会把 `\` 视为非法/转义字符 |
| 3 | **前台/后台都没有代理 `/profile`**：`ImageUpload` 存的是相对路径 `/profile/...` | site-web 只在 80 端口代理 `/api`；未代理时返回 SPA 的 index.html（解码失败 → 走占位图），所以首页“看起来没变” |

补充两个容易误判的点：
- 卡片的取图优先级是 **主图 `cover` → 图集 `images` → 占位图**；只改「图集」时首页卡片不会变（现在已支持主图失败时自动回退图集）。
- 首页数据只在挂载时拉取；后台改完不刷新前台标签页也不会变（现已增加“切回标签页自动刷新”）。

## 2. 修复清单

| 层 | 修改 |
|---|---|
| 配置 | `application.yml`：`ruoyi.profile: ${SITE_UPLOAD_PATH:D:/VUE-WorkSpace/2027/site/uploadPath}`（绝对路径，`mvn clean` 不再影响） |
| 后端 | `CommonController`：上传返回的 `fileName/url` 统一把 `\` 转成 `/`（单个/多个上传都处理） |
| 前台 | `vite.config.js` 增加 `/profile` → 8080 代理；`utils/image.js` 新增 `resolveImageUrl()`（反斜杠归一 + `/profile` 按 `VITE_API_BASE` 拼域名）；`ProductCard` 改为 **cover → images → 占位图** 依次降级；`ProductDetail` 同样归一 |
| 前台 | `HomeView` 监听 `visibilitychange`，从后台切回前台时自动重拉 Banner/分类/推荐 |
| 后台 | `vue.config.js` 增加 `/profile` → 8080 代理（图片预览不再依赖 `/dev-api` 路径拼接） |
| 数据 | 新增 `sql/09-fix-upload-paths.sql`：把历史数据里的 `\` 批量改为 `/`（已执行） |
| 数据 | 本次构建前被清空的 2 个上传封面（产品 1、2）已重置为空，回退到图集/占位图 |

## 3. 验证记录

| # | 用例 | 结果 |
|---|---|---|
| 1 | 上传一张 PNG（`/common/upload`，带 token） | `code=200`，`fileName=/profile/upload/2026/11/18/probe_20261118222336A001.png`（正斜杠） |
| 2 | 文件落盘位置 | `site/uploadPath/upload/2026/11/18/...` **存在**（不再在 target） |
| 3 | `GET /profile/...`（8080 直连） | `200 image/png` |
| 4 | `GET /profile/...`（site-web 80 代理） | `200 image/png` |
| 5 | `GET /profile/...`（site-admin 5174 代理） | `200 image/png` |
| 6 | 页面 | `/`、`/booking`、`5174/` 均 `200` |
| 7 | 构建 | `site-web built in 1.06s`；后端 `BUILD SUCCESS` |

## 4. 需要你做一次操作

之前上传的图片存放在 `target` 下，已被构建清理，**无法恢复**。请在后台重新上传一次：

1. 打开 http://localhost:5174 → 内容管理 → 产品管理 → 修改 `CXW-260-J6019HS`
2. 重新选择「主图」，保存
3. 回到前台 http://localhost/（切回标签页会自动刷新）即可看到

> 之后图片会稳定保存在 `D:\VUE-WorkSpace\2027\site\uploadPath`，构建/重启都不会丢；生产环境用 `SITE_UPLOAD_PATH` 指向独立目录，或直接接 OSS。

## 5. CMS 功能核对（截至 Day 6）

| 模块 | 状态 | 说明 |
|---|---|---|
| 栏目 | ✅ 完成 | 树形结构、上级选择、slug 唯一、封面上传、SEO、启停、删除保护（有子栏目/产品时阻止） |
| 产品 | ✅ 完成 | 列表筛选/分页、主图、图集（多图上传）、卖点/参数/下载（可视化行编辑）、推荐位、发布时间、软删除、浏览量 |
| Banner | ✅ 完成 | 位置、图片/视频、文案/按钮/链接、排序、投放时间段、上下线；前台轮播（自动播放/箭头/圆点） |
| 线索 | ✅ 完成 | 前台表单（验证码/蜜罐/幂等/频控）+ 后台列表/详情/跟进/删除/Excel 导出 |
| 字典 | ✅ 完成 | 5 类 16 条，菜单与页面均绑定 |
| 站点参数 | ✅ 完成 | `site.*` 9 个键，前台页头页脚与 SEO 读取 |
| 菜单/权限 | ✅ 完成（仅 admin 验证） | `cms:*` 菜单 3000 段 + 按钮权限；角色细分（内容运营/线索专员）待建 |
| 上传 | ⚠️ 本地可用 | 本地 `uploadPath`；**未接 OSS/CDN**，无图片压缩/WebP 转换 |
| SEO | ⚠️ 部分 | 页面 title/meta/字段已做；**未做** `sitemap.xml`、`robots.txt`、JSON-LD、预渲染 |
| 文章 | ❌ 未实现 | 表已建，后台/前台均无（资讯 P1） |
| 单页 | ❌ 未实现 | 表已建；隐私政策属 P0，尚未落地 |
| 门店 | ❌ 未实现 | 表已建（P1） |
| 系列 | ❌ 未实现 | 表已建（P1，产品 `seriesId` 字段已预留） |
| 站内搜索 | ❌ 未实现 | 前台 `/search` 页面与接口 |
| 后台看板 | ⚠️ 默认页 | 仍是若依默认首页，未做“待处理线索/产品数/最新文章”卡片 |
| 定时任务 | — | Banner/发布靠 SQL 时间窗过滤，无需 Quartz |
| 多语言/多站点 | ❌ 未预留 | 二期 |

## 6. 结论与下一步

- **“图片不更新”是链路问题，不是 CMS 丢数据**：数据库保存成功、接口返回新路径，但文件被构建清理 + 路径分隔符 + 未代理导致前台取不到。
- P0 功能（栏目/产品/Banner/线索/预约）已齐全并验证；**P1 缺口集中在文章、单页（隐私政策）、门店、SEO 收口、OSS**。
- 建议顺序：**单页（隐私政策）→ 文章 → SEO（sitemap/robots/JSON-LD）→ 门店 → OSS/角色/看板**。
