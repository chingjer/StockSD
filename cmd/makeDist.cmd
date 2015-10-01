REM === 同時製作分布擋與備份在隨身碟 ***
set dest="E:\StockSD_dist"
set from="C:\StockSD"
set htdoc="C:\xampp\htdocs"

rem == backup StockSd
xcopy /M /S /Y  %from%\srvjob\src\*.java %dest%\srvjob\*.*
xcopy /M /S /Y  %from%\backTesting\src\*.java %dest%\backTesting\*.*
xcopy /M /S /Y  %from%\javalib\src\*.java %dest%\javalib\*.*
xcopy /Y /S %from%\javalib\src\DbConfig.chg %dest%\javalib\DbConfig.java
xcopy /M /S /Y  %from%\dbgen\*.* %dest%\dbgen\*.*
xcopy /M /S /Y  %from%\sql\*.* %dest%\sql\*.*
xcopy /M /S /Y  %from%\phpLib\*.* %dest%\phpLib\*.*
xcopy /M /S /Y  %from%\cmd\*.cmd %dest%\cmd\*.*
xcopy /M /S /Y  %from%\cmd\*.ini %dest%\cmd\*.*

xcopy /M /S /Y %htdoc%\PhpProject1\*.* e:\htdocs\PhpProject1\*.*
xcopy /M /S /Y %htdoc%\ssd\*.* %dest%\htdocs\ssd\*.*
xcopy /M /S /Y %htdoc%\BT\*.* %dest%\htdocs\BT\*.*
xcopy /M /S /Y %htdoc%\jslib\*.* %dest%\htdocs\jslib\*.*
xcopy /M /S /Y c:\nb5\*.* e:\nb5\*.*

rem attrib /S -A %dest%\*.*
pause