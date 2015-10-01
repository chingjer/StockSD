echo off
time /T
set basedir="C:/stock/布/布祘Α/import/"
set completedir="C:/stock/布/布祘Α/complete/"

cd C:\stockSD\srvjob\build\classes
rem set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"

rem %javaexe% StockCsv2 %basedir% >c:\stockSD\cmd\mystk\Stocsv2.txt
rem %javaexe% IndexDivergence rsi6
rem %javaexe% GetStopDate > c:\stockSD\cmd\mystk\GetStopDate.txt
%javaexe% CalcAtr N > c:\stockSD\cmd\mystk\CalcAtr.txt
time /T
@echo on
pause