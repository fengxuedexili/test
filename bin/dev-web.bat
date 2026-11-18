@echo off
rem 启动官网前台开发服务器（端口 80）
cd /d %~dp0..
cd site-web
npm run dev > web.log 2>&1
