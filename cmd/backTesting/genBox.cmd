echo off
time /T
cd C:\stockSD\svrjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem *** GenBox
rem %javaexe% GenBox 127.0.0.1/backtesting 2015/1/1 2015/1/16 >c:\stocksd\cmd\backTesting\genBox.txt
%javaexe% GenBox 127.0.0.1/backtesting 2009/01/01 2015/2/15
rem %javaexe% GenBox 
time /T
@echo on
pause