echo off
time /T
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
cd C:\stockSD\srvjob\build\classes

rem Usage: java webBasic mode scope
rem mode: 'A' - 全部重新處理, 'C' - 預設，只處理尚未處理者，'T' - 測試，只處理 1101 
rem scope: 'Q' - Quarter, 'M' - Month

%javaexe% GetBasicData A M 104/07 > c:\stocksd\cmd\mystk\getBasicData.txt
rem %javaexe% GetBasicData A Q 104.1Q > c:\stocksd\cmd\mystk\getBasicData.txt

time /T
@echo on
pause