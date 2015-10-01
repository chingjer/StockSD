CREATE OR REPLACE VIEW v_bt_para AS
select ID,
sys,subsys,num,enabled,firststop_cri as `fstStop`,in_days as `inDays`,buy_mode as `mode`,holddays_cri as `holdDays`,
min_profit as `minProf`,chg_cri as `CHG`,
stop_ma,filt_ma,p14 , p15 , p16 , p17 , p18 , p19 , p20 , p21 , p22 , p23 , p24 , p25  
from bt_para

