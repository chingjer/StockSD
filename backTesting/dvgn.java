
import java.sql.*;

/**
 * 多頭背離系統
 *
 * @author huangtm
 */
class dvgn extends ssd {
    /* -- 自訂的參數 --- */

    final int MAX_PTB = 14;
    final int BUY_CRI = 15;
    final int MIN_SC_OSC = 16;

    @Override
    public void setSys() {
        sys = "dvgn";
        subsys = "";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "dvgn", "c:/stockSd/cmd/backTesting/dvgn.TXT");
        setTDR("A,B,C,D,E,F,G,H,I,J,K,L,M", "Y");
        //setTDR("E", "Y");
        setRandom(false, 0, 0);
        setIsBear(false); //false=作多
        setDayInterval(5); // 設定下一筆需間隔幾天才不算重複
        setMaxUp(-1, 20); // 設定幾天內最大漲福，1為當天,-1時表示不檢查
        is_debug = true;
        setForceEnabled(true); // true=要依大盤出場
        setRate(oStk.cost_tax, oStk.cost_op);
    }

    @Override
    public void setParm() {
        para_grp = new String[]{
            "1,0.05,0.03,0,5,0.93,-1,1,尾盤,5, 14, 14, ma5, 0.96, 0.5, 1.01, -99"
        };
    }

    @Override
    public void printPara() {
        String s1 = getFixParaSpec();
        s1 += "\t(14)MAX_PTB=" + para[MAX_PTB];
        s1 += "\r\n(15)BUY_CRI=" + para[BUY_CRI];
        s1 += "\t(14=6)MIN_SC_OSC=" + para[MIN_SC_OSC];
        debugPrint(s1);
    }

    /**
     * 設定初始停損點
     */
    @Override
    public void setFirstStop() throws SQLException {
        //first_stop = d_rec.price * Double.parseDouble(para[FIRSTSTOP_CRI]);
        if (d_rec.pBuy != 0) {
            //first_stop = d_rec.pBuy * Double.parseDouble(para[FIRSTSTOP_CRI]);
            first_stop = rs_det.getDouble("dvgn_stop");
        } else {
            first_stop = 0d; //作多時設0, 作空時設9999, 表示永遠不會觸及
        }

    }

    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        return true;
    }

    @Override
    public void getData() throws SQLException {
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format("select d.*,s.percentb from divergence d inner join "
                + "stk s on d.stockid = s.stockid and d.dte = s.dte "
                + "where d.typ='+' and d.idx = 'rsi6' and s.percentb < %s "
                + "and sc_osc > %s and sc_mfi > 1 "
                + "and d.dte between %s and %s order by d.stockid,d.dte",
                para[MAX_PTB], para[MIN_SC_OSC],oStk.padCh(beg_date), oStk.padCh(end_date)));
        //System.out.println(sql_b1.toString());
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
        debugPrint(sql_b2.toString()); // check 最後一筆有無在資料庫中？
        clearVars();
        System.gc();
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        if (para[BUY_MODE].equals("尾盤")) {
            double buyPt = price * Double.parseDouble(para[BUY_CRI]);
            return overBuy(buyPt, 9999.0,para[BUY_MODE]);
        } else if (para[BUY_MODE].equals("開盤")) {
            buy_price = stk_rec.p_open;
            buy_date = stk_rec.dte;
            is_close = false;
            return true;

        }
        return false;

    }

    static public void main(String[] args) throws SQLException {
        dvgn sys = new dvgn();
        sys.Start();
    }
}
