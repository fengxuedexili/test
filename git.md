# Git 仓库状态与推送指南

## 0. 当前状态（截至 2026-11-19）

| 项 | 值 |
|---|---|
| 仓库位置 | `D:\VUE-WorkSpace\2027\site`（本地） |
| 远端 | **origin** → `https://github.com/fengxuedexili/test.git` |
| 分支 | `main`（跟踪 `origin/main`） |
| 本地/远端 | 已同步（`git log -1`、`git status` 核对最新状态） |
| 关键提交 | `1af3392` 初始化工程（tag `v0.1.0`）；`6f8970d`、`fde2ee2` 等文档提交 |
| 标签 | `v0.1.0`（已推送，指向 `1af3392`） |
| 工作区 | clean |
| 跟踪文件 | 793 个（无 `target/dist/node_modules/logs/uploadPath`） |
| 本仓库代理 | `http.proxy` / `https.proxy` = `http://127.0.0.1:7897`（跟随系统 IE 代理） |
| 凭据 | Windows 凭据管理器已保存 GitHub HTTPS 凭据（用户 `fengxuedexili`）；仓库内无 Token/密码 |

> 状态：**已推送成功**。仓库页面：https://github.com/fengxuedexili/test

### 本次实际执行的指令（按顺序）

```powershell
cd D:\VUE-WorkSpace\2027\site
```

```powershell
git remote add origin https://github.com/fengxuedexili/test.git
```

```powershell
git config http.proxy http://127.0.0.1:7897
```

```powershell
git config https.proxy http://127.0.0.1:7897
```

```powershell
git ls-remote --heads origin
```

```powershell
git push -u origin main
```

```powershell
git push origin --tags
```

```powershell
git ls-remote --heads origin
```

```powershell
git ls-remote --tags origin
```


## 1. 推送前检查（先跑这几条）

```powershell
cd D:\VUE-WorkSpace\2027\site
```

```powershell
git status
```

```powershell
git log --oneline --decorate -3
```

```powershell
git remote -v
```

```powershell
git tag -l
```

确认没有跟踪敏感文件：

```powershell
git ls-files | Select-String -Pattern 'application-local|\.env$|\.pem$|\.key$|\.p12$|\.dbpass'
```

```powershell
git grep -n -I "CHANGE_ME" -- deploy
```

如果上面两条有输出：不要提交真实密码/密钥，先改回占位符或加入 `.gitignore`。

## 2. 选择远端

| 方案 | 适用 | 远端地址示例 |
|---|---|---|
| A. GitHub | 公网、开源或私有仓库 | `git@github.com:<USER>/<REPO>.git` |
| B. Gitee | 国内访问快 | `git@gitee.com:<USER>/<REPO>.git` |
| C. 自建 GitLab | 公司内网 | `ssh://git@git.example.com:2222/group/site.git` |
| D. 服务器裸仓库 | 只有服务器、无 Git 平台 | `ssh://git@<SERVER_IP>/srv/git/site.git` |

先在对应平台**新建空仓库**（不要勾选“初始化 README/.gitignore/LICENSE”，避免与本地历史冲突）。

## 3. 方案 A：GitHub

### 3.1 SSH（推荐）

生成密钥（已有可跳过）：

```powershell
ssh-keygen -t ed25519 -C "you@example.com"
```

查看公钥并添加到 GitHub → Settings → SSH and GPG keys：

```powershell
Get-Content $env:USERPROFILE\.ssh\id_ed25519.pub
```

测试连通性：

```powershell
ssh -T git@github.com
```

添加远端：

```powershell
git remote add origin git@github.com:<USER>/<REPO>.git
```

确认远端：

```powershell
git remote -v
```

推送主分支：

```powershell
git push -u origin main
```

推送标签：

```powershell
git push origin --tags
```

### 3.2 HTTPS（使用 Personal Access Token）

```powershell
git remote add origin https://github.com/<USER>/<REPO>.git
```

```powershell
git push -u origin main
```

```powershell
git push origin --tags
```

> 密码处粘贴 PAT（GitHub 已不支持账号密码推送）；Windows 可用“凭据管理器”保存。

## 4. 方案 B：Gitee

