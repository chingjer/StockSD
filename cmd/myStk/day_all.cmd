@echo off
c:
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/�Ѳ�/�Ѳ��{��/import/"
set completedir="C:/stock/�Ѳ�/�Ѳ��{��/complete/"
rem set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
set _ERR=0
set _RPT="c:\stockSD\cmd\myStk\day_all_rpt.txt"
date /T > %_RPT%
time /T >> %_RPT%
set isall="N"

rem goto error_resume
echo *** �U���L������ ***
rem %javaexe% dnStkDaily %basedir% 2015/09/09
%javaexe% dnStkDaily %basedir%   >> %_RPT% 
if not errorlevel 0 set _ERR="dnStkDaily Error!"
if NOT %_ERR%==0 goto err

:imp
echo *** �N�L��csv ����� myStk ��Ʈw stk***
%javaexe% StockCsv %basedir%  >> %_RPT% 
if not errorlevel 0 set _ERR="StockCsv Error!"
if NOT %_ERR%==0 goto err

echo *** �p��U�� ma ***
%javaexe% CalcMa %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMa Error!"
if NOT %_ERR%==0 goto err

echo *** �p��U�� kd ***
%javaexe% CalcKd %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcKd Error!"
if NOT %_ERR%==0 goto err

echo *** �p��U�� rsi ***
%javaexe% CalcRsi %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcRsi Error!"
if NOT %_ERR%==0 goto err

echo *** �p�� MACD, OSC,DIF ***
%javaexe% CalcMacd %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMacd Error!"
if NOT %_ERR%==0 goto err

echo *** �p�� mfi ***
%javaexe% CalcMfi %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMfi Error!"
if NOT %_ERR%==0 goto err

echo *** �p�� �U�� %b() ����***
%javaexe% CalcPtb %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcPtb Error!"
if NOT %_ERR%==0 goto err

echo *** �p�� ��L����(sc_, minbw��) ***
%javaexe% CalcOther %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcOther Error!"
if NOT %_ERR%==0 goto err

echo *** �p�� ATR ***
%javaexe% CalcAtr %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcAtr Error!"
if NOT %_ERR%==0 goto err

echo *** ���� box,dbox ***
%javaexe% GenBox 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenBox Error!"
if NOT %_ERR%==0 goto err

echo *** ���� Squeeze ***
%javaexe% GenSqueeze 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenSqueeze Error!"
if NOT %_ERR%==0 goto err

echo *** ���� candleStick ***
%javaexe% genCandleDay 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenCandle Error!"
if NOT %_ERR%==0 goto err

echo *** ���� divergence ***
%javaexe% IndexDivergence 127.0.0.1/mystk rsi6 >> %_RPT% 
if not errorlevel 0 set _ERR="IndexDivergence Error!"
if NOT %_ERR%==0 goto err

echo *** �U���T�j�k�H�R������ ***
rem %javaexe% dnStkDaily2 %basedir% 2015/09/09
%javaexe% dnStkDaily2 %basedir%   >> %_RPT% 
if not errorlevel 0 set _ERR="dnStkDaily2(�T�j�k�H) Error!"
if NOT %_ERR%==0 goto err

:error_resume
:imp2
echo *** �N�T�j�k�H�R������csv ����� myStk ��Ʈw tppii ***
%javaexe% StockCsv2 %basedir%  >> %_RPT% 
if not errorlevel 0 set _ERR="StockCsv2 (�T�j�k�H) Error!"
if NOT %_ERR%==0 goto err

echo *** �έp�T�j�k�H�J�`�� tppii_sum ***
%javaexe% CalcTppiiSum  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcTppiiSum (�T�j�k�H�J�`��) Error!"
if NOT %_ERR%==0 goto err

echo *** �p��򥻸��per,pbr�� ***
%javaexe% CalcBasic  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcBasic (�p��򥻸��per,pbr��) Error!"
if NOT %_ERR%==0 goto err

echo *** ��s�Ȱ��Ĩ��� ***
%javaexe% GetStopDate  >> %_RPT% 
if not errorlevel 0 set _ERR="GetStopDate (��s�Ȱ��Ĩ���) Error!"
if NOT %_ERR%==0 goto err

echo *** �ƥ���Ʈw ***
call c:\stockSD\cmd\backupDb.cmd >>%_RPT%
cd C:\stockSD\srvjob\build\classes

echo *** �C�L�L���ѳ��� ***
%javaexe% DailyReport >> %_RPT% 
if not errorlevel 0 set _ERR="CalcBasic (�C�L�L���ѳ���) Error!"
if NOT %_ERR%==0 goto err

rem ... �C��n�����Ʀp��s���е��~�򩹤U�[
:mail
echo ===== Job Complete! ===== >>%_RPT%
call C:\stockSD\cmd\myStk\MySendMail "day_all�@�~����" "day_all Complete!"
c:
cd %basedir%
copy A112*.csv %completedir%
copy RSTA*.csv %completedir%
del A112*.csv 
del RSTA*.csv 
copy T2*.csv %completedir%
copy G2*.csv %completedir%
del T2*.csv 
del G2*.csv 

goto eoj
:err
echo !!!!! �o�Ϳ��~, �@�~�S������ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! >>%_RPT%
call C:\stockSD\cmd\myStk\MySendMail "day_all���`�q��" %_ERR%
:eoj
time /T >> %_RPT%
@echo on