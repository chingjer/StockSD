@echo off
set CLASSPATH_TEMP=%CLASSPATH%
set CLASSPATH=%CLASSPATH%;C:\stockSD\javaLib\build\classes;
"C:\Program Files (x86)\Java\jdk1.7.0_67\bin\javac" -Xlint:unchecked %1 %2 %3 
set CLASSPATH=%CLASSPATH_TEMP%
set CLASSPATH_TEMP=
@echo on