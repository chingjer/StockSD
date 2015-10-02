CREATE OR REPLACE VIEW v_mystk AS
select m.id as `識別碼`, m.account as `帳戶`,
  case m.catgry WHEN 'F' then '資' WHEN 'B' then '券' WHEN 'N' then '現' ELSE m.catgry END as '資券',
  m.stockid as `代號`,i.stkname as `名稱`,
  m.in_date as `進場日`,
  m.in_price as `進場價`,
  m.qty as `數量`,
  i.price as `今收`,
  round(s.tr,2) as `ATR`,
  m.out_date as `出場日`,
  m.out_price as `出場價`,
  m.stp_price as `停損價`,
  CONCAT(m.mthd,t.spec,'(',t.HOLDDAYS,')') as `模式`
  from mystk m 
  inner join stkid i on m.stockid = i.stockid
  inner join stk s on s.stockid = i.stockid and s.dte = m.in_date
  left join mthd t on m.mthd = t.mthd
  