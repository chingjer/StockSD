echo off
time /T
set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
rem %javaexe% StockCsv2 %basedir% >c:\stockSD\cmd\Stocsv2.txt
%javaexe% CalcBasic %basedir%
time /T
@echo on
pause