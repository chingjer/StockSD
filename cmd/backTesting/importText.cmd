echo off
time /T
cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem *** textÂà¤J¨ìMySQL
%javaexe% ImportText 127.0.0.1/backtesting stk c:\temp\stk097H1.txt >> "c:\stockSD\cmd\backTesting\imortText.txt"
@echo on
time /T
pause