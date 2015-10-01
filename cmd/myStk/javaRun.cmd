@echo off
set CLASSPATH_TEMP=%CLASSPATH%
set CLASSPATH=%CLASSPATH%;C:\stockSD\javaLib\build\classes;
set jerr=0
"C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java" %1 %2 %3 
if not errorlevel 0 set jerr=-1
set CLASSPATH=%CLASSPATH_TEMP%
set CLASSPATH_TEMP=
@echo on