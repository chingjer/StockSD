@echo off

rem 將csv更新到資料庫
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

:import
%javaexe% StockCsv %basedir% 
if not errorlevel 0 goto err
@echo *** dailyImp.cmd ok! ************************************* 

rem copy %basedir%*.csv %completedir%
rem del %basedir%*.csv 

goto eoj
:err
@echo !!!!! dailyImp.cmd 發生錯誤, 作業沒有完成 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

:eoj
@echo on
pause