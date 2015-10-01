CREATE OR REPLACE VIEW v_basic4 AS
select stockid,
  yr2 as `年報`, qt AS `季報`,grmonth AS `月報`,
  round(pbr,2) AS `股價淨值比`,  round(per,2) AS `本益比`,  round(psr,2) AS `每股營收比`,
  round(pricecash_r,2) AS `股價現金比`,
  val AS `價值分數`,debt_ratio AS `負債比`,
  cur_r AS `流動比率`,
  quick_r AS `速動比率`,
  round(roe5,2) AS `五年ROE平均`,
  round(eps4,2) AS `近4季合計eps`,
  CONCAT(qeps_1,"/",qeps_2,"/",qeps_3,"/",qeps_4,"/",qeps_5) AS `近5季Eps` 
  
  from stkbasic
  
  eps_1 double  default 0, ## 最近第1年eps
  eps_2 double  default 0, ## 最近第2年...
  eps_3 double  default 0, 
  eps_4 double  default 0, 
  eps_5 double  default 0, 
  roe_1 double  default 0, ## 最近第1年ROE
  roe_2 double  default 0, ## 最近第2年...
  roe_3 double  default 0, 
  roe_4 double  default 0, 
  roe_5 double  default 0, 
  
  cashbeop int  default 0, ## 期末現金及約當現金(Cash Balances - End of Period)
  cashchg_1 int  default 0, ## 最近第1期產生現金流量(Change in Cash Flow)
  cashchg_2 int  default 0, ## 最近第2期...
  cashchg_3 int  default 0, 
  cashchg_4 int  default 0,  
  outcap double  default 0, ## Outstanding capital,流通股本(億)
  yr int  default 0, ## 基本資料起始年度(現金股利等)
  cashdiv_1 double  default 0, ## 最近第1年現金股利(Cash Dividend)
  cashdiv_2 double  default 0, ## 最近第2年...
  cashdiv_3 double  default 0, 
  cashdiv_4 double  default 0, 
  cashdiv_5 double  default 0, 
  cashdiv_6 double  default 0, 
  nav double  default 0, ## 每股淨值 
  yr2 varchar(20) , ## 財務比率起始年度(eps,roe,流動比率等)
  cashdiv_yld double  default 0, ## 現金殖利率(Cash Dividend Yield)
  rev12 int  default 0, ## 最近12個月總營收(仟)(Revenue)
  yrmax double  default 0, ## 近2年最高價
  yrmin double  default 0,  ## 近2年最低價
  yrdown double  default 0, ## 距2年高點(%)
  long_inv_1 int  default 0, ## 長期投資_1(百萬)( long term investments)
  long_inv_5 int  default 0, ## 長期投資_5
  fix_ass_1 int  default 0, ## 固定資產_1(百萬)(Fixed assets)
  fix_ass_5 int  default 0, ## 固定資產_5 
  nprofita_1 int  default 0, ## 稅後淨利_1 (百萬)(net profit after tax)
  nprofita_2 int  default 0, ## 稅後淨利_2
  nprofita_3 int  default 0, ## 稅後淨利_3 
  nprofita_4 int  default 0, ## 稅後淨利_4 
  reinv_rate4 double  default 0, ## 四年盈再率(Reinvestment Rate)
  roe_sc int  default 0, ## roe 品質
  grmonth varchar(7) , ## 營收月份(如103/10)
  gryoy_1 double  default 0, ## 最近1月營收成長率(YOY)
  gryoy_2 double  default 0, ## 最近2月
  gryoy_3 double  default 0, 
  gracc double  default 0, ## 今年累計營收成長率(YOY)
  qeg_1 double  default 0, ## 最近1季稅後eps成長率(YOY)
  qeg_2 double  default 0, ## 最近2季...
  qeg_3 double  default 0, 
  qeg_4 double  default 0, 
  qeg_5 double  default 0, 
  qeg_6 double  default 0, 
  qeg_7 double  default 0, 
  qeg_8 double  default 0, 
  qeg_sc int  default 0, ## 獲利成長品質
  yrmax_dte date , ## 近2年最高價日期
  befup double  default 0 ## 範圍(高點到高點後最低點，必須<=30%)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

