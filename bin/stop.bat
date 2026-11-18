@echo off
echo.
echo [��Ϣ] ��������target����·����
echo.


cd %~dp0

taskkill /F /IM java.exe

taskkill /F /IM node.exe

pause