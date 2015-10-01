
import java.sql.*;

/**
 * <h1>陰陽線回溯測試</h1>
 * usage: java candle sys subsys datecodes<br>
 * sys: "candle" 也是使用的測試明細表table的名稱<br>
 * subsys: "型態", 可以組合，用';'分隔，如："+晨星;+晨星十字"<br>
 *
 * @since 2015/5/10
 * @version 1
 * @serial 1
 * @author huangtm
 */
class candle extends ssdp {

    final int ACS_DATANUM = 11;
    final int ACS_POPEN = 0;
    final int ACS_PHIGH = 1;
    final int ACS_PLOW = 2;
    final int ACS_PPRICE = 3;
    final int ACS_SC_MA10 = 4;
    final int ACS_VOL = 5;
    final int ACS_VA10 = 6;
    final int ACS_KDK = 7;
    final int ACS_KDD = 8;
    final int ACS_RSI6 = 9;
    final int ACS_RSI12 = 10;

    String lastId = "";
    java.util.Date lastDte = new java.util.Date(0);
    java.util.Date[] skipDate = new java.util.Date[200];
    int skipDateRecs = 0;

    double acs[][] = new double[CandleStick.MAX_NUM][ACS_DATANUM];

    @Override
    public void initOther() {
        setFiles("bt_stat", "candle", "c:/stockSd/cmd/backTesting/candle.txt");
        setTDR(dateCodes, "Y");
        setRandom(false, 0, 0);
        setDayInterval(5); // 設定下一筆需間隔幾天才不算重複
        setMaxUp(-1, 20); // 設定幾天內最大漲福，1為當天,-1時表示不檢查
        is_debug = true;
        setForceEnabled(true); // true=要依大盤出場
        setRate(oStk.cost_tax, oStk.cost_op);

        if (subsys.substring(0, 1).equals("-")) {
            setIsBear(true);
        } else {
            setIsBear(false);
        }
        System.out.println("subsys=" + subsys + ",is_bear = " + is_bear);

    }

    @Override
    public void getData() throws SQLException {
        ResultSet rs;
        Statement stmt;
        int aRecs = 0, i;
        String ktypes, aa[];
        String s2;
        String kdd_cri, mfi_cri, rsi6_cri, ptb_cri;
        String lt_gt;
        boolean isSkip;
        java.util.Date dte;

        stmt = conn.createStatement();
        stmt_det = conn.createStatement();
        stmt_update = conn.createStatement();

        /* --- 當日該型態的筆數 > getParaVal("RECS_SKIP] 存入 skipDate[] 
         隨後初步篩選時，型態發生日期如果在skipDate[]中的會剔除掉---
         --------------------------------------*/
        aa = subsys.split(";");
        ktypes = "";
        for (i = 0; i < aa.length; i++) {
            aa[i] = aa[i].replace('@', '+'); //如將"@槌子"->"+槌子"
            ktypes += ",'" + aa[i] + "'";
        }
        ktypes = ktypes.substring(1);
        debugPrint(ktypes, true);

        initStr(sql_b1);
        sql_b1.append("select dte from v_cs_recs where "
                + "kType in (" + ktypes + ") and cnt > " + getParaVal("RECS_SKIP"));
        debugPrint(sql_b1.toString());
        rs = stmt.executeQuery(sql_b1.toString());
        i = 0;
        while (rs.next()) {
            skipDate[i++] = rs.getDate("dte");
            if (i >= skipDate.length) {
                break;
            }
        }
        skipDateRecs = i;
        debugPrint("SkipDateRecs=" + skipDateRecs, true);
        //---
        initStr(sql_b1);
        //sql_b1.append("delete from " + tbl_det + " where datecode='" + date_code + "'");
        sql_b1.append("delete from " + tbl_det);
        stmt_update.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format(
                "select * from v_candle where dte between %s and %s and percentb is not null ",
                oStk.padCh(beg_date), oStk.padCh(end_date)));

