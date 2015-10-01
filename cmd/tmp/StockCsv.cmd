@echo off

rem 盢csv穝戈畐
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/布/布祘Α/import/"
set completedir="C:/stock/布/布祘Α/complete/"
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

:import
%javaexe% StockCsv %basedir% 
if not errorlevel 0 goto err
@echo *** dailyImp.cmd ok! ************************************* 

rem copy %basedir%*.csv %completedir%
rem del %basedir%*.csv 

goto eoj
:err
@echo !!!!! dailyImp.cmd 祇ネ岿粇, 穨⊿ΤЧΘ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

:eoj
@echo on
pause