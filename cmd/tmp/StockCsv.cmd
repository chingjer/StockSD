@echo off

rem �Ncsv��s���Ʈw
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/�Ѳ�/�Ѳ��{��/import/"
set completedir="C:/stock/�Ѳ�/�Ѳ��{��/complete/"
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

:import
%javaexe% StockCsv %basedir% 
if not errorlevel 0 goto err
@echo *** dailyImp.cmd ok! ************************************* 

rem copy %basedir%*.csv %completedir%
rem del %basedir%*.csv 

goto eoj
:err
@echo !!!!! dailyImp.cmd �o�Ϳ��~, �@�~�S������ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

:eoj
@echo on
pause