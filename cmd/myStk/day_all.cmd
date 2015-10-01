@echo off
c:
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/股票/股票程式/import/"
set completedir="C:/stock/股票/股票程式/complete/"
rem set javaexe="C:\Program Files (x86)\Java\jdk1.7.0_67\bin\java.exe"
set javaexe="C:\Program Files\Java\jdk1.8.0_40\bin\java.exe"
set _ERR=0
set _RPT="c:\stockSD\cmd\myStk\day_all_rpt.txt"
date /T > %_RPT%
time /T >> %_RPT%
set isall="N"

rem goto error_resume
echo *** 下載盤後資料檔 ***
rem %javaexe% dnStkDaily %basedir% 2015/09/09
%javaexe% dnStkDaily %basedir%   >> %_RPT% 
if not errorlevel 0 set _ERR="dnStkDaily Error!"
if NOT %_ERR%==0 goto err

:imp
echo *** 將盤後csv 檔轉到 myStk 資料庫 stk***
%javaexe% StockCsv %basedir%  >> %_RPT% 
if not errorlevel 0 set _ERR="StockCsv Error!"
if NOT %_ERR%==0 goto err

echo *** 計算各種 ma ***
%javaexe% CalcMa %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMa Error!"
if NOT %_ERR%==0 goto err

echo *** 計算各種 kd ***
%javaexe% CalcKd %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcKd Error!"
if NOT %_ERR%==0 goto err

echo *** 計算各種 rsi ***
%javaexe% CalcRsi %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcRsi Error!"
if NOT %_ERR%==0 goto err

echo *** 計算 MACD, OSC,DIF ***
%javaexe% CalcMacd %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMacd Error!"
if NOT %_ERR%==0 goto err

echo *** 計算 mfi ***
%javaexe% CalcMfi %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcMfi Error!"
if NOT %_ERR%==0 goto err

echo *** 計算 各種 %b() 指標***
%javaexe% CalcPtb %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcPtb Error!"
if NOT %_ERR%==0 goto err

echo *** 計算 其他指標(sc_, minbw等) ***
%javaexe% CalcOther %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcOther Error!"
if NOT %_ERR%==0 goto err

echo *** 計算 ATR ***
%javaexe% CalcAtr %isall%  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcAtr Error!"
if NOT %_ERR%==0 goto err

echo *** 產生 box,dbox ***
%javaexe% GenBox 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenBox Error!"
if NOT %_ERR%==0 goto err

echo *** 產生 Squeeze ***
%javaexe% GenSqueeze 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenSqueeze Error!"
if NOT %_ERR%==0 goto err

echo *** 產生 candleStick ***
%javaexe% genCandleDay 127.0.0.1/mystk >> %_RPT% 
if not errorlevel 0 set _ERR="GenCandle Error!"
if NOT %_ERR%==0 goto err

echo *** 產生 divergence ***
%javaexe% IndexDivergence 127.0.0.1/mystk rsi6 >> %_RPT% 
if not errorlevel 0 set _ERR="IndexDivergence Error!"
if NOT %_ERR%==0 goto err

echo *** 下載三大法人買賣資料檔 ***
rem %javaexe% dnStkDaily2 %basedir% 2015/09/09
%javaexe% dnStkDaily2 %basedir%   >> %_RPT% 
if not errorlevel 0 set _ERR="dnStkDaily2(三大法人) Error!"
if NOT %_ERR%==0 goto err

:error_resume
:imp2
echo *** 將三大法人買賣資料檔csv 檔轉到 myStk 資料庫 tppii ***
%javaexe% StockCsv2 %basedir%  >> %_RPT% 
if not errorlevel 0 set _ERR="StockCsv2 (三大法人) Error!"
if NOT %_ERR%==0 goto err

echo *** 統計三大法人彙總表 tppii_sum ***
%javaexe% CalcTppiiSum  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcTppiiSum (三大法人彙總表) Error!"
if NOT %_ERR%==0 goto err

echo *** 計算基本資料per,pbr等 ***
%javaexe% CalcBasic  >> %_RPT% 
if not errorlevel 0 set _ERR="CalcBasic (計算基本資料per,pbr等) Error!"
if NOT %_ERR%==0 goto err

echo *** 更新暫停融券資料 ***
%javaexe% GetStopDate  >> %_RPT% 
if not errorlevel 0 set _ERR="GetStopDate (更新暫停融券資料) Error!"
if NOT %_ERR%==0 goto err

echo *** 備份資料庫 ***
call c:\stockSD\cmd\backupDb.cmd >>%_RPT%
cd C:\stockSD\srvjob\build\classes

echo *** 列印盤後選股報表 ***
%javaexe% DailyReport >> %_RPT% 
if not errorlevel 0 set _ERR="CalcBasic (列印盤後選股報表) Error!"
if NOT %_ERR%==0 goto err

rem ... 每日要做的事如更新指標等繼續往下加
:mail
echo ===== Job Complete! ===== >>%_RPT%
call C:\stockSD\cmd\myStk\MySendMail "day_all作業完成" "day_all Complete!"
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
echo !!!!! 發生錯誤, 作業沒有完成 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! >>%_RPT%
call C:\stockSD\cmd\myStk\MySendMail "day_all異常通知" %_ERR%
:eoj
time /T >> %_RPT%
@echo on