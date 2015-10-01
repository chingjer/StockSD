select ktype,count(*) from candlestick where dte < '2015-1-1' group by ktype
select year(dte),count(*) from candlestick group by year(dte)
select year(dte),count(*) from candlestick where ktype='+¤W¤É¤Tªk' group by year(dte)
select count(*) from candlestick
select ktype,count(*) from candlestick where year(dte) > 2007 group by ktype
select count(*) from candlestick where year(dte) > 2007 
## --- delete duplicate ---
select * from candlestick 
where stockid+dte in (select stockid+dte from candlestick group by stockid,dte,ktype having count(*)> 1)
order by stockid,dte,ktype
## 