echo off
time /T
set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
%javaexe% StockCsv2 %basedir% >c:\stockSD\cmd\Stocsv2.txt
if not errorlevel 0 goto err
copy %basedir%T2*.csv %completedir%
copy %basedir%G2*.csv %completedir%
del %basedir%T2*.csv 
del %basedir%G2*.csv 
:err
time /T
@echo on
pause