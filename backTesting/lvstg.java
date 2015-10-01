
import java.sql.*;

/**
 * 大量強勢股
 *
 * @author huangtm
 */
class lvstg extends ssd {
    /* -- 自訂的參數 --- */

    final int MIN_VOL = 14;
    final int MIN_VOLVA5 = 15;
    final int MIN_KDK = 16;
    final int MIN_SC_MACD = 17;
    final int MIN_MFI = 18;
    final int MIN_PRSI = 19;
    final int BUY_CRI = 20;

    @Override
    public void setSys(){
       sys = "lvstg";
       subsys = "";
    }
    @Override
    public void initOther() {
        setFiles("bt_stat", "lvstg", "c:/stockSd/cmd/backTesting/LVSTG.TXT");
        //setTDR("A,B,C,D,E,F,G,H,I,J,K", "Y");
        setTDR("M", "Y");
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
            "1,0.05,0.03,0,3,1,-1,5, 尾盤,5, 14, 14, ma5, 0.96,500,2,75,4,80,1,1.03"
        };
    }

    @Override
    public void printPara() {
        String s1 = getFixParaSpec();
        s1 += "\r\n(14)MIN_VOL=" + para[MIN_VOL];
        s1 += "\t(15)MIN_VOLVA5=" + para[MIN_VOLVA5];
        s1 += "\t(16)MIN_KDK=" + para[MIN_KDK];
        s1 += "\t(17)MIN_SC_MACD=" + para[MIN_SC_MACD];
        s1 += "\r\n(18)MIN_MFI=" + para[MIN_MFI];
        s1 += "\t(19)MIN_PRSI=" + para[MIN_PRSI];
        s1 += "\t(20)BUY_CRI=" + para[BUY_CRI];
        debugPrint(s1);
    }

    /**
     * 設定初始停損點
     */
    @Override
    public void setFirstStop() throws SQLException {
        first_stop = d_rec.price * Double.parseDouble(para[FIRSTSTOP_CRI]);
    }

    @Override
    public void getData() throws SQLException {
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format("select * from v_lvstg where vol > %s and vol > va5 * %s"
                + " and kdk > %s and sc_osc >= %s and mfi >= %s and ptbrsi6 >= %s"
                + " and dte between %s and %s order by stockid,dte",
                para[MIN_VOL], para[MIN_VOLVA5], para[MIN_KDK], para[MIN_SC_MACD],
                para[MIN_MFI], para[MIN_PRSI], oStk.padCh(beg_date), oStk.padCh(end_date)));
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            initStr(sql_b2);
            sql_b2.append("insert into " + tbl_det
                    + " (stockid,dte,price,datecode) values ("
                    + "'" + rs_det.getString("stockid") + "', "
                    + "'" + rs_det.getString("dte") + "', "
                    + "'" + rs_det.getString("price") + "', "
                    + "'" + date_code + "') ");
            stmt_update.executeUpdate(sql_b2.toString());
        }
        debugPrint(sql_b2.toString()); // check 最後一筆有無在資料庫中？
        clearVars();
        System.gc();
    }

    /**
     * 其他之進場篩選, return true 表示通過本項篩選
     */
    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        return !(date_code.equals("C") && "5410;5455".contains(d_rec.stockId));
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double buyPt = price * Double.parseDouble(para[BUY_CRI]);
        return overBuy(buyPt, 9999.0,para[BUY_MODE]);

    }

    static public void main(String[] args) throws SQLException {
        lvstg sys = new lvstg();
        sys.Start();
    }
}
