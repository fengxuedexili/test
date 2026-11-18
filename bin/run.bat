@echo off
setlocal
cd /d %~dp0
cd ..\ruoyi-admin\target

if not exist site-server.jar (
  echo [ERROR] site-server.jar not found. Run bin\package.bat first.
  pause
  exit /b 1
)

if "%SITE_DB_PASSWORD%"=="" (
  echo [ERROR] Database password is not set.
  echo         PowerShell:  $env:SITE_DB_PASSWORD='your_password'
  echo         CMD:         set SITE_DB_PASSWORD=your_password
  pause
  exit /b 1
)

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
echo [INFO] Starting site-server.jar on http://localhost:8080 ...
java %JAVA_OPTS% -jar site-server.jar
pause
