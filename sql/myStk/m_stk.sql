select * from stk where dte = '2015-4-27'; 
## ----- CREATE TABLE for stkbackup 半年資料並刪除之
select dte,count(*) from stkbackup group by dte order by dte;
create table stk104H1 as select * from stkbackup where dte between '2015-1-1' and '2015-6-30';
select dte,count(*) from stk104H1 group by dte order by dte;
delete from stkbackup where dte between '2015-1-1' and '2015-6-30';

## ----- 查詢近2年漲幅曾經超過1倍者
CREATE TEMPORARY TABLE IF NOT EXISTS temp1 as 
(select stockid,max(p_high) as max_high,min(p_low) as min_low from stk where dte > '2013-1-1' 
and p_high <> 0 and p_low <> 0 group by stockid );

CREATE TEMPORARY TABLE IF NOT EXISTS tempmax as
(select a.stockid, b.dte as max_dte,a.max_high from temp1 a inner join stk b on a.stockid = b.stockid and a.max_high = b.p_high);

CREATE TEMPORARY TABLE IF NOT EXISTS tempmin as
(select a.stockid, b.dte as min_dte,a.min_low from temp1 a inner join stk b on a.stockid = b.stockid and a.min_low = b.p_low);
DROP TABLE IF EXISTS MINMAX;

CREATE TABLE MINMAX as
select max.stockid,i.stkname,min_low, min_dte,max_high, max_dte, round((max_high-min_low)*100/min_low,2) as `漲幅`
from tempmax max 
inner join tempmin min on max.stockid=min.stockid 
inner join stkid i on max.stockid = i.stockid
where max_dte > min_dte and min_low <> 0 and max_high <> 0
order by `漲幅` DESC;

select m.* from minmax m where concat(m.stockid,m.min_dte) in (select concat(stockid,min(min_dte)) from minmax group by stockid)
and m.`漲幅` between 100 and 400
##----- Check ATR
SELECT stockid, dte,price,p_high,p_low,tr FROM `stk` WHERE stockid='2330' and dte > '2015-7-1'