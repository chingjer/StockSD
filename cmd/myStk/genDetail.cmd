echo off
time /T
set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem %javaexe% StockCsv2 %basedir% >c:\stockSD\cmd\Stocsv2.txt
%javaexe% GenDetail 127.0.0.1/mystk bt_para C:\xampp\htdocs\BT\ 3
time /T
@echo on
pause