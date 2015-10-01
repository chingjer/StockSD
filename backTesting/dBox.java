
import java.sql.*;
class dBox extends ssd {
    final int BUY_CRI = 14;
    final int VA5_CRI = 15;

    @Override
    public void setSys() {
        sys = "dbox";
        subsys = "";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "dbox", "c:/stockSd/cmd/backTesting/dbox.TXT");
        //setTDR("3,5,G,H,I,J,K", "Y");
        setTDR("M", "Y");
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
            "1, 0.05, 0.03, 0, 3, 1.03,  99,  3, 收盤, 3, 14, 14, ma5, 1.04, 0.98, 500"
        };
    }

    @Override
    public void printPara() {
        String s1 = new String(getFixParaSpec());
        s1 = s1 + "\r\n(14)BUY_CRI=" + para[BUY_CRI];
        s1 = s1 + "\t(15)VA5_CRI=" + para[VA5_CRI];

        debugPrint(s1);
    }

    @Override
    public void setFirstStop() throws SQLException {
        double bot;
        bot = rs_det.getDouble("bot");
        if (d_rec.pBuy != 0) {
            first_stop = bot * Double.parseDouble(para[FIRSTSTOP_CRI]);
        } else {
            first_stop = 9999d; //作多時設0, 作空時設9999, 表示永遠不會觸及
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
        sql_b1.append( String.format( "select * from dBoxdata where"
                + " dte between %s and %s and  va5 > %s order by stockid,dte",
                oStk.padCh(beg_date),oStk.padCh(end_date), para[VA5_CRI]));
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            initStr(sql_b2);
            sql_b2.append("insert into " + tbl_det
                    + " (stockid,dte,price,bot,datecode) values ("
                    + "'" + rs_det.getString("stockid") + "', "
                    + "'" + rs_det.getString("dte") + "', "
                    + "'" + rs_det.getString("price") + "', "
                    + "'" + rs_det.getString("low2price") + "', "
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
        double bot, buyPt, upDown;
        double buy_cri = Double.parseDouble(para[BUY_CRI]);
        boolean isOk = false;

        bot = rs_det.getDouble("bot");
        buyPt = bot * buy_cri;
        upDown = stk_rec.updown;

        if (para[BUY_MODE].equals("收紅")) {
            if (stk_rec.price < buyPt && upDown >= 0) {
                isOk = true;
            }
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
        dBox sys = new dBox();
        sys.Start();
    }
}
