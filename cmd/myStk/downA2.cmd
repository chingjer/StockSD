echo off
time /T
set basedir="C:/stock/布/布祘Α/import/"
set completedir="C:/stock/布/布祘Α/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
%javaexe% DownA2 %basedir% 2015/02/01
time /T
@echo on
pause