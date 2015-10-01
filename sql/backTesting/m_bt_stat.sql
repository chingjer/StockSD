delete from bt_stat where sys='bbrev'
delete from bt_stat where sys='candle' and subsys='@上升三法'; 
delete from bbdrev
select * from bt_stat where sys='candle' and subsys='-空頭母子' order by num,datecode