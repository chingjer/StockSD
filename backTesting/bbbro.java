
import java.sql.*;

class bbbro extends ssd {

    final int VA10_VOL = 14;
    final int OSC_UPDAYS = 15;
    final int PTB_CRI = 16;
    final int MFI_CRI = 17;
    final int PTBRSI12_CRI = 18;
    final int VA5_VA5 = 19;
    final int UP_CRI = 20;
    final int BUY_CRI = 21;

    @Override
    public void setSys(){
       sys = "bbbro";
       subsys = "";
    }
    @Override
    public void initOther() {
        setFiles("bt_stat", "bbbro", "c:/stockSd/cmd/backTesting/bbbro.TXT");
        setTDR("A,B,C,D,E,F", "Y");
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
            "2,0.05,0.03,0,3,0.98,-1,1, 尾盤,5, 14, 14, ma5, 0.96,3,1,0.8,60,0.7,3,4,1.04"
        };
    }

    @Override
    public void printPara() {
        String s1 = getFixParaSpec();
        s1 += "\r\n(14)VA10_VOL=" + para[VA10_VOL];
        s1 += "\t(15)OSC_UPDAYS=" + para[OSC_UPDAYS];
        s1 += "\t(16)PTB_CRI=" + para[PTB_CRI];
        s1 += "\t(17)MFI_CRI=" + para[MFI_CRI];
        s1 += "\r\n(18)PTBRSI12_CRI=" + para[PTBRSI12_CRI];
        s1 += "\t(19)VA5_VA5=" + para[VA5_VA5];
        s1 += "\t(20)UP_CRI=" + para[UP_CRI];
        s1 += "\t(21)BUY_CRI=" + para[BUY_CRI];

        debugPrint(s1);

    }

    @Override
    public void setFirstStop() throws SQLException {
        first_stop = d_rec.price * Double.parseDouble(para[FIRSTSTOP_CRI]);
    }

    @Override
    public void getData() throws SQLException {
        String dtePrev, sql, stockId;
        Statement stmt;
        ResultSet rs;
        java.util.Date dte;
        double va5, price;
        double va5_va5;

        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();
        stmt = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        stmt_update.executeUpdate(sql_b1.toString());

        va5_va5 = Double.parseDouble(para[VA5_VA5]);

        initStr(sql_b1);
        sql_b1.append("select * from squeeze where"
                + " vol >= va10 * " + para[VA10_VOL]
                + " and sc_osc > " + para[OSC_UPDAYS]
                + " and percentb >= " + para[PTB_CRI]
                + " and mfi >= " + para[MFI_CRI]
                + " and ptbrsi12 >= " + para[PTBRSI12_CRI]
                + " and updown >= " + para[UP_CRI]
                + " and dte between " + oStk.padCh(beg_date) + " and " + oStk.padCh(end_date)
                + " order by stockid,dte");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            // 檢查 va5 是否大於5日前的 va5 (5日均量)
            stockId = rs_det.getString("stockid");
            dte = rs_det.getDate("dte");
            va5 = rs_det.getDouble("va5");
            price = rs_det.getDouble("price");

            dtePrev = oStk.getPrevStockDate(dte, 6);
            sql = "select va5 from stk where stockid='"
                    + stockId + "' and dte = " + oStk.padCh(dtePrev);
            rs = stmt.executeQuery(sql);
            initStr(sql_b2);
            if (rs.next()) {
                if (va5 >= rs.getDouble("va5") * va5_va5) {
                    sql_b2.append("insert into " + tbl_det
                            + " (stockid,dte,price,datecode) values ("
                            + "'" + stockId + "', "
                            + "" + oStk.dateToStrCh(dte) + ", "
                            + "" + price + ", "
                            + "'" + date_code + "') ");
                    stmt_update.executeUpdate(sql_b2.toString());
                }
            }
        } // while
        debugPrint(sql_b2.toString()); // check 最後一筆有無在資料庫中？
        clearVars();
    }

    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        if (date_code.equals("C") && "5410;5455".contains(d_rec.stockId)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double buyPt = price * Double.parseDouble(para[BUY_CRI]);
        return overBuy(buyPt, 9999.0,para[BUY_MODE]);
    }

    static public void main(String[] args) throws SQLException {
        bbbro sys = new bbbro();
        sys.Start();
    }
}
