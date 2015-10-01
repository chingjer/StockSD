
import java.sql.*;

/**
 * 大量強勢股
 *
 * @author huangtm
 */
class lvstgp extends ssdp {
    @Override
    public void initOther() {
        setFiles("bt_stat", "lvstg", "c:/stockSd/cmd/backTesting/LVSTG.TXT");
        setTDR(dateCodes, "Y");
        setRandom(false, 0, 0);
        setIsBear(false); //false=作多
        setDayInterval(5); // 設定下一筆需間隔幾天才不算重複
        setMaxUp(-1, 20); // 設定幾天內最大漲福，1為當天,-1時表示不檢查
        is_debug = true;
        setForceEnabled(true); // true=要依大盤出場
        setRate(oStk.cost_tax, oStk.cost_op);
    }

    /**
     * 設定初始停損點
     */
    @Override
    public void setFirstStop() throws SQLException {
        first_stop = d_rec.price * Double.parseDouble(getParaVal("FIRSTSTOP_CRI"));
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
                getParaVal("MIN_VOL"), getParaVal("MIN_VOLVA5"), getParaVal("MIN_KDK"), getParaVal("MIN_SC_MACD"),
                getParaVal("MIN_MFI"), getParaVal("MIN_PRSI"), oStk.padCh(beg_date), oStk.padCh(end_date)));
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
        debugPrint("test var-para min_mfi=" + getParaVal("min_mfi"));
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
        double buyPt = price * Double.parseDouble(getParaVal("BUY_CRI"));
        return overBuy(buyPt, 9999.0,getParaVal("BUY_MODE"));
    }

    static public void main(String[] args) throws SQLException {
        lvstgp sys = new lvstgp();
        if (args.length < 3) {
            System.err.println("Usage: java lvstg sys subsys dateCodes(like:A,B,C)");
            System.exit(-1);
        }
        sys.args = args;
        sys.Start();
    }
}