        kdd_cri = getParaVal("KDD_CRI");
        mfi_cri = getParaVal("MFI_CRI");
        rsi6_cri = getParaVal("RSI6_CRI");
        ptb_cri = getParaVal("PTB_CRI");
        if (!is_bear) // 作多
        {
            lt_gt = " < ";
        } else {
            lt_gt = " > ";
        }
        s2 = "";
        if (!kdd_cri.equals("-1")) {
            s2 += " or kdd" + lt_gt + kdd_cri;
        }
        if (!mfi_cri.equals("-1")) {
            s2 += " or mfi" + lt_gt + mfi_cri ;
        }
        if (!rsi6_cri.equals("-1")) {
            s2 += " or rsi6" + lt_gt + rsi6_cri;
        }
        if (!ptb_cri.equals("-1")) {
            s2 += " or percentb" + lt_gt + ptb_cri;
        }
        if (!"".equals(s2)) {
            sql_b1.append(" and (" + s2.substring(3) + ") ");
        }
        sql_b1.append("and ktype in (" + ktypes + ")");
        sql_b1.append(" and vol > va10 * " +  getParaVal("VOL_CRI"));
        
        if (ktypes.contains("上升三法")) {
            sql_b1.append(" and mfi > " +  getParaVal("GT_MFI"));
            sql_b1.append(" and rsi6 > " +  getParaVal("GT_RSI"));
        }
        
