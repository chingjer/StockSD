echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/股票/股票程式/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** 下載 csv檔
rem java dnStkDaily %basedir% 2014/09/09
%javaexe% dnStkDaily %basedir%
if errorlevel 0 goto eoj
echo !!!!! 下載 csv檔發生錯誤 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause