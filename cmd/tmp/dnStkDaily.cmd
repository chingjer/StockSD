echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/布/布祘Α/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** 更 csv郎
rem java dnStkDaily %basedir% 2014/09/09
%javaexe% dnStkDaily %basedir%
if errorlevel 0 goto eoj
echo !!!!! 更 csv郎祇ネ岿粇 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause