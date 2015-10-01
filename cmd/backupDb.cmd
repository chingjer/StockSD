echo *** backup mysql Database start ***
time /T
c:
cd \xampp\mysql\bin
if not exist e:\mysql_bk md e:\mysql_bk
mysqldump -uroot -p%ppp%99 mystk stk --no-data >e:\mysql_bk\mystk.sql
mysqldump -uroot -p%ppp%99 mystk --ignore-table=mystk.stk >>e:\mysql_bk\mystk.sql
echo *** backup backtesting Database start ***
time /T
mysqldump -uroot -p%ppp%99 backtesting stk --no-data >e:\mysql_bk\backtesting.sql
mysqldump -uroot -p%ppp%99 backtesting --ignore-table=backtesting.stk >>e:\mysql_bk\backtesting.sql
time /T
echo *** backup databse end ***

