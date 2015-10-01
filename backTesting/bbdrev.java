
import java.sql.*;

/**
 * BB反轉系統
 *
 * @author huangtm
 */
class bbdrev extends ssd {
    /* -- 自訂的參數 --- */

    final int MIN_PTB = 14;
    final int BUY_CRI = 15;

    @Override
    public void setSys() {
        sys = "bbdrev";
        subsys = "1";
    }

    @Override
    public void initOther() {
        setFiles("bt_stat", "bbdrev", "c:/stockSd/cmd/backTesting/bbdrev.TXT");
        setTDR("A,B,C,D,E,F,G,W", "Y");
        //setTDR("G,E", "Y");
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
            "1,0.05,0.03,0,5,1.03,-1,5,背離,5, 14, 14, ma5, 1.04, 0.90, 0.97"
        };
        //    "1,0.05,0.03,0,5,1.03,-1,5,背離,5, 14, 14, ma5, 1.04, 0.90, 0.97",
    }

    @Override
    public void printPara() {
        String s1 = getFixParaSpec();
        s1 += "\t(14)MIN_PTB=" + para[MIN_PTB];
        s1 += "\r\n(15)BUY_CRI=" + para[BUY_CRI];
        debugPrint(s1);
    }

    /**
     * 設定初始停損點
     */
    @Override
    public void setFirstStop() throws SQLException {
        //first_stop = d_rec.price * Double.parseDouble(para[FIRSTSTOP_CRI]);
        if (d_rec.pBuy != 0) {
            first_stop = d_rec.pBuy * Double.parseDouble(para[FIRSTSTOP_CRI]);
        } else {
            first_stop = is_bear?9999d:0d; //作多時設0, 作空時設9999, 表示永遠不會觸及
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
        sql_b1.append(String.format("select * from stk where percentb > %s"
                + " and ma20 > 5 and vol > 300"
                + " and rsi6 < rsi12"
                + " and dte between %s and %s order by stockid,dte",
                para[MIN_PTB], 
                oStk.padCh(beg_date), oStk.padCh(end_date)));
        //        + " and  sc_osc >= %s AND kdk > kdd and sc_kdk > 0 and sc_kdd > 0 and rsi6 > rsi12"
        //        + " and sc_osc >= %s AND rsi6 > rsi12"
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            initStr(sql_b2);
            sql_b2.append("insert into " + tbl_det
                    + " (stockid,dte,price,percentb,datecode) values ("
                    + "'" + rs_det.getString("stockid") + "', "
                    + "'" + rs_det.getString("dte") + "', "
                    + "'" + rs_det.getString("price") + "', "
                    + "'" + rs_det.getString("percentb") + "', "
                    + "'" + date_code + "') ");
            stmt_update.executeUpdate(sql_b2.toString());
        }
        debugPrint(sql_b2.toString()); // check 最後一筆有無在資料庫中？
        clearVars();
        System.gc();
    }

    @Override
    public boolean getBuyPrice(double price) throws SQLException {
        double min_ptb = Double.parseDouble(para[MIN_PTB]);
        if (para[BUY_MODE].equals("背離")) {
            if (rs_stk.getDouble("price") >= d_rec.price 
                    && rs_stk.getDouble("percentb") <= rs_det.getDouble("percentb")){
                debugPrint(
                    String.format("stockid=%s, dte=%s,背離stk.price=%.2f,%%b=%.2f d_rec.price=%.2f, %%b=%.2f",
                    rs_stk.getString("stockid"),rs_stk.getString("dte"),
                    rs_stk.getDouble("price"),rs_stk.getDouble("percentb"),
                    d_rec.price, rs_det.getDouble("percentb") ));
                if (stk_rec.moveNext(rs_stk)){
                    buy_price = stk_rec.p_open;
                    buy_date = stk_rec.dte;
                    is_close = false;
                    return true;
                }
            }
        }else if (para[BUY_MODE].equals("開盤")) {
                buy_price = stk_rec.p_open;
                buy_date = stk_rec.dte;
                is_close = false;
                return true;
       
        }else if (para[BUY_MODE].equals("尾盤")){
            double buyCri = Double.parseDouble(para[BUY_CRI]);
            if (stk_rec.price < d_rec.price * buyCri){
                buy_price = stk_rec.price;
                buy_date = stk_rec.dte;
                is_close = true;
                return true;
            }
        }
        return false;
        /*
        double buyPt = price * Double.parseDouble(para[BUY_CRI]);
        boolean isOk = false;
        if (stk_rec.price < buyPt){
            buy_price = stk_rec.price;
            isOk = true;
        } 
        if (isOk){
            buy_date = stk_rec.dte;
            is_close = true; 
            return true;
        }
        return isOk;
       */
        /* 尾盤
        double buyPt = price * Double.parseDouble(para[BUY_CRI]);
        return overBuy(buyPt, -9999.0);
        */
        /* 尾盤 + 成交量
        double buyCri = Double.parseDouble(para[BUY_CRI]);
        
        double minVolVa10 = Double.parseDouble(para[MIN_VOLVA10]);
        String sql;
        ResultSet rs;
        sql = String.format("select vol,va10,updown,price from stk where stockid='%s' and dte=%s",
                stk_rec.stockId,oStk.dateToStrCh(stk_rec.dte));
        rs = oStk.stmt.executeQuery(sql);
        if (rs.next()){
            if (rs.getDouble("price") >= d_rec.price * buyCri &&
                    rs.getDouble("vol") >= rs.getDouble("va10") * minVolVa10) {
                buy_price = stk_rec.price;
                buy_date = stk_rec.dte;
                is_close = true;
                return true;
            }
        }
        return false;
        */
    }

    static public void main(String[] args) throws SQLException {
        bbdrev sys = new bbdrev();
        sys.Start();
    }
}
