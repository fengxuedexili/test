<#
.SYNOPSIS
  site 官网冒烟测试：前台接口/页面 + 后台登录与核心接口 + 未授权校验。

.DESCRIPTION
  默认跑本地环境。验证码开启时可先运行一次生成验证码图片，再带 -CaptchaCode 重跑后台部分。
  日志写入 site/logs/smoke-*.log。

.EXAMPLE
  .\bin\smoke.ps1 -SkipAdmin
.EXAMPLE
  .\bin\smoke.ps1 -CaptchaCode 12
.EXAMPLE
  .\bin\smoke.ps1 -BaseUrl http://127.0.0.1:8080 -SiteUrl http://www.example.com
#>
param(
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$SiteUrl = 'http://localhost',
  [string]$Username = 'admin',
  [string]$Password = 'admin123',
  [string]$CaptchaCode = '',
  [switch]$SkipAdmin,
  [switch]$SkipWeb
)

$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

$script:pass = 0
$script:fail = 0
$script:warn = 0
$script:needCaptcha = $false

$root = Split-Path -Parent $PSScriptRoot
$logDir = Join-Path $root 'logs'
if (!(Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }
$logFile = Join-Path $logDir ('smoke-' + (Get-Date -Format 'yyyyMMdd-HHmmss-fff') + '.log')

function Write-Log([string]$text, [string]$level = 'INFO') {
  $line = '{0} [{1}] {2}' -f (Get-Date -Format 'HH:mm:ss'), $level, $text
  Write-Host $line
  Add-Content -LiteralPath $logFile -Value $line -Encoding UTF8
}

function Invoke-RawGet([string]$url, [hashtable]$headers = @{}) {
  $resp = Invoke-WebRequest -Uri $url -Headers $headers -UseBasicParsing -TimeoutSec 10
  $text = [Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
  return @{ Status = [int]$resp.StatusCode; Text = $text }
}

function Invoke-JsonGet([string]$url, [hashtable]$headers = @{}) {
  $raw = Invoke-RawGet $url $headers
  $json = $null
  try { $json = $raw.Text | ConvertFrom-Json } catch { }
  return @{ Status = $raw.Status; Text = $raw.Text; Json = $json }
}

function Test-Api([string]$name, [string]$url, [hashtable]$headers = @{}) {
  try {
    $r = Invoke-JsonGet $url $headers
    if ($r.Json -and $r.Json.code -eq 200) {
      $script:pass++
      Write-Log "PASS  $name"
    } else {
      $script:fail++
      $body = $r.Text
      if ($body.Length -gt 160) { $body = $body.Substring(0, 160) }
      Write-Log "FAIL  $name -> HTTP $($r.Status) body $body" 'ERROR'
    }
  } catch {
    $script:fail++
    Write-Log "FAIL  $name -> $($_.Exception.Message)" 'ERROR'
  }
}

function Test-Text([string]$name, [string]$url, [string]$expect) {
  try {
    $r = Invoke-RawGet $url
    if ($r.Text -like "*$expect*") {
      $script:pass++
      Write-Log "PASS  $name"
    } else {
      $script:fail++
      Write-Log "FAIL  $name -> 未包含 '$expect'" 'ERROR'
    }
  } catch {
    $script:fail++
    Write-Log "FAIL  $name -> $($_.Exception.Message)" 'ERROR'
  }
}

function Test-Page([string]$name, [string]$url) {
  try {
    $resp = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
    if ([int]$resp.StatusCode -eq 200) {
      $script:pass++
      Write-Log "PASS  $name ($url)"
    } else {
      $script:warn++
      Write-Log "WARN  $name -> HTTP $($resp.StatusCode)" 'WARN'
    }
  } catch {
    $script:warn++
    Write-Log "WARN  $name -> $($_.Exception.Message)" 'WARN'
  }
}

function Test-Unauthorized([string]$name, [string]$url) {
  try {
    $r = Invoke-JsonGet $url
    if ($r.Json -and $r.Json.code -eq 401) {
      $script:pass++
      Write-Log "PASS  $name (code=401)"
    } else {
      $script:fail++
      Write-Log "FAIL  $name -> 期望 code=401，实际 $($r.Text)" 'ERROR'
    }
  } catch {
    $script:fail++
    Write-Log "FAIL  $name -> $($_.Exception.Message)" 'ERROR'
  }
}

Write-Log '================ site smoke start ================'
Write-Log "BaseUrl=$BaseUrl  SiteUrl=$SiteUrl  SkipAdmin=$SkipAdmin  SkipWeb=$SkipWeb"

# ---------- 前台接口 ----------
Test-Api 'site config'              "$BaseUrl/api/site/config"
Test-Api 'category tree'            "$BaseUrl/api/site/category/tree?type=1"
Test-Api 'product page'             "$BaseUrl/api/site/product/page?pageNum=1&pageSize=1"
Test-Api 'product recommend'        "$BaseUrl/api/site/product/recommend?limit=1"
Test-Api 'banner home'              "$BaseUrl/api/site/banner?position=home"
Test-Api 'article page'             "$BaseUrl/api/site/article/page?pageNum=1&pageSize=1"
Test-Api 'page privacy'             "$BaseUrl/api/site/page/privacy"
Test-Api 'store list'               "$BaseUrl/api/site/store"
Test-Api 'store provinces'          "$BaseUrl/api/site/store/provinces"
Test-Text 'sitemap.xml'             "$BaseUrl/api/site/sitemap.xml" '<urlset'
Test-Text 'robots.txt'              "$BaseUrl/api/site/robots.txt" 'Sitemap:'

# ---------- 前台页面 ----------
if (-not $SkipWeb) {
  Test-Page 'web home'      "$SiteUrl/"
  Test-Page 'web product'   "$SiteUrl/product"
  Test-Page 'web news'      "$SiteUrl/news"
  Test-Page 'web store'     "$SiteUrl/store"
  Test-Page 'web booking'   "$SiteUrl/booking"
  Test-Page 'web about'     "$SiteUrl/p/about"
}

# ---------- 后台 ----------
if (-not $SkipAdmin) {
  $captcha = $null
  try { $captcha = Invoke-JsonGet "$BaseUrl/captchaImage" } catch { }

  $uuid = ''
  $captchaEnabled = $false
  if ($captcha -and $captcha.Json) {
    if ($captcha.Json.uuid) { $uuid = [string]$captcha.Json.uuid }
    if ($captcha.Json.img) {
      $captchaEnabled = $true
      try {
        $png = Join-Path $logDir 'smoke-captcha.png'
        [IO.File]::WriteAllBytes($png, [Convert]::FromBase64String([string]$captcha.Json.img))
        Write-Log "验证码图片已保存：$png"
      } catch {
        Write-Log "验证码图片保存失败：$($_.Exception.Message)" 'WARN'
      }
    }
  }

  if ($captchaEnabled -and [string]::IsNullOrWhiteSpace($CaptchaCode)) {
    $script:needCaptcha = $true
    $script:warn++
    Write-Log 'WARN  验证码开启且未提供 -CaptchaCode，跳过后台检查（图片见 logs\smoke-captcha.png）' 'WARN'
  } else {
    $login = $null
    try {
      $body = @{ username = $Username; password = $Password; code = $CaptchaCode; uuid = $uuid } | ConvertTo-Json -Compress
      $bytes = [Text.Encoding]::UTF8.GetBytes($body)
      $resp = Invoke-WebRequest -Method Post -Uri "$BaseUrl/login" -ContentType 'application/json; charset=utf-8' -Body $bytes -UseBasicParsing -TimeoutSec 10
      $loginText = [Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
      $login = $loginText | ConvertFrom-Json
    } catch {
      Write-Log "登录请求失败：$($_.Exception.Message)" 'ERROR'
    }

    if ($login -and $login.code -eq 200 -and $login.token) {
      $script:pass++
      Write-Log "PASS  后台登录 ($Username)"
      $h = @{ Authorization = 'Bearer ' + $login.token }

      Test-Api 'getInfo'                "$BaseUrl/getInfo" $h
      Test-Api 'dashboard summary'      "$BaseUrl/cms/dashboard/summary" $h
      Test-Api 'admin category list'    "$BaseUrl/cms/category/list" $h
      Test-Api 'admin series list'      "$BaseUrl/cms/series/list" $h
      Test-Api 'admin product list'     "$BaseUrl/cms/product/list?pageNum=1&pageSize=1" $h
      Test-Api 'admin banner list'      "$BaseUrl/cms/banner/list?pageNum=1&pageSize=1" $h
      Test-Api 'admin article list'     "$BaseUrl/cms/article/list?pageNum=1&pageSize=1" $h
      Test-Api 'admin page list'        "$BaseUrl/cms/page/list" $h
      Test-Api 'admin store list'       "$BaseUrl/cms/store/list?pageNum=1&pageSize=1" $h
      Test-Api 'admin lead list'        "$BaseUrl/cms/lead/list?pageNum=1&pageSize=1" $h

      Test-Unauthorized '未授权访问线索' "$BaseUrl/cms/lead/list?pageNum=1&pageSize=1"
    } else {
      $script:fail++
      $msg = ''
      if ($login) { $msg = "code=$($login.code) msg=$($login.msg)" }
      Write-Log "FAIL  后台登录 $msg" 'ERROR'
    }
  }
}

Write-Log ('================ 结果：PASS {0} / FAIL {1} / WARN {2} ================' -f $script:pass, $script:fail, $script:warn)
Write-Log "日志：$logFile"

if ($script:fail -gt 0) { exit 1 }
if ($script:needCaptcha) { exit 2 }
exit 0

