update bt_para set enabled=0 where sys="candle" and subsys="@晨星" and num <> 1;

delete from bt_para where sys="candle" and subsys="@貫穿線" and num <> 4;
select * from bt_para where  sys="candle" and subsys="-夜星" order by num;