```powershell
git remote add origin git@gitee.com:<USER>/<REPO>.git
```

```powershell
git push -u origin main
```

```powershell
git push origin --tags
```

HTTPS 形式：

```powershell
git remote set-url origin https://gitee.com/<USER>/<REPO>.git
```

## 5. 方案 C：自建 GitLab

```powershell
git remote add origin ssh://git@git.example.com:2222/group/site.git
```

```powershell
git push -u origin main
```

```powershell
git push origin --tags
```

## 6. 方案 D：服务器裸仓库（无平台）

服务器上执行：

```bash
sudo mkdir -p /srv/git/site.git
sudo git init --bare /srv/git/site.git
sudo chown -R git:git /srv/git/site.git
```

本机执行：

```powershell
git remote add origin ssh://git@<SERVER_IP>/srv/git/site.git
```

```powershell
git push -u origin main
```

```powershell
git push origin --tags
```

## 7. 推送后验证

```powershell
git remote -v
```

```powershell
git branch -vv
```

```powershell
git ls-remote --heads origin
```

```powershell
git ls-remote --tags origin
```

网页端应能看到：`main` 分支、2 个提交、`v0.1.0` 标签、793 个文件。

## 8. 常见问题

**1) 远端已有 README，push 被拒（non-fast-forward）**

```powershell
git pull origin main --allow-unrelated-histories
```

```powershell
git push -u origin main
```

确认远端内容可被覆盖时才使用强推（有风险）：

```powershell
git push -u origin main --force-with-lease
```

**2) 远端地址写错 / 需要切换**

```powershell
git remote set-url origin <新的远端地址>
```

```powershell
git remote -v
```

**3) 认证失败（SSH）**

```powershell
ssh -T git@github.com
```

```powershell
ssh-add $env:USERPROFILE\.ssh\id_ed25519
```

**4) 行尾警告 LF/CRLF**

仓库已包含 `.gitattributes`（`*.ps1/*.bat` 用 CRLF，`*.sh` 用 LF）。如仍有提示：

```powershell
git config core.autocrlf true
```

**5) 文件过大被拒（GitHub 单文件 100MB）**

先查最大的 5 个跟踪文件：

```powershell
git ls-files | ForEach-Object { Get-Item -LiteralPath $_ } | Sort-Object Length -Descending | Select-Object -First 5 Name,Length
```

确认 `target/`、`dist/`、`uploadPath/`、`*.jar` 未被跟踪（本项目已忽略）；若历史中误入大文件，需用 `git filter-repo` 清理后重写历史（务必先备份）。

**6) 误提交了密码/密钥**

1) 立即在平台上作废并更换该密码/密钥；2) 用 `git filter-repo` 或 BFG 从历史移除；3) 重新推送并通知协作者重新克隆。

**7) 代理导致 push / ls-remote 失败（Connection was reset）**

查看当前代理：

```powershell
git config --get http.proxy
```

设置代理（本仓库已按系统 IE 代理配置）：

```powershell
git config http.proxy http://127.0.0.1:7897
```

```powershell
git config https.proxy http://127.0.0.1:7897
```

关闭代理或换网络后，删除本仓库配置：

```powershell
git config --unset http.proxy
```

```powershell
git config --unset https.proxy
```

> 判断方法：浏览器能打开 GitHub，但 `git ls-remote` 报 `Recv failure: Connection was reset`，通常是 Git 未走系统代理。

## 9. 日常提交流程

```powershell
git status
```

```powershell
git add -A
```

```powershell
git commit -m "feat: 说明本次改动"
```

```powershell
git push
```

发布一个版本标签：

```powershell
git tag -a v0.2.0 -m "site v0.2.0"
```

```powershell
git push origin v0.2.0
```

## 10. 建议约定

- `main` 保持可发布；功能在 `feature/xxx` 分支开发，合并前跑 `bin\smoke.ps1`。
- 每次上线打 `vX.Y.Z` 标签，部署脚本按标签保留上一版 `jar` 与 `dist`，便于回滚。
- 密钥只放服务器环境变量或 `/etc/site/site.env`；`application-local.yml`、`.env`、`*.pem`、`*.key` 永不入库。
