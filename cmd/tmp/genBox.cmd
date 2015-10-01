echo off
time /T
cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
rem *** GenBox
rem %javaexe% GenBox 2015/1/1 2015/1/16 >c:\stocksd\cmd\genBox.txt
%javaexe% GenBox 2015/1/1 2015/1/16
rem %javaexe% GenBox 
time /T
@echo on
pause