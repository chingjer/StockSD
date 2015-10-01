echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/布/布祘Α/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** 更戳砯猭
%javaexe% DownA1 %basedir% 2015/01/19 2015/01/19
rem %javaexe% DownA1 %basedir%
if errorlevel 0 goto eoj
echo !!!!! 更 csv郎祇ネ岿粇 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause