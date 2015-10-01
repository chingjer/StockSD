update bt_period set rmk="2015" where datecode="N";
delete from bt_period where datecode='W';
insert into bt_period (datecode,begdate,enddate,enabled,rmk) values("W","2008-1-1","2015-12-31","Y","2008-2015");
select * from bt_period order by datecode;
