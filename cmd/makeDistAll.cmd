REM === 同時製作分布擋與備份在隨身碟 ***
set dest="E:\StockSD_dist"
set from="C:\StockSD"
set htdoc="C:\xampp\htdocs"

del /S /Q %dest%
cd %from%
attrib +a /s *.*
cd %htdoc%
attrib +a /s *.*
cd C:\stockSD\cmd
makeDist.cmd