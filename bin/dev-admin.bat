@echo off
rem 启动管理后台开发服务器（端口 5174）
cd /d %~dp0..
cd site-admin
npm run dev > admin.log 2>&1
