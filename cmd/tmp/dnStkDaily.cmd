echo off
cd C:\stockSD\srvjob\build\classes
set basedir="C:/stock/�Ѳ�/�Ѳ��{��/import/"

set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"

rem *** �U�� csv��
rem java dnStkDaily %basedir% 2014/09/09
%javaexe% dnStkDaily %basedir%
if errorlevel 0 goto eoj
echo !!!!! �U�� csv�ɵo�Ϳ��~ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
:eoj
dir %basedir%
pause