echo off
time /T
set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"

cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
%javaexe% DownA3 %basedir% 2015/06/09
time /T
@echo on
pause