        sql_b1.append(" order by stockid,dte");
        debugPrint(sql_b1.toString());
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (rs_det.next()) {
            // --- skip if date in skiDateRecs[]
            isSkip = false;
            dte = rs_det.getDate("dte");
            for (int dx = 0; dx < skipDateRecs; dx++) {
                if (dte.equals(skipDate[dx])) {
                    isSkip = true;
                    break;
                }
            }
            if (!isSkip) {
                initStr(sql_b2);
                sql_b2.append("insert into " + tbl_det
                        + " (stockid,dte,price,datecode, ktype,p_open,p_high,p_low,percentb,bandwidth) values ("
                        + "'" + rs_det.getString("stockid") + "', "
                        + "'" + rs_det.getString("dte") + "', "
                        + "'" + rs_det.getString("price") + "', "
                        + "'" + date_code + "', "
                        + "'" + rs_det.getString("ktype") + "', "
                        + "'" + rs_det.getString("p_open") + "', "
                        + "'" + rs_det.getString("p_high") + "', "
                        + "'" + rs_det.getString("p_low") + "', "
                        + "'" + rs_det.getString("percentb") + "', "
                        + "'" + rs_det.getString("bandwidth")
                        + "') ");
                //System.out.println(sql_b2.toString());
                stmt_update.executeUpdate(sql_b2.toString());
            }
        }
        debugPrint(sql_b2.toString()); // check 最後一筆有無在資料庫中？
        clearVars();
        System.gc();
    }

    @Override
    public void setFirstStop() throws SQLException {
        String kType;
        double p_open, p_high, p_low, price;
        double firstStopCri = Double.parseDouble(getParaVal("FIRSTSTOP_CRI"));
        double midHL;

        /**
         * setFirstStop()在setBuy()與setSale()兩程序中各執行一次 setBuy()進場篩選時 d_rec.pBuy
         * 還是 0 因為candleStick都是在隔天(IN_DAYS==1)就判斷是否買進， 所以沒有必要判斷是否已觸及停損價位而停止追蹤買進
         * 以下判斷可以大量節省 setArrayCS() 讀取前數天價格資料的時間
         */
        if (d_rec.pBuy == 0d) {
            if (is_bear) {
                first_stop = 9999d; //作空時停損價位，n > 9999指永遠不會停損
            } else {
                first_stop = 0d; //作多時停損價位，n < 0 指永遠不會停損
            }
            return;
        }
        kType = rs_det.getString("KTYPE");
        p_open = rs_det.getDouble("p_open");
        p_high = rs_det.getDouble("p_high");
        p_low = rs_det.getDouble("p_low");
        price = rs_det.getDouble("price");
        midHL = (p_high + p_low) / 2;

        setArrayCS(d_rec.stockId, d_rec.dte);

        if (!is_bear) //作多
        {
            if (kType.contains("多頭母子")) {
                first_stop = p_open * firstStopCri;
            } else if (kType.contains("吞噬") ) {
                first_stop = price * firstStopCri;
            } else if (kType.contains("外側三日上升")) {
                first_stop = p_low * firstStopCri;
            } else if (kType.contains("內困三日翻紅")) {
                first_stop = acs[1][ACS_POPEN] * firstStopCri;
            } else if (kType.contains("物極必反")) {
                first_stop = price * firstStopCri;
            } else if (kType.contains("單白兵")|| kType.contains("上升三法")) {
                first_stop = p_low * firstStopCri;
            } else if (kType.contains("槌子")) {
                first_stop = p_low * firstStopCri;
            } else if (kType.contains("晨星")) {
                first_stop = acs[1][ACS_PLOW] * firstStopCri;
            } else if (kType.contains("貫穿線")) {
                first_stop = price * firstStopCri;
            } else {
                first_stop = 9999d; //此會導致立刻停損，因為收盤一定<9999 
            }
        } else {//作空

            if (kType.contains("空頭母子")) {
                first_stop = p_open * firstStopCri;
            } else if (kType.contains("吞噬")) {
                first_stop = p_high * firstStopCri;
            } else if (kType.contains("外側三日下降")) {
                first_stop = acs[1][ACS_PHIGH] * firstStopCri;
            } else if (kType.contains("內困三日翻黑")) {
                first_stop = acs[1][ACS_PPRICE] * firstStopCri;
            } else if (kType.contains("物極必反")) {
                first_stop = p_high;
            } else if (kType.contains("單黑鴉")) {
                first_stop = p_high * firstStopCri;
            } else if (kType.contains("吊人")) {
                first_stop = p_high * firstStopCri;
            } else if (kType.contains("流星")) {
                first_stop = p_high * firstStopCri;
            } else if (kType.contains("夜星")) {
                first_stop = acs[1][ACS_PHIGH] * firstStopCri; //夜星第2根的高點
            } else if (kType.contains("烏雲罩頂")) {
                first_stop = price * firstStopCri;
            } else {
                first_stop = 0d;  //此會導致立刻停損，因為收盤一定>0
            }
        }
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
        double buyCri = Double.parseDouble(getParaVal("BUY_CRI"));
        double volCri = Double.parseDouble(getParaVal("VOL_CRI"));
        String kd_rsi = getParaVal("KD_RSI");
        String buy_mode = getParaVal("BUY_MODE");
        double buyPt = price * buyCri, buyPt2 = 0d;
        double p_low, p_high, p_open, kdd, ptbrsi6;
        String kType;
        double risk = 0d;
        boolean isOpenBuy = false;
        double firstStopCri = Double.parseDouble(getParaVal("FIRSTSTOP_CRI"));
        int px = 0;

        //debugPrint(d_rec.stockId + d_rec.dte.toString(),true);
        setArrayCS(d_rec.stockId, d_rec.dte);

        kType = rs_det.getString("ktype");
        p_open = rs_det.getDouble("p_open");
        p_low = rs_det.getDouble("p_low");
        p_high = rs_det.getDouble("p_high");

        // KD,RSI的過濾
        if (is_bear) {//空
            if (kd_rsi.equals("OR")) {
                if (!(isDeadCross("KD", 0) || isDeadCross("RSI", 0))) {
                    return false;
                }
            } else if (kd_rsi.equals("AND")) {
                if (!(isDeadCross("KD", 0) && isDeadCross("RSI", 0))) {
                    return false;
                }
            } else if (kd_rsi.equals("KD")) {
                if (!isDeadCross("KD", 0)) {
                    return false;
                }
            } else if (kd_rsi.equals("RSI")) {
                if (!isDeadCross("RSI", 0)) {
                    return false;
                }
            }

        } else {//多方
            if (kd_rsi.equals("OR")) {
                if (!(isGoldenCross("KD", 0) || isGoldenCross("RSI", 0))) {
                    return false;
                }
            } else if (kd_rsi.equals("AND")) {
                if (!(isGoldenCross("KD", 0) && isGoldenCross("RSI", 0))) {
                    return false;
                }
            } else if (kd_rsi.equals("KD")) {
                if (!isGoldenCross("KD", 0)) {
                    return false;
                }
            } else if (kd_rsi.equals("RSI")) {
                if (!isGoldenCross("RSI", 0)) {
                    return false;
                }
            }
        }
        // 過濾成交量(不是選股日的成交量)
        if (kType.contains("外側三日上升")) {
            px = 1;
            if (acs[px][ACS_VOL] < acs[px][ACS_VA10] * volCri) {
                return false;
            }
        }
        isOpenBuy = false;
        if (buy_mode.equals("開盤")) {
            isOpenBuy = true;
        } else if (buy_mode.equals("跳空")) {
            if (is_bear) //作空
            {
                if (stk_rec.p_open < price * 0.99) {
                    isOpenBuy = true;
                }
            } else {
                if (stk_rec.p_open > price * 1.01) {
                    isOpenBuy = true;
                }
            }
        }
        if (isOpenBuy) {
            buy_price = stk_rec.p_open;
            buy_date = stk_rec.dte;
            is_close = false;
            return true;
        }
        if (is_bear) {
            if (kType.contains("-夜星")){
                buyPt = acs[1][ACS_PPRICE] * buyCri;
            }
            if (stk_rec.price < buyPt) {
                buy_price = stk_rec.price;
                buy_date = stk_rec.dte;
                is_close = true;
                return true;
            } else {
                return false;
            }
        } else {//作多
            return overBuy(buyPt, 9999.0, buy_mode);
        }
    }

    void setArrayCS(String stockId, java.util.Date dte) throws SQLException {
        String sDteCurr, sDtePrev;
        StringBuilder sql = new StringBuilder(300);
        Statement stmt;
        ResultSet rs;
        int cnt;

        if (stockId.equals(lastId) && dte.equals(lastDte)) {
            return;
        } else {
            lastId = stockId;
            lastDte = dte;
        }
        for (int i = 0; i < CandleStick.MAX_NUM; i++) {
            for (int j = 0; j < ACS_DATANUM; j++) {
                acs[i][j] = -1d;
            }
        }
        sDteCurr = oStk.dateToStr(dte);
        sDtePrev = oStk.getPrevStockDate(dte, 4);
        initStr(sql);
        sql.append(String.format(
                "select * from stk "
                + "where stockid='%s'  and dte between %s and %s order by dte desc",
                stockId, oStk.padCh(sDtePrev), oStk.padCh(sDteCurr)));
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql.toString());
        cnt = 0;
        while (rs.next()) {
            acs[cnt][ACS_POPEN] = rs.getDouble("p_open");
            acs[cnt][ACS_PHIGH] = rs.getDouble("p_high");
            acs[cnt][ACS_PLOW] = rs.getDouble("p_low");
            acs[cnt][ACS_PPRICE] = rs.getDouble("price");
            acs[cnt][ACS_SC_MA10] = rs.getDouble("sc_ma10");
            acs[cnt][ACS_VOL] = rs.getDouble("vol");
            acs[cnt][ACS_VA10] = rs.getDouble("va10");
            acs[cnt][ACS_KDK] = rs.getDouble("kdk");
            acs[cnt][ACS_KDD] = rs.getDouble("kdd");
            acs[cnt][ACS_RSI6] = rs.getDouble("rsi6");
            acs[cnt][ACS_RSI12] = rs.getDouble("rsi12");
            cnt++;
        }

    }

    boolean isGoldenCross(String sIdx, int lastDay) {
        boolean isOk = false;
        if (sIdx.equals("KD")) {
            if (acs[lastDay][ACS_KDK] > acs[lastDay][ACS_KDD]
                    && acs[lastDay][ACS_KDK] > acs[lastDay + 1][ACS_KDK]
                    && acs[lastDay][ACS_KDD] > acs[lastDay + 1][ACS_KDD]) {
                isOk = true;
            }
        } else if (sIdx.equals("RSI")) {
            if (acs[lastDay][ACS_RSI6] > acs[lastDay][ACS_RSI12]
                    && acs[lastDay][ACS_RSI6] > acs[lastDay + 1][ACS_RSI6]
                    && acs[lastDay][ACS_RSI12] > acs[lastDay + 1][ACS_RSI12]) {
                isOk = true;
            }
        }
        return isOk;
    }

    boolean isDeadCross(String sIdx, int lastDay) {
        boolean isOk = false;
        if (sIdx.equals("KD")) {
            if (acs[lastDay][ACS_KDK] < acs[lastDay][ACS_KDD]
                    && acs[lastDay][ACS_KDK] < acs[lastDay + 1][ACS_KDK]
                    && acs[lastDay][ACS_KDD] < acs[lastDay + 1][ACS_KDD]) {
                isOk = true;
            }
        } else if (sIdx.equals("RSI")) {
            if (acs[lastDay][ACS_RSI6] < acs[lastDay][ACS_RSI12]
                    && acs[lastDay][ACS_RSI6] < acs[lastDay + 1][ACS_RSI6]
                    && acs[lastDay][ACS_RSI12] < acs[lastDay + 1][ACS_RSI12]) {
                isOk = true;
            }
        }
        return isOk;
    }

    static public void main(String[] args) throws SQLException {
        candle sys = new candle();
        if (args.length < 3) {
            System.err.println("Usage: java lvstg sys subsys dateCodes(like:A,B,C)");
            System.exit(-1);
        }
        sys.args = args;
        sys.Start();
    }
}
