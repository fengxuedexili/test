@echo off
echo.
echo [��Ϣ] ��������target����·����
echo.



cd bin
cd %~dp0
mvn clean package --define maven.test.skip=true --define maven.clean.skip=true



pause