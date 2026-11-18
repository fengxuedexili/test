@echo off
setlocal
cd /d %~dp0
cd ..

echo [INFO] Building site-server (skip tests) ...
call mvn clean package -DskipTests -pl ruoyi-admin -am
if errorlevel 1 (
  echo [ERROR] Build failed.
  pause
  exit /b 1
)

echo [OK] Output: ruoyi-admin\target\site-server.jar
pause
