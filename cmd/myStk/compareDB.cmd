echo off
time /T
cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
rem *** textÂà¤J¨ìMySQL
rem %javaexe% CompareDB Y stk ma5 ma10 va5 kdk kdd > c:\StockSD\cmd\compareDb.txt
%javaexe% CompareDB Y stk bandwidth  > c:\StockSD\cmd\compareDb.txt
time /T
@echo on
pause