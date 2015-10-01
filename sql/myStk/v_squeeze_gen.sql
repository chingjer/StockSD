CREATE OR REPLACE VIEW v_squeeze_gen AS
SELECT stockId, dte , bandwidth, vol, va10, 
updown , price, ub, percentb, sc_osc, MFI, ptbrsi12, va5
FROM stk WHERE 
vol>=va10*3 AND 
percentb>=0.8 AND 
sc_osc>1 AND 
MFI>=60 AND 
ptbrsi12>0.7 AND 
KDK>KDD AND 
sc_kdk>0 AND 
sc_kdd>0;
