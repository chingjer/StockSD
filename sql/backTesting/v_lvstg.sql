CREATE OR REPLACE VIEW v_lvstg AS
SELECT s.stockId, s.dte, s.price, s.ptbrsi6, s.updown, 
s.vol, s.KDK, s.sc_osc, s.MFI, s.va5
FROM stk s
WHERE s.price>5 AND s.updown Between 6.5 And 7.1 AND 
s.vol>va5 And s.vol>100 AND s.KDK>60 AND s.sc_osc>0 
AND s.MFI>=50 AND s.sc_kdk>0 AND s.KDD<kdk AND s.sc_kdd>0;
