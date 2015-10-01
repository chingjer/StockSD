
import java.sql.*;
class farmer extends ssd {

    @Override
    public void setSys() {
        sys = "lvstg";
        subsys = "";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "farmer", "c:/stockSd/cmd/backTesting/farmer.TXT");
        /* 隨機模式 
         setTDR("A,B,D,E,F","Y"); //setTDR( "A,B","Y") 將 datacode A,B兩者設為Y
         setRandom(true, 30, 30); 
         setForceEnabled(true); // true=要依大盤出場

         //setIsDelStat( false ) ; // 設定不先清除統計檔(該日期代號)
         */

        /* 選股模式 */
        setTDR("R", "Y"); //setTDR( "A,B","Y") 將 datacode A,B兩者設為Y
        setRandom(false, 0, 0);
        setForceEnabled(false); // true=要依大盤出場

        setIsBear(false); //false=作多
        setDayInterval(-1); // 設定下一筆需間隔幾天才不算重複
        setMaxUp(-1, 20); // 設定幾天內最大漲福，1為當天,-1時表示不檢查
        is_debug = true;
        setRate(oStk.cost_tax, oStk.cost_op);
    }

    @Override
    public void setParm() {
        para_grp = new String[]{
            "99, 0.05, 0.03, 0, 3, 0.85, 20, 1, 尾盤, 30, 20, 20, ma5, 0.96"
        };
    }

     @Override
     public void printPara() {
        String s1 = new String(getFixParaSpec());
        debugPrint(s1);
    }

    @Override
    public void setFirstStop() throws SQLException {
        if (d_rec.pBuy != 0) {
            first_stop = d_rec.pBuy * Double.parseDouble(para[FIRSTSTOP_CRI]);
        } else {
            first_stop = 0d; //作多時設0, 作空時設9999, 表示永遠不會觸及
        }
    }

    @Override
    public void getData() throws SQLException {
        ResultSet rs;
        Statement stmt;
        int aRecs = 0;

        stmt = conn.createStatement();
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();

        if (!isRandom()) {
            initStr(sql_b1);
            sql_b1.append("delete from randsel");
            stmt_update.executeUpdate(sql_b1.toString());
            initStr(sql_b1);
            //sql_b1.append("select stockid from [2008價值投資績效]");
            //sql_b1.append("select stockid from [1001211反向]");
            //sql_b1.append("select stockid from [QRY-101虧轉盈]");
            sql_b1.append("select stockid from [goodstock]");
            rs = stmt.executeQuery(sql_b1.toString());
            while (rs.next()) {
                initStr(sql_b1);
                sql_b1.append(String.format("insert into [randsel] (stockid) values('%s')",
                        rs.getString("stockid")));
                aRecs = stmt_update.executeUpdate(sql_b1.toString());
                if (aRecs == 0) {
                    debugPrint("Insert Error!SQL=" + sql_b1.toString(), true);
                }
            }

        }
        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());

        // 因 insert into ... select from 無法使用，改成一筆一筆 insert
        initStr(sql_b1);
        sql_b1.append("select * from [QRY-牛眼] where"
                + " dte =#" + beg_date
                + "# order by stockid,dte"); //注意沒有 end_date
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

    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        /**
         * 其他之進場篩選, return true 表示通過本項篩選
         */
        return true;
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double buyPt = price - 1.0;
        return overBuy(buyPt, 9999.0,para[BUY_MODE]);
    }

    static public void main(String[] args) throws SQLException {
        farmer sys = new farmer();
        sys.Start();
    }
}
