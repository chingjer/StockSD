echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/�Ѳ�/�Ѳ��{��/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** �U�����f�T�j�k�H
%javaexe% DownA1 %basedir% 2015/01/19 2015/01/19
rem %javaexe% DownA1 %basedir%
if errorlevel 0 goto eoj
echo !!!!! �U�� csv�ɵo�Ϳ��~ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause