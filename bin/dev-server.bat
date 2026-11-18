@echo off
rem 启动后端（端口 8080），用法：bin\dev-server.bat 数据库密码
if "%~1"=="" (
  echo usage: bin\dev-server.bat ^<db_password^>
  pause
  exit /b 1
)
cd /d %~dp0..
set "SITE_DB_PASSWORD=%~1"
if not exist "%~dp0..\logs" mkdir "%~dp0..\logs"
cd ruoyi-admin\target
java -Xms256m -Xmx1024m -jar site-server.jar > "%~dp0..\logs\backend.log" 2>&1
