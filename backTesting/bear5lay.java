
import java.sql.*;

/**
 * 大量強勢股
 *
 * @author huangtm
 */
class bear5lay extends ssdp {
    @Override
    public void initOther() {
        setFiles("bt_stat", "bear5lay", "c:/stockSd/cmd/backTesting/bear5lay.TXT");
        setTDR(dateCodes, "Y");
        setRandom(false, 0, 0);
        setIsBear(true); //false=作多
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
        first_stop = rs_det.getDouble("ma5") * Double.parseDouble(getParaVal("FIRSTSTOP_CRI"));
    }

    @Override
    public void getData() throws SQLException {
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format("select * from v_bear5layNB "
                + " where dte between %s and %s order by stockid,dte",
                 oStk.padCh(beg_date), oStk.padCh(end_date)));
        debugPrint(sql_b1.toString());
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            initStr(sql_b2);
            sql_b2.append("insert into " + tbl_det
                    + " (stockid,dte,price,ma5,datecode) values ("
                    + "'" + rs_det.getString("stockid") + "', "
                    + "'" + rs_det.getString("dte") + "', "
                    + "'" + rs_det.getString("price") + "', "
                    + "'" + rs_det.getString("ma5") + "', "
                    + "'" + date_code + "') ");
            stmt_update.executeUpdate(sql_b2.toString());
        }
        debugPrint("test var-para buy_cri=" + getParaVal("buy_cri"));
        clearVars();
        System.gc();
    }

    /**
     * 其他之進場篩選, return true 表示通過本項篩選
     */
    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        return true;
    }

    @Override
   public boolean getBuyPrice(double price) throws SQLException {
        double buyPt;
        double buy_cri = Double.parseDouble(getParaVal("BUY_CRI"));
        boolean isOk = false;

        buyPt = rs_det.getDouble("ma5") * buy_cri;
        
        if (stk_rec.price < buyPt) {
            isOk = true;
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
        bear5lay sys = new bear5lay();
        if (args.length < 3) {
            System.err.println("Usage: java bear5lay sys subsys dateCodes(like:A,B,C)");
            System.exit(-1);
        }
        sys.args = args;
        sys.Start();
    }
}
