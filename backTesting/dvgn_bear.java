
import java.sql.*;
class dvgn_bear extends ssd {
    final int BUY_CRI = 14;
    final int MIN_PTB = 15;

    @Override
    public void setSys() {
        sys = "dvgn_bear";
        subsys = "1";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "dvgn_bear", "c:/stockSd/cmd/backTesting/dvgn_bear.TXT");
        //setTDR("3,5,G,H,I,J,K", "Y");
        setTDR("A,B,C,D,E,F,3,5,G,W", "Y");
        //setTDR("E", "Y");
        setRandom(false, 0, 0);
        
        setIsBear(true); //false=作多
        
        setDayInterval(5); // 設定下一筆需間隔幾天才不算重複
        setMaxUp(-1, 20); // 設定幾天內最大漲福，1為當天,-1時表示不檢查
        is_debug = true;
        setForceEnabled(true); // true=要依大盤出場
        setRate(oStk.cost_tax, oStk.cost_op);

    }

    @Override
    public void setParm() {
        para_grp = new String[]{
            "1, 0.05, 0.03, 0, 3, 0,  99,  1, 開盤, 3, 14, 14, ma5, 1.04, 0.98, 0.7"
        };
        //    "1, 0.05, 0.03, 0, 3, 0,  99,  1, 收盤, 3, 14, 14, ma5, 1.04, 0.98, 0.6"
    }

    @Override
    public void printPara() {
        String s1 = new String(getFixParaSpec());
        s1 = s1 + "\r\n(14)BUY_CRI=" + para[BUY_CRI];
        s1 = s1 + "\t(15)MIN_PTB=" + para[MIN_PTB];

        debugPrint(s1);
    }

    @Override
    public void setFirstStop() throws SQLException {
        if (d_rec.pBuy != 0) {
            //first_stop = d_rec.price * Double.parseDouble(para[FIRSTSTOP_CRI]);
            first_stop = rs_det.getDouble("dvgn_stop");
        } else {
            first_stop = is_bear?9999d:0d; //作多時設0, 作空時設9999, 表示永遠不會觸及
        }
    }

    @Override
    public void getData() throws SQLException {
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append( "delete from " + tbl_det + " where datecode='" + date_code + "'");
        //sql_b1.append("delete from " + tbl_det );
        stmt_update.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format("select d.*,s.percentb from divergence d inner join "
                + "stk s on d.stockid = s.stockid and d.dte = s.dte "
                + "where d.typ='-' and d.idx = 'rsi6' and s.percentb > %s "
                + "and rsi6 < rsi12 "
                + "and d.dte between %s and %s order by d.stockid,d.dte",
                para[MIN_PTB],oStk.padCh(beg_date), oStk.padCh(end_date)));
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            initStr(sql_b2);
            sql_b2.append("insert into " + tbl_det
                    + " (stockid,dte,price,percentb,dvgn_stop,datecode) values ("
                    + "'" + rs_det.getString("stockid") + "', "
                    + "'" + rs_det.getString("dte") + "', "
                    + "'" + rs_det.getString("price") + "', "
                    + "'" + rs_det.getString("percentb") + "', "
                    + "'" + rs_det.getString("p_stop") + "', "
                    + "'" + date_code + "') ");
            stmt_update.executeUpdate(sql_b2.toString());
        }
        clearVars();
        System.gc();
    }

    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        return true;
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double buyPt;
        double buy_cri = Double.parseDouble(para[BUY_CRI]);
        boolean isOk = false;

        buyPt = price * buy_cri;
        
        if (para[BUY_MODE].equals("開盤")) {
            buy_price = stk_rec.p_open;
            buy_date = stk_rec.dte;
            is_close = false;
            return true;
        }
        if (para[BUY_MODE].equals("收紅")) {
            //if (stk_rec.price < buyPt && upDown >= 0) {
            //    isOk = true;
            //}
        } else {
            if (stk_rec.price < buyPt) {
                isOk = true;
            }
        }
        if (isOk) {
            buy_price = stk_rec.price;
            buy_date = stk_rec.dte;
            is_close = true;
            return true;
        } else {
            return false;
        }
    }

    static public void main(String[] args) throws SQLException {
        dvgn_bear sys = new dvgn_bear();
        sys.Start();
    }
}
