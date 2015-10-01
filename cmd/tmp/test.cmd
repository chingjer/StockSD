c:
cd C:\stockSD\srvjob\build\classes

set basedir="C:/stock/布/布祘Α/import/"
set completedir="C:/stock/布/布祘Α/complete/"

c:
cd %basedir%
copy T2*.csv %completedir%
copy G2*.csv %completedir%
del T2*.csv 
del G2*.csv 
pause
