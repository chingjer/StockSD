echo off
time /T

cd C:\stockSD\backTesting\build\classes
rem set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
rem ***** extends ssd
%javaexe% bbrev
rem ***** extends ssdp
rem %javaexe% bear5lay bear5lay 1 A,B,C,D,E,F,G,W
rem A,B,C,D,E,F,G,H,I,J,K,L,M,N,W
rem %javaexe% candle "candle" "@�l�l" "B,C,D,E,G,W"
rem %javaexe% candle "candle" "@�W�ɤT�k" "W"
rem %javaexe% candle "candle" "-���Y�]��" "A,B,C,D,E,G,I,J,K,L,M,N,W"
time /T
@echo on
pause