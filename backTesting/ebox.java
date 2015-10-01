
import java.sql.*;

class ebox extends ssd {
    /* -- 自訂的參數 --- */

    final int MAX_DIST_BT = 14;
    final int VA5_MULT = 15;
    final int MIN_PRSI6 = 16;
    final int BUY_MIN = 17;
    final int BUY_MAX = 18;

    @Override
    public void setSys() {
        sys = "ebox";
        subsys = "";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "ebox", "c:/stockSd/cmd/backTesting/ebox.TXT");
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
            "1,0.05,0.03,0,3,0.96,-1,5, 尾盤,5, 14, 14, ma5, 0.96,6, 3, 0.8, 1, 1.06",
            "2,0.05,0.03,0,3,0.96,-1,5, 尾盤,5, 14, 14, ma5, 0.96,6, 3, 0.8, 1, 1.06",
            "6,0.05,0.03,0,3,0.96,-1,5, 尾盤,5, 14, 14, ma5, 0.96,6, 3, 0.8, 1, 1.06"
        };
    }

    @Override
    public void printPara() {
        String s1 = new String(getFixParaSpec());
        s1 += "\r\n(14)MAX_DIST_BT=" + para[MAX_DIST_BT];
        s1 += "\t(15)VA5_MULT=" + para[VA5_MULT];
        s1 += "\t(16)MIN_PRSI6=" + para[MIN_PRSI6];
        s1 += "\t(17)BUY_MIN=" + para[BUY_MIN];
        s1 += "\r\n(18)BUY_MAX=" + para[BUY_MAX];

        debugPrint(s1);

    }

    @Override
    public void setFirstStop() throws SQLException {
        double bt = rs_det.getDouble("bt"); // box_top
        //first_stop = bt * Double.parseDouble(para[FIRSTSTOP_CRI]);
        
        if (d_rec.pBuy != 0) {
            first_stop = bt * Double.parseDouble(para[FIRSTSTOP_CRI]);
        } else {
            first_stop = 0d; //作多時設0, 作空時設9999, 表示永遠不會觸及
        }
        
    }

    @Override
    public void getData() throws SQLException {
        String stockId;
        java.util.Date dte;
        double price, bt;

        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();

        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());
        initStr(sql_b1);
        sql_b1.append( "select e.* from box e inner join stk s on e.stockid=s.stockid and e.dte = s.dte "
                + "where e.vol > e.va5 * " + para[VA5_MULT]
                + " and s.ptbrsi6 >= " + para[MIN_PRSI6]
                + " and e.diff_bt <= " + para[MAX_DIST_BT]
                + " and e.dte between " + oStk.padCh(beg_date) + " and " + oStk.padCh(end_date)
                + " order by e.stockid,e.dte");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            stockId = rs_det.getString("stockid");
            dte = rs_det.getDate("dte");
            price = rs_det.getDouble("price");
            bt = rs_det.getDouble("highprice");
            initStr(sql_b2);
            sql_b2.append( "insert into " + tbl_det
                    + " (stockid,dte,price,datecode,BT) values ("
                    + "'" + stockId + "', "
                    + "'" + dte + "', "
                    + "" + price + ", "
                    + "'" + date_code + "',"
                    + bt + ") ");
            stmt_update.executeUpdate(sql_b2.toString());
        } // while
        clearVars();
        System.gc();
    }

    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        /**
         * 其他之進場篩選, return true 表示通過本項篩選
         */
        if (date_code.equals("C") && "5410;5455".indexOf(d_rec.stockId) != -1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double buyPt, buyPtMax;
        double bt = rs_det.getDouble("bt"); // box_top // box_top
        buyPt = bt * Double.parseDouble(para[BUY_MIN]);
        buyPtMax = bt * Double.parseDouble(para[BUY_MAX]);
        return overBuy(buyPt, buyPtMax,para[BUY_MODE]);
    }

    static public void main(String[] args) throws SQLException {
        ebox sys = new ebox();
        sys.Start();
    }
}
