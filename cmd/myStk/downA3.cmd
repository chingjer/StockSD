echo off
time /T
set basedir="C:/stock/�Ѳ�/�Ѳ��{��/import/"
set completedir="C:/stock/�Ѳ�/�Ѳ��{��/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
%javaexe% DownA3 %basedir% 2015/06/09
time /T
@echo on
pause