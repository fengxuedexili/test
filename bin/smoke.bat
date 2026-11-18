@echo off
rem 一键冒烟：bin\smoke.bat [-SkipAdmin] [-SkipWeb] [-CaptchaCode 12] [-BaseUrl ...] [-SiteUrl ...]
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0smoke.ps1" %*
