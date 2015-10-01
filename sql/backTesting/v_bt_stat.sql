CREATE OR REPLACE VIEW v_bt_stat AS
select b.sys,b.subsys,b.num as `參數編號`, b.sel_recs as `總筆數`, b.trade_recs as `交易筆數`, 
round(b.trade_recs*100/b.sel_recs,2) as `交易率%`,b.exp as `期望值`,
b.net_profit as `獲利`, b.exp_r as `R期望值`, b.winning as `勝率%`,
b.avgdays as `平均天數`, b.exp_quater as `季期望值`, 
b.datecode, p.begdate, p.enddate,p.rmk
from bt_stat b inner join bt_period p on b.datecode = p.datecode
order by sys,subsys,datecode, num