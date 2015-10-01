CREATE OR REPLACE VIEW v_bsidx AS
select CONCAT('[',s.t5b ,'/',s.t5s,',',s.t20b,'/',s.t20s,']') as bsidx , s.*
from tppii_sum s 
