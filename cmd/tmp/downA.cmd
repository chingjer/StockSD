echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/股票/股票程式/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** 下載期貨三大法人
%javaexe% DownA1 %basedir% 2015/01/19 2015/01/19
rem %javaexe% DownA1 %basedir%
if errorlevel 0 goto eoj
echo !!!!! 下載 csv檔發生錯誤 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause