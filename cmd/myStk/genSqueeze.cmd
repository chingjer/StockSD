echo off
time /T
cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
%javaexe% GenSqueeze 127.0.0.1/mystk 2015/1/1 2015/2/12
time /T
@echo on
pause