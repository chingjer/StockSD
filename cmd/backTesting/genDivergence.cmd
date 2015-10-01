echo off
time /T

cd C:\stockSD\svrjob\build\classes
rem set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem %javaexe% lvstg
rem %javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2014-1-1 2014-12-31 > "c:\stockSD\cmd\backTesting\divergence.txt"
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2007-12-1 2008-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2008-12-1 2009-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2009-12-1 2010-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2010-12-1 2011-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2011-12-1 2012-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2012-12-1 2013-12-31
%javaexe% IndexDivergence 127.0.0.1/backtesting kdk 2013-12-1 2014-12-31
time /T
@echo on
pause