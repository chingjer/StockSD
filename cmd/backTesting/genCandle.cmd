echo off
time /T
cd C:\stockSD\srvjob\build\classes
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem 一年資料大約1hr10min
rem %javaexe% genCandle 127.0.0.1/backtesting 2015-1-1 2015-5-6 
rem %javaexe% genCandle 127.0.0.1/backtesting 2013-1-1 2013-12-31 
rem %javaexe% genCandle 127.0.0.1/backtesting 2008-1-1 2008-6-30
rem %javaexe% genCandle 127.0.0.1/backtesting 2012-1-1 2012-12-31 

%javaexe% genCandle 127.0.0.1/backtesting 2011-1-1 2011-12-31 
%javaexe% genCandle 127.0.0.1/backtesting 2010-1-1 2010-12-31 
%javaexe% genCandle 127.0.0.1/backtesting 2009-1-1 2009-12-31 
%javaexe% genCandle 127.0.0.1/backtesting 2008-7-1 2008-12-31
%javaexe% genCandle 127.0.0.1/backtesting 2014-1-1 2014-12-31 
time /T
@echo on
pause