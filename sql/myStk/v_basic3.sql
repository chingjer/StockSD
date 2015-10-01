CREATE OR REPLACE VIEW v_basic3 AS
SELECT stockid,  dte as `日期`, 
  CEIL(a_qty) as `外資淨買超`,
  CEIL(b_qty) as `投信淨買超`,
  CEIL(c_qty) as `自營淨買超`,
  CEIL(tot_qty) as `三大法人淨買超`
FROM tppii