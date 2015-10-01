
import java.sql.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.io.*;

abstract class ssd implements StockTest {
    final double MAX_UPDOWN = 7.0;
    
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
    double acs[][] = new double[CandleStick.MAX_NUM][ACS_DATANUM];
    String acs_lastId = "";
    java.util.Date acs_lastDte = new java.util.Date(0);   


    StkDb oStk;
    Connection conn;

    protected String sys;
    protected String subsys;

    protected ResultSet rs_det, rs_stk; //rs_det:測試明細擋, rs_stk:股票主擋
    protected Statement stmt_det, stmt_stk, stmt_update;

    protected StringBuilder sql_b1 = new StringBuilder(300);
    protected StringBuilder sql_b2 = new StringBuilder(300);
    protected StringBuilder sql_b3 = new StringBuilder(300);
    protected StringBuilder strb1 = new StringBuilder(300);

    protected double buy_price; // 進場篩選後得出的買進價格
    protected java.util.Date buy_date; // 進場篩選後得出的買進日期
    protected boolean is_close; //是否是在尾盤買進
    protected BufferedWriter f_out;

    protected boolean is_debug = true; //是否列印加減碼進出詳細過程
    private boolean is_random = false; //是否使用隨機選股
    protected boolean is_bear; // true=作空
    protected boolean forcestop_enabled; // 是否要依大盤出場

    private boolean is_del_stat = true; // 是否先清除 該 date_code 的 tbl_stat
    private int rnd_times = 30; //隨機選股時,同一參數組測試幾次(30)
    private int rnd_recs = 30; //隨機選股時，每一次測試隨機抓取幾筆資料(30)

    protected String tbl_stat, tbl_det, file_trace;
    protected String[] para_grp; // 各參數以','分隔
    private int px; // current para_grp[] index
    protected String[] para = new String[PARA_MAXNUM];
    protected String[][] date_ranges = new String[DATE_MAXNUM][4];
    protected String beg_date, end_date, date_code;
    protected double first_stop;//初始停損點
    protected double max_high; // 最高點
    protected double min_low; // 最低點
    protected double tot_profit; // 加減碼後總獲利
    protected boolean is_chg; // 是否改變成第二出場規則？
    protected String ma_fld; // 出場均線欄位名稱
    protected Double ma_stop; //均線出場點
    protected int hold_days; //持股天數
    protected int flg_clear; //出場模式
    protected DecimalFormat dec_fmt; // 數字輸出四捨五入格式
    protected double tax_rate = 0.003, op_rate = 0.001425;

    private java.util.Date[] stop_dates; //強制出場日期
    private java.util.Date[] stop_datesBear; //強制出場日期(空方)

    private int day_interval = 5; //下一筆需間隔幾天
    private int bef_days = 4; // n天前收盤大於 max_up 時就不買進(漲幅過大)
    private int max_up = 20; // 漲幅超過20%

    private int tot_sel_recs; // 初步篩選總筆數(已扣除重複者)
    private int tot_buy_recs;  // 實際買進總筆數
    private int in_days;

    protected detRecord d_rec = new detRecord();

    /**
     * rs_det 測試主檔目前紀錄值
     */
    class detRecord {

        String stockId;
        java.util.Date dte, buyDate, stopDate;
        double price, pBuy, pStop;
        boolean isClose;
        int addTimes; // 加減碼序號
        int invCnt; // 庫存
        double lastBuy; // 最後買進或出場價格

        boolean moveNext(ResultSet rs) throws SQLException {
            boolean more;
            more = rs.next();
            if (more) {
                setValues(rs);
            }
            return more;
        }

        void setValues(ResultSet rsDet) throws SQLException {
            //rsDet.refreshRow();
            stockId = rsDet.getString("stockid");
            dte = rsDet.getDate("dte");
            price = rsDet.getDouble("price");
            pBuy = rsDet.getDouble("p_buy");
            buyDate = rsDet.getDate("buydate");
            pStop = rsDet.getDouble("p_stop");
            stopDate = rsDet.getDate("stopdate");
            isClose = rsDet.getBoolean("isclose");
        }
    }
    protected stkRecord stk_rec = new stkRecord();

    /**
     * rs_stk 盤後資料檔目前紀錄值
     */
    class stkRecord {

        String stockId;
        java.util.Date dte;
        double price, p_high, p_low, p_open, updown;

        boolean moveNext(ResultSet rs) throws SQLException {
            boolean more;
            more = rs.next();
            if (more) {
                setValues(rs);
            }
            return more;
        }

        void setValues(ResultSet rsStk) throws SQLException {
            //rsStk.refreshRow();
            stockId = rsStk.getString("stockid");
            dte = rsStk.getDate("dte");
            price = rsStk.getDouble("price");
            p_high = rsStk.getDouble("p_high");
            p_low = rsStk.getDouble("p_low");
            p_open = rsStk.getDouble("p_open");
            updown = rsStk.getDouble("updown");
        }
    }

    @Override
    public void Start() {
        String sNow, s1;
        Statement stmt;
        int dx;

        try {
            setSys();
            setMyDatabase();
            setTestDateRange(); // maybe override
            setParm(); // abstract
            //dec_fmt = new DecimalFormat("#,##0.00");
            dec_fmt = new DecimalFormat("0.00");
            dec_fmt.setRoundingMode(RoundingMode.HALF_UP);
            initOther(); // may be override
            setForceStopDate(is_bear); // 設定多頭大盤出場日
            f_out = new BufferedWriter(new FileWriter(file_trace));

            sNow = oStk.dateTimeToStr(new java.util.Date());
            debugPrint("*** begin ***" + sNow, true);

            stmt = conn.createStatement();
            for (dx = 0; dx < DATE_MAXNUM; dx++) {
                if (date_ranges[dx][0].equals("")) {
                    break;
                }
                if (date_ranges[dx][DATE_ENABLED].equals("N")) {
                    continue;
                }
                date_code = date_ranges[dx][DATE_CODE];
                beg_date = date_ranges[dx][DATE_BEG];
                end_date = date_ranges[dx][DATE_END];
                if (isRandom()) {
                    crRandid();
                }
                initStr(sql_b1);
                if (isDelStat()) {
                    sql_b1.append(String.format("delete from " + tbl_stat
                            + " where sys='%s' and subsys='%s' and datecode='%s'",
                            sys, subsys, date_code));
                    stmt.executeUpdate(sql_b1.toString());
                }
                for (int j = 0; j < para_grp.length; j++) {
                    px = j; // current para_grp[] index
                    if (isRandom()) {
                        for (int i = 1; i <= rnd_times; i++) {
                            sNow = oStk.dateTimeToStr(new java.util.Date());
                            s1 = "參數 " + px + ", 期間 " + date_code
                                    + ", 第 " + i + "次, 時間:" + sNow;
                            debugPrint(s1, true);

                            test1();
                        }
                    } else {
                        sNow = oStk.dateTimeToStr(new java.util.Date());
                        s1 = "參數 " + px + ", 期間 " + date_code + ", 時間:" + sNow;
                        debugPrint(s1, true);
                        test1();
                    }
                    clearVars();
                    System.gc();
                } // for
            } // for
            sNow = oStk.dateTimeToStr(new java.util.Date());
            debugPrint("*** end ***" + sNow, true);
            System.out.println("作業完畢！測試報表為" + file_trace);
            f_out.close();

        } // try
        catch (SQLException e) {
            System.err.println(e.getMessage());
            if (sql_b1.length() > 0) {
                System.err.println("sql_b1 =" + sql_b1.toString());
            }
            if (sql_b2.length() > 0) {
                System.err.println("sql_b2 =" + sql_b2.toString());
            }
            if (sql_b3.length() > 0) {
                System.err.println("sql_b3 =" + sql_b3.toString());
            }
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected void clearVars() throws SQLException {
        stmt_det = null;
        stmt_stk = null;
        stmt_update = null;
        rs_det = null;
        rs_stk = null;
    }

    /**
     * 每一個測試單元包括：一組參數，一個期間，隨機測試30次的其中１次
     */
    @Override
    public void test1() throws SQLException {
        getOneParaGroup(px);
        printPara();
        if (isRandom()) {
            randomSelStock(rnd_recs);
        }
        getData(); //取得符合選股條件資料
        setBuy(); //設定買進資料
        if (tot_buy_recs == 0) {
            debugPrint("***** 沒有買進的股票！ *****", true);
            return;
        }
        setSale(); //設定加、減碼與賣出資料
        calcEarn(); //計算盈虧
        calcStat(); //計算統計資料
        clearVars(); //清除共用變數
    }

    /**
     * 取得固定參數的輸出字串
     *
     * @return 固定參數的輸出字串
     */
    @Override
    public String getFixParaSpec() {
        StringBuilder s1 = new StringBuilder(200);
        s1.append("(0)ADD_MAXTIMES=" + para[ADD_MAXTIMES]);
        s1.append("\t(1)ADD_CRI=" + para[ADD_CRI]);
        s1.append("\t(2)SUB_CRI=" + para[SUB_CRI]);
        s1.append("\t(3)MAX_LOSE=" + para[MAX_LOSE]);
        s1.append("\r\n(4)NUM_CHECK=" + para[NUM_CHECK]);
        s1.append("\t(5)FIRSTSTOP_CRI=" + para[FIRSTSTOP_CRI]);
        s1.append("\t(6)MAXDROP_CRI=" + para[MAXDROP_CRI]);
        s1.append("\t(7)IN_DAYS=" + para[IN_DAYS]);
        s1.append("\r\n(8)BUY_MODE=" + para[BUY_MODE]);
        s1.append("\t(9)HOLDDAYS_CRI=" + para[HOLDDAYS_CRI]);
        s1.append("\t(10)MIN_PROFIT=" + para[MIN_PROFIT]);
        s1.append("\t(11)CHG_CRI=" + para[CHG_CRI]);
        s1.append("\r\n(12)STOP_MA=" + para[STOP_MA]);
        s1.append("\t(13)FILT_MA=" + para[FILT_MA]);
        return s1.toString();
    }

    /**
     * 檢查資料是否正常，比如減資或除權息，不正常則取消買進
     */
    @Override
    public boolean chkIsNormal() {
        if (stk_rec.updown < -7 || stk_rec.updown > 7) {
            debugPrint(String.format("%s漲跌幅異常！買進取消。stk_rec.updown =%s",
                    stk_rec.stockId, stk_rec.updown));
            return false;
        }
        return true;
    }

    /**
     * 設定出場日期與賣價，包括處理加減碼
     */
    @Override
    public void setSale() throws SQLException {

        StringBuilder rndIDs = new StringBuilder(200); //隨機選股代號一覽
        int nxIDs = 0; // 隨機選股時，印出選股代號，用nxIDs計算每10筆跳行
        int days; // 持股天數
        boolean moreStk; // more recorss of stk
        int affectedRecs = 0; // SQL Update時影響的筆數

        stmt_det = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_stk = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_update = conn.createStatement();

        debugPrint("=== setSale()開始 ===");
        // 讀取已買進的股票資料
        initStr(sql_b1);
        sql_b1.append("select * from " + tbl_det + " where p_buy <> 0 and datecode='"
                + date_code + "' ORDER BY stockid,dte");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (d_rec.moveNext(rs_det)) {
            d_rec.lastBuy = d_rec.pBuy;

            //if (is_debug) 
            debugPrint(String.format("%s -- %tF,buydate=%tF, pBuy=%.2f",
                    d_rec.stockId, d_rec.dte, d_rec.buyDate, d_rec.pBuy));

            // 將隨機選取的股票代號每10個一行存到IDs
            if (isRandom()) {
                nxIDs++;
                rndIDs.append(d_rec.stockId + ", ");
                if ((nxIDs + 1) % 10 == 1 && nxIDs != rnd_recs) {
                    rndIDs.append("\r\n");
                }
            }

            setFirstStop(); // 設定初始停損點

            // 讀取買進日以後該股票盤後資料(in stk)
            initStr(sql_b2);
            sql_b2.append("select * from stk where stockid='" + d_rec.stockId
                    + "' and dte >= " + oStk.dateToStrCh(d_rec.buyDate) + " order by dte");
            rs_stk = stmt_stk.executeQuery(sql_b2.toString());
            moreStk = stk_rec.moveNext(rs_stk);
            if (moreStk) {
                max_high = stk_rec.p_high;
                min_low = stk_rec.p_low;
            } else {
                debugPrint("setSale()/ no any stk record ERROR!", true);
                System.exit(-1);
            }
            flg_clear = FLAG_CONTINUE;
            d_rec.addTimes = 0;
            d_rec.invCnt = 1;
            is_chg = false;
            ma_fld = "";
            ma_stop = 0d;
            hold_days = 0;
            moreStk = stk_rec.moveNext(rs_stk);

            // 逐筆讀入stk資料以判斷賣出日期
            while (moreStk) {
                if (!chkIsNormal()) {
                    initStr(sql_b3);
                    sql_b3.append("update " + tbl_det + " set "
                            + "p_buy = 0 where stockid='" + d_rec.stockId + "' and dte ="
                            + oStk.dateToStrCh(d_rec.dte)
                            + " and p_buy <> 0 and p_stop = 0 and datecode='" + date_code + "'");
                    affectedRecs = stmt_update.executeUpdate(sql_b3.toString());
                    if (affectedRecs == 0) {
                        debugPrint("setSale()/update abnormal stk rec's pBuy=0 err!\r\n"
                                + sql_b3.toString(), true);
                        System.exit(-1);
                    }
                    break;
                }

                // 判斷是否需要賣出
                if (flg_clear == FLAG_CONTINUE) {
                    hold_days += 1;
                    chkIsMaxDrop();
                    tot_profit = calcTotProfit(d_rec.stockId, d_rec.dte, stk_rec.price);
                    //if (is_debug) debugPrint(
                    //	String.format("dte=%tF price=%.2f, tot_profit=%.2f", 
                    //            stk_rec.dte,stk_rec.price,tot_profit));
                    chkIsChg();
                    chkIsStop();
                    chkOtherStop();
                    chkMinProfit();
                }
                if (flg_clear == FLAG_CONTINUE) {
                    if (chkIsForceStopDate(stk_rec.dte)) {
                        flg_clear = FLAG_STOP_NEXTOPEN;
                    }
                }
                // 判斷需賣出時的處理
                if (flg_clear != FLAG_CONTINUE) {
                    //if (stk_rec.stockId.equals("6248"))
                    //	debugPrint(String.format("stk_rec: %s dte=-%tF price =%.2f",
                    //		stk_rec.stockId,stk_rec.dte,stk_rec.price));

                    if (flg_clear == FLAG_STOP_NEXTOPEN) //隔日開盤賣出
                    {
                        if (!rs_stk.next()) {
                            rs_stk.last();
                            stk_rec.setValues(rs_stk);
                            d_rec.pStop = stk_rec.price;
                        } else {
                            stk_rec.setValues(rs_stk);
                            d_rec.pStop = stk_rec.p_open;
                        }
                    } else if (flg_clear == FLAG_STOP_CLOSE) //尾盤賣出
                    {
                        d_rec.pStop = stk_rec.price;
                    }

                    d_rec.stopDate = stk_rec.dte;

                    initStr(sql_b3);
                    sql_b3.append("update " + tbl_det + " set "
                            + "p_stop = " + d_rec.pStop
                            + ",stopdate = " + oStk.dateToStrCh(d_rec.stopDate)
                            + " where stockid='" + d_rec.stockId + "' and dte ="
                            + oStk.dateToStrCh(d_rec.dte)
                            + " and p_buy <> 0 and p_stop = 0 and datecode='" + date_code + "'");

                    affectedRecs = stmt_update.executeUpdate(sql_b3.toString());
                    if (affectedRecs == 0) {
                        debugPrint("setSale()/update stopDate err!\r\n" + sql_b3.toString(), true);
                        System.exit(-1);
                    } else if (is_debug) {
                        debugPrint(
                                String.format("%s/%tF/p_stop=%.2f, stopdate=%tF",
                                        d_rec.stockId, d_rec.dte, d_rec.pStop, d_rec.stopDate));
                    }

                    /* 以下無法用在 ODBC
                     rs_det.updateDouble("p_stop", pStop);
                     rs_det.updateDate("stopdate", stopDate);
                     rs_det.updateRow();
                     */
                    break;
                }
                if (!incrementStock()) {
                    if (decrementStock()) {
                        if (d_rec.invCnt == 0) {
                            break;
                        }
                    }
                }
                if (stk_rec.p_high > max_high) {
                    max_high = stk_rec.p_high;
                }
                if (stk_rec.p_low < min_low) {
                    min_low = stk_rec.p_low;
                }

                moreStk = stk_rec.moveNext(rs_stk); //讀取下一筆
                if (!moreStk) {
                    rs_stk.last();
                    stk_rec.setValues(rs_stk);
                    flg_clear = FLAG_STOP_CLOSE;
                    moreStk = true;
                    debugPrint("setSale()/" + d_rec.stockId + oStk.dateToStr(d_rec.dte)
                            + " 已超過資料庫最後日期，強制賣出!");
                }
            }//while rs_stk
            rs_stk.close();
            rs_stk = null;
            System.gc();
        } // while rs_det

        if (isRandom()) {
            debugPrint("選取股票代號：\r\n" + rndIDs.substring(0, rndIDs.length() - 2));
        }
        rs_det.close();
        //**** 計算持股天數 *****
        initStr(sql_b1);
        sql_b1.append("select * from " + tbl_det + " where p_buy <> 0 and p_stop <> 0 "
                + "and datecode='" + date_code + "'");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        while (d_rec.moveNext(rs_det)) {
            initStr(sql_b2);
            sql_b2.append("select count(*) as cnt from stk where stockid='"
                    + d_rec.stockId + "' and dte between "
                    + oStk.dateToStrCh(d_rec.buyDate)
                    + " and " + oStk.dateToStrCh(d_rec.stopDate));
            rs_stk = stmt_stk.executeQuery(sql_b2.toString());
            if (rs_stk.next()) {
                days = rs_stk.getInt("cnt") - (d_rec.isClose ? 1 : 0);
                initStr(sql_b3);
                sql_b3.append("update " + tbl_det + " set days = " + days
                        + " where stockid='" + d_rec.stockId + "' and dte ="
                        + oStk.dateToStrCh(d_rec.dte) + " and buydate="
                        + oStk.dateToStrCh(d_rec.buyDate)
                        + " and datecode='" + date_code + "'");
                affectedRecs = stmt_update.executeUpdate(sql_b3.toString());
                if (affectedRecs != 1) {
                    debugPrint("setSale update days err!\r\n" + sql_b3.toString(), true);
                    System.exit(-1);
                }
            }
            System.gc();
        } // while 
        if (rs_det != null) {
            rs_det.close();
            rs_det = null;
        }
        if (rs_stk != null) {
            rs_stk.close();
            rs_stk = null;
        }
        stmt_det = null;
        stmt_stk = null;
        stmt_update = null;
        System.gc();

    }

    /**
     * 設定下一筆需間隔幾天才不算重複 n = -1時表示不檢查
     */
    public void setDayInterval(int n) {
        day_interval = n;
    }

    /**
     * 設定幾天內的最大漲幅，凡符合者將不會被買進。見 chkNoOverUp()
     *
     * @param nDays nDays = -1時表示不檢查
     * @param max 最大漲幅(%)
     */
    public void setMaxUp(int nDays, int max) {
        bef_days = nDays;
        max_up = max;
    }

    /**
     * 設定是多方操作或空方操作
     *
     * @param b false=作多
     */
    public void setIsBear(boolean b) {
        is_bear = b;
    }

    /**
     * 設定是否要強制出場，搭配stop_dates[],stop_datesBear[]使用
     *
     * @param b true:要
     */
    public void setForceEnabled(boolean b) {
        forcestop_enabled = b;
    }

    /**
     * 檢查lastdate是否在Days(如5天)前，避免重複選股，譬如昨日剛選到，今天又選到
     */
    public boolean chkNoRepeat(java.util.Date lastDate, int Days, java.util.Date dte) throws SQLException {
        String dtePrev;
        if (Days == -1) {
            return true;
        }
        dtePrev = oStk.getPrevStockDate(dte, Days + 1);
        return lastDate.compareTo(oStk.strToDate(dtePrev)) < 0;
    }

    /**
     * 檢查是否在幾天內(bef_days) 漲幅有無超過 max_up(如20%)
     *
     * @param bef_days bef_days : -1時不檢查；1為當天，為n天前的收盤與 price 的漲跌幅
     * @param max_up 最大漲幅，如漲幅小於 max_up 則 return ok
     * @param dte
     * @param price
     * @return true:沒有超過最大漲幅
     * @throws SQLException
     */
    public boolean chkNoOverUp(int bef_days, double max_up, String stockid,
            java.util.Date dte, double price) throws SQLException {
        String dtePrev, sql;
        Statement stmt;
        ResultSet rs1;
        double prevPrice, n;

        if (bef_days == -1) {
            return true;
        }

        stmt = conn.createStatement();
        dtePrev = oStk.getPrevStockDate(dte, bef_days);
        sql = "select * from stk where stockid='" + stockid
                + "' and dte = " + oStk.padCh(dtePrev);
        rs1 = stmt.executeQuery(sql);
        prevPrice = 0;
        if (rs1.next()) {
            prevPrice = rs1.getDouble("price");
        }
        if (prevPrice != 0) {
            n = (price - prevPrice) / prevPrice * 100;
        } else {
            n = 999.0;
        }
        stmt.close();
        return (n <= max_up);
    }

    @Override
    public int getInDays() {
        return in_days;
    }

    /**
     * 進場篩選，決定實際進場日期與買價
     */
    @Override
    public void setBuy() throws SQLException {

        String lastId = "xxxx";
        String dteCurr, dteNext;
        java.util.Date lastDate = null;
        boolean isOk = false, isBuy = false;
        int in_days_cri; // 調整後para[IN_DAYS]
        int affectedRecs = 0;

        debugPrint("=== setBuy() ===");
        stmt_det = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_stk = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_update = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        tot_sel_recs = 0;
        tot_buy_recs = 0;

        initStr(sql_b1);
        sql_b1.append("select * from " + tbl_det + " where datecode='"
                + date_code + "'" + " ORDER BY stockid,dte");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        //while (rs_det.next())
        while (d_rec.moveNext(rs_det)) {
            //d_rec.setValues(rs_det);

            if (!d_rec.stockId.equals(lastId)) {
                lastId = d_rec.stockId;
                lastDate = d_rec.dte;
                isOk = true;
            }
            if (!isOk) // 同一股票的第1筆以後檢查幾日內有無重複
            {
                isOk = chkNoRepeat(lastDate, day_interval, d_rec.dte);
            }
            if (isOk) // 幾日內有無漲幅太大
            {
                isOk = chkNoOverUp(bef_days, max_up, d_rec.stockId, d_rec.dte, d_rec.price);
            }
            if (isOk) {
                isOk = chkOtherSetBuy();
            }
            if (isOk) {
                in_days_cri = Integer.parseInt(para[IN_DAYS]);
                if (in_days_cri < 0) {
                    in_days_cri = 0;
                }
                tot_sel_recs++;
                dteCurr = oStk.getNextStockDate(d_rec.dte, 2);
                dteNext = oStk.getNextStockDate(d_rec.dte, in_days_cri + 2);
                initStr(sql_b2);
                sql_b2.append("select * from stk where stockid='"
                        + d_rec.stockId + "' and dte between " + oStk.padCh(dteCurr)
                        + " and " + oStk.padCh(dteNext) + " order by dte");
                rs_stk = stmt_stk.executeQuery(sql_b2.toString());
                //if (is_debug) debugPrint(sql_b2.toString());

                setFirstStop();

                in_days = 0;
                while (stk_rec.moveNext(rs_stk)) {
                    in_days++;
                    if (in_days > in_days_cri) {
                        break;
                    }
                    if (chkIsForceStopDate(stk_rec.dte)) {
                        break;
                    }

                    if (!para[BUY_MODE].equals("開盤")) //開盤就買就一定會買
                    {
                        if ((!is_bear && stk_rec.price < first_stop)
                                || (is_bear && stk_rec.price > first_stop)) {
                            if (is_debug) {
                                debugPrint(String.format("%s,%tF %.2f < FirstStop(%.2f)",
                                        d_rec.stockId, stk_rec.dte, stk_rec.price, first_stop));
                            }
                            break;
                        }
                    }

                    is_close = true;
                    isBuy = getBuyPrice(d_rec.price);
                    if (isBuy) {
                        initStr(sql_b3);
                        sql_b3.append("update " + tbl_det + " set "
                                + "p_buy = " + String.valueOf(buy_price)
                                + ",buydate = " + oStk.dateToStrCh(buy_date)
                                + ",isclose = " + String.valueOf(is_close)
                                + ",indays = " + in_days
                                + " where stockid='" + d_rec.stockId + "' and dte ="
                                + oStk.dateToStrCh(d_rec.dte)
                                + " and datecode='" + date_code + "'");
                        affectedRecs = stmt_update.executeUpdate(sql_b3.toString());
                        if (affectedRecs == 0) {
                            if (is_debug) {
                                debugPrint("update no recs!\r\n" + sql_b3.toString());
                            }
                        }

                        tot_buy_recs++;
                        break;
                    }
                } // while
            } // if
            else {
                debugPrint(String.format("setBuy() NO-BUY %s,%tF ",
                        d_rec.stockId, d_rec.dte));

            }
            lastDate = d_rec.dte;
            isOk = false;
        } // while
        clearVars();
        System.gc();
    }

    /**
     * rs_stk 是某股票從選股日隔天幾日內(in_days+1)的stk資料，注意資料多1天以處理隔日開盤買進
     *
     * @param buyPt 買進點
     * @param buyPtMax 最大買進點
     * @return true:買進
     * @throws SQLException
     */
    public boolean overBuy(double buyPt, double buyPtMax, String mode) throws SQLException {
        double buyPt2, pBuy = 0d;
        //String mode = para[BUY_MODE];

        is_close = true;
        if (mode.equals("尾盤") || mode.equals("收盤")) {
            if (stk_rec.price >= buyPt && stk_rec.price <= buyPtMax) {
                pBuy = stk_rec.price;
            }
        } else if (mode.equals("開盤")) {
            is_close = false;
            pBuy = stk_rec.p_open;
        } else if (mode.equals("盤中")) {
            if (stk_rec.p_high >= buyPt && stk_rec.p_high <= buyPtMax) {
                if (stk_rec.p_open >= buyPt) {
                    pBuy = stk_rec.p_open;
                } else if (stk_rec.p_open < buyPt) {
                    pBuy = buyPt;
                }
            }
        } else if (mode.equals("隔開")) // 漲停版時隔日開盤買進, 否則尾盤買進
        {
            if (stk_rec.updown >= (MAX_UPDOWN -0.5)) //漲停
            {
                is_close = false;
                rs_stk.next();
                stk_rec.setValues(rs_stk);
                pBuy = stk_rec.p_open;
            } else if (stk_rec.price >= buyPt && stk_rec.price <= buyPtMax) {
                pBuy = stk_rec.price;
            }
        } else if (mode.equals("隔低")) // 漲停版時隔日以低於前日收盤*1.01買進,未漲停尾盤買進
        {
            if (stk_rec.updown >= (MAX_UPDOWN -0.5)) //漲停
            {
                is_close = false;
                buyPt2 = stk_rec.price * 1.01;
                rs_stk.next();
                stk_rec.setValues(rs_stk);
                if (stk_rec.p_low <= buyPt2) {
                    if (stk_rec.p_open <= buyPt2) {
                        pBuy = stk_rec.p_open;
                    } else {
                        pBuy = buyPt2;
                    }
                }
            } else if (stk_rec.price >= buyPt && stk_rec.price <= buyPtMax) {
                pBuy = stk_rec.price;
            }
        }
        if (pBuy != 0d) {
            buy_price = pBuy;
            buy_date = stk_rec.dte;
            /*if (is_debug) 
             {
             debugPrint("overBuy:"+d_rec.stockId+",BuyPt="
             + dec_fmt.format(buyPt) +
             ", Buy price="+dec_fmt.format(buy_price) +
             ", Buy date="+oStk.dateToStr(buy_date));
             }*/
            return true;
        }
        /*if (is_debug) 
         {
         debugPrint("No overBuy:"+d_rec.stockId+",BuyPt="
         + dec_fmt.format(buyPt) + ", Stock price="+stk_rec.price);
         }*/
        return false;
    }

    /**
     * 計算單筆的獲利 每一次的「賣出」，都會收一次證券交易稅，稅率是千分之三； 買賣各收取千分之 1.425手續費 tax_rate 與 op_rate
     * 在 initOther()用 setRate()設定
     *
     * @param pBuy
     * @param pSale
     * @return
     */
    public double calcOneProfit(double pBuy, double pSale) {
        double cost, earn = 0d;
        cost = (pBuy * op_rate) + (pSale * (op_rate + tax_rate));
        if (!is_bear) {
            earn = pSale - pBuy - cost;
        } else {
            earn = pBuy - pSale - cost;
        }
        return earn;
    }

    /**
     * 計算每一筆交易的盈虧百分比
     */
    @Override
    public void calcEarn() throws SQLException {
        double pEarn, pEarnRate;
        int recs = 0;

        stmt_det = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_update = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        initStr(sql_b1);
        sql_b1.append("select * from " + tbl_det + " where datecode='" + date_code
                + "' and p_buy <> 0 order by stockid, Times");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        debugPrint("=== calcEarn() ===");
        while (rs_det.next()) {
            d_rec.setValues(rs_det);
            pEarn = calcOneProfit(d_rec.pBuy, d_rec.pStop);
            pEarnRate = (pEarn * 100) / d_rec.pBuy;
            /*if (is_debug)
             {
             debugPrint(d_rec.stockId+",buy="
             +d_rec.pBuy+",sale="+d_rec.pStop+",earn="
             +dec_fmt.format(pEarn) +",rate="+dec_fmt.format(pEarnRate));
             }*/
            initStr(sql_b3);
            sql_b3.append("update " + tbl_det + " set "
                    + "profit = " + dec_fmt.format(pEarnRate)
                    + " where stockid='" + d_rec.stockId + "' and dte ="
                    + oStk.dateToStrCh(d_rec.dte) + " and buydate ="
                    + oStk.dateToStrCh(d_rec.buyDate)
                    + " and datecode='" + date_code + "'");

            recs = stmt_update.executeUpdate(sql_b3.toString());
            if (recs == 0) {
                System.out.println("calcEarn update err!\r\n" + sql_b3.toString());
                System.exit(-1);
            }
        } // while
        System.gc();
    }
    public int getParaNum(int px){
        return px;
    }

    /**
     * 一組參數在一個期間產生一筆統計 如果隨機選股，則每一次也產生一筆統計
     */
    @Override
    public void calcStat() throws SQLException {

        int totRecs = 0, earnRecs, loseRecs; // 交易總筆數，賺錢筆數， 損失筆數
        double totEarn, earn, lose; //淨利, 賺錢總計, 損失總計
        double exptot, expLose; // 平均獲利，平均損失
        double avgDays = 0d; // 平均持股天數
        int recs = 0;
        String flds;

        stmt_det = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt_update = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append(
                String.format("select count(*) as cnt, sum(profit) as tot FROM "
                + "%s WHERE datecode='%s' AND p_buy <> 0 and p_stop <> 0 ",
                tbl_det,date_code));
        rs_det = stmt_det.executeQuery(sql_b1.toString() + "AND profit >= 0");
        earnRecs = 0;
        earn = 0d;
        if (rs_det.next()) {
            earnRecs = rs_det.getInt("cnt");
            if (rs_det.wasNull()) {
                earnRecs = 0;
            }
            if (earnRecs != 0) {
                earn = rs_det.getDouble("tot");
            }
        }
        rs_det.close();

        rs_det = stmt_det.executeQuery(sql_b1.toString() + "AND profit < 0");
        loseRecs = 0;
        lose = 0d;
        if (rs_det.next()) {
            loseRecs = rs_det.getInt("cnt");
            if (rs_det.wasNull()) {
                loseRecs = 0;
            }
            if (loseRecs != 0) {
                lose = rs_det.getDouble("tot");
            }
        }
        rs_det.close();

        totRecs = earnRecs + loseRecs; // 總筆數
        totEarn = earn + lose; //淨利
        if (totRecs == 0) {
            debugPrint("calcStat() ***** found tot_recs=0! *****", true);
            //System.exit(-1);
            return;
        }

        if (loseRecs == 0) {
            loseRecs = 1;
        }

        exptot = totEarn / totRecs; // 平均獲利(或期望值)
        expLose = Math.abs(lose / loseRecs); // 平均虧損

        if (expLose == 0) {
            expLose = 0.0001;
        }

        initStr(sql_b1);
        sql_b1.append("select avg(days) from " + tbl_det
                + " where  datecode='" + date_code + "' and p_buy <> 0 and p_stop <> 0");
        rs_det = stmt_det.executeQuery(sql_b1.toString());
        if (rs_det.next()) {
            avgDays = rs_det.getDouble(1);
            if (rs_det.wasNull()) {
                avgDays = 0d;
            }
        }
        if (avgDays == 0d) {
            avgDays = 1d;
            debugPrint("***** calcStat Err! avgDays == 0 chg to 1");
        }
        initStr(sql_b2);
        flds = "sys,subsys,num,datecode,sel_recs,trade_recs," +
                "exp,exp_r,exp_quater,avgdays,winning,net_profit";
        sql_b2.append(String.format("insert into %s (%s) values(",tbl_stat,flds));
        //sql_b2.append(String.format("'%s','%s',%d,",sys,subsys,px));//sys,subsys,參數編號
        sql_b2.append(String.format("'%s','%s',%d,",sys,subsys,getParaNum(px)));//sys,subsys,參數編號
        sql_b2.append(String.format("'%s',%d,%d,",date_code,tot_sel_recs,totRecs)); 
        sql_b2.append(String.format("%.2f,%.2f,",exptot,exptot / expLose));//期望值,R期望值
        sql_b2.append(String.format("%.2f,",exptot * 60d / avgDays));//季期望值
        sql_b2.append(String.format("%.2f,%.2f,",avgDays,(earnRecs * 100.0 / totRecs)));//平均持股天數,勝率
        sql_b2.append(String.format("%.2f)",totEarn));//淨利

        debugPrint (sql_b2.toString());
        recs = stmt_update.executeUpdate(sql_b2.toString());
        stmt_update.close();
        if (recs == 0) {
            debugPrint("calcStat Insert err!\r\n" + sql_b2.toString(), true);
            System.exit(-1);
        }
        System.gc();
    }

    /**
     * tax 交易稅率， op 手續費率，都要再除以1000
     */
    public void setRate(double tax, double op) {
        tax_rate = tax / 1000d;
        op_rate = op / 1000d;
    }

    @Override
    public void initOther() {
        setFiles("[強勢股stat]", "[強勢股]", "LVSTG.txt");
        setTDR_ALL("Y");
        //setTDR("A,B","Y"); //setTDR( "A,B","Y") 將 datacode A,B兩者設為Y
        setRandom(false, 0, 0); // 是否隨機選股
        //setRandom(true, 30, 30); 
        setIsBear(false); //false=作多
        setForceEnabled(true); // true=要依大盤出場
        setDayInterval(5); // 設定下一筆需間隔幾天才不算重複, -1不檢查
        setMaxUp(4, 20); // 設定幾天內最大漲福，1為當天，-1不檢查
        is_debug = true;
        setRate(3.0, 1.425);
    }

    /**
     * 檢查[庫存]+[已賣出]總獲利是否大於[最小獲利]*庫存數目 小於等於時，立即於隔日開盤出清(FLAG_STOP_NEXTOPEN)。
     */
    @Override
    public void chkMinProfit() throws SQLException {
        int num_check;
        double max_lose;
        boolean isStop = false;

        if (flg_clear != FLAG_CONTINUE) {
            return;
        }

        num_check = Integer.parseInt(para[NUM_CHECK]);
        max_lose = Double.parseDouble(para[MAX_LOSE]);
        if (!is_bear) {
            if (d_rec.invCnt >= num_check && tot_profit < max_lose * d_rec.invCnt) {
                isStop = true;
            }
        } else if (d_rec.invCnt >= num_check && tot_profit > max_lose * d_rec.invCnt) {
            isStop = true;
        }
        if (isStop) {
            if (is_debug) {
                debugPrint(String.format("chkminProfit出場/%s" + ",庫存=%d, 總獲利=%2f", d_rec.stockId, d_rec.invCnt, tot_profit));
            }
            flg_clear = FLAG_STOP_NEXTOPEN;
        }
    }

    /**
     * 檢查是否強制出場, true: 出場
     */
    @Override
    public boolean chkIsForceStopDate(java.util.Date dte) {
        boolean rslt;

        if (!forcestop_enabled) {
            return false;
        }

        if (!is_bear) {
            rslt = isStopDate(dte, stop_dates);
        } else {
            rslt = isStopDate(dte, stop_datesBear);
        }
        if (rslt && is_debug) {
            debugPrint("ForceStopDate=" + oStk.dateToStr(dte));
        }
        return rslt;
    }

    /**
     * 檢查額外的出場條件，比如說：在除權息日前一天出場(本系統沒有此功能) 檢查結果如要出場，可設定 flg_clear 為FLF_STOP_CLOSE
     * 或 FLAG_STOP_NEXTOPEN
     */
    @Override
    public void chkOtherStop() {
        if (flg_clear != FLAG_CONTINUE) {
            return;
        }
    }
    /**
     * 其他之進場篩選, return true 表示通過本項篩選
     */
    @Override
    public boolean chkOtherSetBuy() throws SQLException {
        return true;
    }
    

    /**
     * 檢查是否要出場？ true: 出場
     */
    @Override
    public void chkIsStop() throws SQLException {
        int adj;
        int daysCri;
        double minProfit, currPrice, maStopFilt;
        String sDte;
        int flgStop = 0;

        if (flg_clear != FLAG_CONTINUE) {
            return;
        }

        daysCri = Integer.parseInt(para[HOLDDAYS_CRI]);
        minProfit = Double.parseDouble(para[MIN_PROFIT]);
        //rs_stk.refreshRow();
        sDte = oStk.dateToStr(stk_rec.dte);
        currPrice = stk_rec.price;
        if (d_rec.isClose) {
            adj = 0;
        } else {
            adj = 1;
        }
        maStopFilt = ma_stop * Double.parseDouble(para[FILT_MA]);

        if (!is_bear) //多方操作
        {
            if (is_chg) //已經改變成浮動式第二出場規則時
            {
                if (currPrice < maStopFilt) {
                    flgStop = 1;
                }
            } else // 還沒改變出場規則時
            {
                //hold_days從buy_date次日起算，如果不是收盤買進，則hold_days=1其實為持股第2天
                if ((hold_days + adj) >= daysCri && tot_profit < minProfit) {
                    flgStop = 2;
                }
                if (currPrice < first_stop) {
                    flgStop = 3;
                }
            }
        } else if (is_bear) {
            if (is_chg) {
                if (currPrice > maStopFilt) {
                    flgStop = 1;
                }
            } else {

                if ((hold_days + adj) >= daysCri && tot_profit < minProfit) {
                    flgStop = 2;
                }
                if (currPrice > first_stop) {
                    flgStop = 3;
                }
            }
        }
        if (flgStop != 0) {
            flg_clear = FLAG_STOP_CLOSE;

            if (flgStop == 1) {
                debugPrint(d_rec.stockId + " - " + sDte + "$" + dec_fmt.format(currPrice)
                        + " 跌/升破平均線出場點" + ma_fld + "(" + dec_fmt.format(ma_stop)
                        + ")*" + para[FILT_MA] + "=" + dec_fmt.format(maStopFilt));
            } else if (flgStop == 2) {
                debugPrint(d_rec.stockId + " - " + sDte
                        + "持股天數("+(hold_days + adj)+") >= " 
                        + daysCri + " 且獲利("+tot_profit+")未達期間最小漲幅" + minProfit);
            } else if (flgStop == 3) {
                debugPrint(d_rec.stockId + " - " + sDte + " 跌破[初始停損點]"
                        + dec_fmt.format(first_stop) + "出場");
            }

        }
    }

    /**
     * 是否改變成浮動式第二出場規則？ 如果已改變，則設定最高均線出場點(不下移),同時設定 is_chg = true
     */
    @Override
    public void chkIsChg() throws SQLException {
        double chgCri = Double.parseDouble(para[CHG_CRI]);
        double stkMa = 0d;
        if (!is_chg) {
            if (tot_profit >= chgCri) {
                is_chg = true;
                ma_fld = para[STOP_MA];
                ma_stop = rs_stk.getDouble(ma_fld);
                /*if (is_debug)
                 {
                 debugPrint (d_rec.stockId + "," + stk_rec.dte
                 + " IsChg,  TotProfit =" + dec_fmt.format(tot_profit)
                 + " > " + para[CHG_CRI]);
                 }*/
            }
        }
        if (!ma_fld.equals("")) {
            //rs_stk.refreshRow();
            stkMa = rs_stk.getDouble(ma_fld);
            if ((!is_bear && stkMa > ma_stop)
                    || (is_bear && stkMa < ma_stop)) {
                ma_stop = stkMa;
                /*if (is_debug)
                 debugPrint(String.format("mafld=%s, mastop=%.2f filt=%.2f",
                 ma_fld, ma_stop, ma_stop * Double.parseDouble(para[FILT_MA]) ));*/
            }
        }
    }

    /**
     * 將beg_date那一天成交量>300的股票代號賦予序號後存至[randid]
     */
    void crRandid() throws SQLException {
        int ix, recs;
        Statement stmt, stmt2;
        ResultSet rs1;

        stmt = conn.createStatement();
        initStr(sql_b1);
        sql_b1.append("select * from randid where dte=" + oStk.padCh(beg_date));
        rs1 = stmt.executeQuery(sql_b1.toString());
        if (rs1.next()) {
            debugPrint("日期相同，不重新產生 [randid] 表格", true);
            return;
        } else {
            debugPrint("重新產生 [randid] 表格", true);
        }

        stmt2 = conn.createStatement();
        stmt.executeUpdate("delete from randid");
        initStr(sql_b1);
        sql_b1.append("select * from stk where dte=" + oStk.padCh(beg_date) + " and vol > 300");
        rs1 = stmt.executeQuery(sql_b1.toString());
        ix = 0;
        while (rs1.next()) {
            ix++;
            initStr(sql_b2);
            sql_b2.append("insert into randid values('" + rs1.getString("stockid")
                    + "'," + ix + "," + oStk.padCh(beg_date) + ")");
            recs = stmt2.executeUpdate(sql_b2.toString());
            if (recs == 0) {
                debugPrint("in crRandid,sql=(" + sql_b2.toString() + ") error!");
                System.exit(-1);
            }
        }
        stmt = null;
        stmt2 = null;
    }

    /**
     * 隨機選取 needRecs 筆股票代號到 [randsel] Table
     */
    public void randomSelStock(int needRecs) throws SQLException {
        Random rnd;
        int totRecs; // 表格randid中有幾筆紀錄
        int ix, rows;
        Statement stmt, stmt2;
        ResultSet rs1, rs2;
        String sql, sql2, stockId;
        long rndnum;
        StringBuilder IDs = new StringBuilder(200);

        debugPrint("randomSelStock(" + needRecs + ") Start...");

        stmt = conn.createStatement();
        stmt2 = conn.createStatement();

        initStr(sql_b1);
        sql_b1.append("delete from [randsel]");
        stmt.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append("select * from randid order by num DESC");
        rs1 = stmt.executeQuery(sql_b1.toString());
        rs1.next();
        totRecs = rs1.getInt("num");

        rnd = new Random();
        ix = 0;
        while (true) {
            // nextint(n) 產生0~n-1亂數，但rndid是從1開始，所以要+1
            rndnum = rnd.nextInt(totRecs) + 1;
            initStr(sql_b1);
            sql_b1.append("select * from  [randsel] where num = " + rndnum);
            rs1 = stmt.executeQuery(sql_b1.toString());
            if (!rs1.next()) // not exists
            {
                initStr(sql_b2);
                sql_b2.append("select * from randid where num = " + rndnum);
                rs2 = stmt2.executeQuery(sql_b2.toString());
                rs2.next();
                stockId = rs2.getString("stockid");
                initStr(sql_b2);
                sql_b2.append(String.format("insert into [randsel] values('%s',%d)", stockId, rndnum));
                rows = stmt2.executeUpdate(sql_b2.toString());
                if (rows != 1) {
                    debugPrint("insert Error!sql=(" + sql_b2.toString() + ") error!", true);
                    System.exit(-1);
                }
                IDs.append(stockId + ",");
                ix++;
                if (ix >= needRecs) {
                    break;
                }
            }
        } // while
        debugPrint("randomSelStock(" + needRecs + ") End...IDs=" + IDs.toString());
    }

    /* 設定資料庫 */
    @Override
    public void setMyDatabase() throws SQLException {
        oStk = new StkDb(DbConfig.DB_TYPE, "127.0.0.1/backTesting", DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
    }

    /**
     * 將某一個參數組分解放入para[]
     *
     * @param idx 第幾個參數組
     */
    void getOneParaGroup(int idx) {
        int j;
        StringTokenizer st = new StringTokenizer(para_grp[idx], PARA_SEPRATOR);
        for (j = 0; j < PARA_MAXNUM; j++) {
            para[j] = "";
        }
        j = 0;
        while (st.hasMoreTokens()) {
            if (j >= PARA_MAXNUM) {
                System.out.println("參數的數目太多" + para_grp[idx]);
                System.exit(-1);
            }
            para[j] = st.nextToken().trim();
            j++;
        }
    }

    /**
     * 設定統計、資料與追蹤報表
     *
     * @param tblStat 統計table
     * @param tblData 資料table
     * @param txtTrace 執行過程檔
     */
    @Override
    public void setFiles(String tblStat, String tblData, String txtTrace) {
        tbl_stat = tblStat;
        tbl_det = tblData;
        file_trace = txtTrace;
    }

    /**
     * 如果是隨機選股，要同時設定重複作幾次(rnd_times)，每次隨機取幾筆(rnd_recs)
     *
     * @param isRandom true:要隨機選股
     * @param times 做幾次
     * @param recs 每次隨機選幾筆
     */
    public void setRandom(boolean isRandom, int times, int recs) {
        is_random = isRandom;
        if (isRandom) {
            rnd_times = times;
            rnd_recs = recs;
        }
    }

    public void setIsDelStat(boolean b) {
        is_del_stat = b;
    }

    public boolean isDelStat() {
        return is_del_stat;
    }

    public boolean isRandom() {
        return is_random;
    }

    /**
     * 初始化 date_rabges, date_ranges[i][DATE_ENABLED]開始都設為 "N"=disabled
     */
    void initTDR() {
        for (int i = 0; i < DATE_MAXNUM; i++) {
            date_ranges[i][0] = "";
            date_ranges[i][1] = "";
            date_ranges[i][2] = "";
            date_ranges[i][DATE_ENABLED] = "N";
        }
    }

    /**
     * 將datecodes指定的期間代號使有效("Y")或無效("N")<BR>
     * 如：setTDR( "A,B","Y") 將 datacode A,B兩者設為Y
     *
     * @param dateCodes
     * @param YN
     */
    @Override
    public void setTDR(String dateCodes, String YN) {
        String[] dc;
        dc = dateCodes.split(",");
        for (String dc1 : dc) {
            for (String[] date_range : date_ranges) {
                if (date_range[DATE_CODE].equals(dc1)) {
                    date_range[DATE_ENABLED] = YN;
                    break;
                }
            }
        }
    }

    /**
     * 將所有期間使有效("Y")或無效("N")
     */
    @Override
    public void setTDR_ALL(String YN) {
        for (int i = 0; i < DATE_MAXNUM; i++) {
            date_ranges[i][DATE_ENABLED] = YN;
        }
    }

    /**
     * 將資料庫中[測試期間]資料讀入
     */
    @Override
    public void setTestDateRange() {
        Statement stmt;
        String sql;
        ResultSet rs;
        int i = 0, j = 0;
        String nm[] = new String[]{"DATECODE", "BEGDATE", "ENDDATE", "ENABLED"};
        /**
         * 將資料庫中[測試期間資料讀入
         */
        initTDR();
        try {
            stmt = conn.createStatement();
            sql = "select * from bt_period where ENABLED='Y' order by datecode";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                for (j = 0; j <= 2; j++) {
                    date_ranges[i][j] = rs.getString(nm[j]);
                }
                i++;
            }
        } catch (SQLException e) {
            System.err.println("setTestDateRange() Error");
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * 將 stop_date[大盤出場日] 表讀入， isBear 空頭時讀入 stop_datesBear[], !isBear多頭時讀入
     * stop_dates[]
     */
    @Override
    public void setForceStopDate(boolean isBear) {
        Statement stmt;
        ResultSet rs;
        String cIsBear;
        int cnt, i = 0;
        java.util.Date dte;

        if (isBear) {
            cIsBear = "Y";
        } else {
            cIsBear = "N";
        }
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select count(*) as cnt from stop_date "
                    + "where IS_BEAR='" + cIsBear + "'");
            rs.next();
            cnt = rs.getInt("cnt");
            if (!isBear) {
                stop_dates = new java.util.Date[cnt];
            } else {
                stop_datesBear = new java.util.Date[cnt];
            }
            rs = stmt.executeQuery("select * from stop_date "
                    + "where IS_BEAR='" + cIsBear + "'");
            while (rs.next()) {
                dte = rs.getDate("dte");
                if (!isBear) {
                    stop_dates[i++] = dte;
                } else {
                    stop_datesBear[i++] = dte;
                }
            }
        } catch (SQLException e) {
            System.err.println("setForceStopDate() Error");
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * 檢查某日期是否在 stop_dates[]中
     *
     * @param dte
     * @param dteAry
     * @return
     */
    boolean isStopDate(java.util.Date dte, java.util.Date dteAry[]) {
        for (java.util.Date dteAry1 : dteAry) {
            if (dte.equals(dteAry1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查是否自高點回跌超過MAXDROP_CRI, true 表示已跌落 當 para[MAXDROP_CRI]==-1 時表示不作判斷
     */
    @Override
    public void chkIsMaxDrop() throws SQLException {
        double max_drop, stkPrice;
        boolean isStop = false;
        if (flg_clear != FLAG_CONTINUE) {
            return;
        }
        max_drop = Double.parseDouble(para[MAXDROP_CRI]);
        //rs_stk.refreshRow();
        stkPrice = rs_stk.getDouble("price");
        if (max_drop == -1d) {
            return;
        }
        if (!is_bear) {
            if (stkPrice < max_high * (100 - max_drop) / 100) {
                isStop = true;
            }
        } else if (is_bear) {
            if (stkPrice > min_low * (100 + max_drop) / 100) {
                isStop = true;
            }
        }

        if (isStop) {
            if (is_debug) {
                //rs_det.refreshRow();
                debugPrint(rs_det.getString("stockid") + " - "
                        + oStk.dateToStr(rs_det.getDate("dte"))
                        + "， 自高點回跌 " + para[MAXDROP_CRI] + "%出場");
            }
            flg_clear = FLAG_STOP_CLOSE;
        }
    }

    /**
     * 計算包括已賣出與庫存之總獲利，如果資料錯誤則return -100d，否則return總獲利
     *
     * @param stockid stockid + dte 為該股票的識別
     * @param dte
     * @param currPrice 當日價格
     * @return
     * @throws SQLException
     */
    @Override
    public double calcTotProfit(String stockid, java.util.Date dte, double currPrice) throws SQLException {
        double totProfit1, totProfit2, totProfit;
        double cost1, cost2, price1, price2;
        String sql;
        ResultSet rs4;
        Statement stmt;

        cost1 = 0d;
        cost2 = 0d;

        //*** 計算已結案總獲利 ***
        sql = "select sum(p_stop) as price1,sum(p_buy) as cost1 "
                + " from " + tbl_det + " where stockid='" + stockid + "' and"
                + " dte = " + oStk.dateToStrCh(dte)
                + " and p_buy <>0 and p_stop <>0 and datecode='"+date_code+"'";
        stmt = conn.createStatement();
        rs4 = stmt.executeQuery(sql);

        totProfit1 = 0d;
        if (rs4.next()) {
            price1 = rs4.getDouble("price1");
            cost1 = rs4.getDouble("cost1");
            if (!rs4.wasNull()) {
                totProfit1 = price1 - cost1;
            }
        }
        //*** 計算庫存總獲利
        sql = "select count(*) AS CNT ,sum(p_buy) as COST2 from " + tbl_det
                + " where stockid='" + stockid + "' and"
                + " dte = " + oStk.dateToStrCh(dte)
                + " and p_buy <>0 and p_stop =0 and datecode='"+date_code+"'";
        rs4 = stmt.executeQuery(sql);
        totProfit2 = 0d;
        if (rs4.next()) {
            price2 = currPrice * rs4.getInt("cnt");
            cost2 = rs4.getDouble("cost2");
            if (!rs4.wasNull()) {
                totProfit2 = price2 - cost2;
            }
        }
        if ((cost1 + cost2) != 0d) {
            totProfit = (totProfit1 + totProfit2) / (cost1 + cost2) * 100;
        } else {
            totProfit = -100.0d;
        }
        if (is_bear) {
            totProfit = totProfit * -1;
        }
        return totProfit;

    }

    /**
     * 檢查是否要加碼？
     */
    @Override
    public boolean chkIsAdd() throws SQLException {
        if (is_bear) {
            return false; //空頭時不加碼
        }
        int addMaxTimes = Integer.parseInt(para[ADD_MAXTIMES]);
        double addCri = Double.parseDouble(para[ADD_CRI]); // 5% 則為 0.05
        double currPrice = stk_rec.price;

        if (d_rec.invCnt < addMaxTimes && currPrice >= d_rec.lastBuy * (1 + addCri)) {
            debugPrint(stk_rec.stockId + ","
                    + stk_rec.dte + " 加碼,庫存 " + (d_rec.invCnt + 1));
            return true;
        } else {
            return false;
        }
    }

    public void initStr(StringBuilder sb1) {
        if (sb1.length() != 0) {
            sb1.delete(0, sb1.length());
        }
    }

    @Override
    public boolean incrementStock() throws SQLException {
        StringBuilder sql = new StringBuilder(150);
        int affectedRecs = 0; // SQL Update時影響的筆數

        if (!chkIsAdd()) {
            return false;
        }
        d_rec.addTimes++;
        d_rec.invCnt++;
        d_rec.lastBuy = stk_rec.price;
        //if (sql.length()!=0) sql.delete(0,sql.length());
        sql.append("insert into " + tbl_det);
        sql.append(" (stockid, dte,price,buydate,p_buy,p_stop,times"
                + ",remark,datecode,isclose) values(");
        sql.append("'" + d_rec.stockId + "',");
        sql.append(oStk.dateToStrCh(d_rec.dte) + ",");
        sql.append(d_rec.price + ",");
        sql.append(oStk.dateToStrCh(stk_rec.dte) + ","); // buydate
        sql.append(d_rec.lastBuy + ","); // p_buy
        sql.append("0,"); // p_stop
        sql.append(d_rec.addTimes + ",");
        sql.append("'加'" + ",'" + date_code + "',");
        sql.append(d_rec.isClose + ")");
        affectedRecs = stmt_update.executeUpdate(sql.toString());
        if (affectedRecs == 0) {
            debugPrint("incrementStock() inset err!\r\n" + sql.toString(), true);
            System.exit(-1);
        }
        return true;
    }

    /**
     * 檢查是否要減碼？
     */
    @Override
    public boolean chkIsReduce() throws SQLException {
        if (is_bear) {
            return false; //空頭時不加碼
        }
        double currPrice = stk_rec.price;
        double subCri = Double.parseDouble(para[SUB_CRI]); // 5% 則為 0.05

        if ((d_rec.addTimes > 0 && currPrice <= d_rec.lastBuy * (1 - subCri))
                || (d_rec.addTimes == 0 && currPrice < first_stop)) {
            if (d_rec.addTimes == 0) {
                debugPrint(String.format("%s,%tF,cuurrPrice=%.2f < firstStop=%.2f 減碼 ",
                        stk_rec.stockId, stk_rec.dte, currPrice, first_stop));
            } else {
                debugPrint(String.format("%s,%tF,cuurrPrice=%.2f < lastBuy-%.2f=%.2f 減碼 ",
                        stk_rec.stockId, stk_rec.dte, currPrice, subCri, d_rec.lastBuy * (1 - subCri)));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decrementStock() throws SQLException {
        ResultSet rsAddSub; // add and sub 加減碼專用
        Statement stmtAddSub;
        int affectedRecs = 0; // SQL Update時影響的筆數

        if (!chkIsReduce()) {
            return false;
        }
        // 讀取未結案之最後一筆加減碼資料(後進先出)
        initStr(sql_b1);
        sql_b1.append("select * from " + tbl_det + " where stockid ='" + d_rec.stockId
                + "' and dte = " + oStk.dateToStrCh(d_rec.dte) + " "
                + "and  p_buy <>0 and p_stop = 0 order by Times desc");
        stmtAddSub = conn.createStatement();
        rsAddSub = stmtAddSub.executeQuery(sql_b1.toString());
        if (rsAddSub.next()) {
            initStr(sql_b3);
            sql_b3.append("update " + tbl_det + " set "
                    + "p_stop = " + stk_rec.price
                    + ",stopdate = " + oStk.dateToStrCh(stk_rec.dte)
                    + ",remark='減' where stockid='" + d_rec.stockId + "' and dte ="
                    + oStk.dateToStrCh(d_rec.dte) + " and datecode='"
                    + date_code + "' "
                    + " and Times=" + rsAddSub.getInt("Times"));
            affectedRecs = stmt_update.executeUpdate(sql_b3.toString());
            if (affectedRecs == 0) {
                debugPrint("decrementStock()/update stopDate err!\r\n" + sql_b3.toString(), true);
                System.exit(-1);
            }
            d_rec.lastBuy = stk_rec.price;
            d_rec.invCnt--;
        } else {
            d_rec.invCnt = 0;
        }
        return true;
    }

    protected void debugPrint(String s) {
        debugPrint(s, false);
    }

    /**
     * 集中寫檔動作，以免 throws IOException滿天飛 isDisplay = true時，同時System.out.println
     *
     * @param s
     * @param isDisplay
     */
    protected void debugPrint(String s, boolean isDisplay) {
        try {
            f_out.write(s);
            f_out.newLine();
            f_out.flush();
            if (isDisplay) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    void setArrayCS(String stockId, java.util.Date dte) throws SQLException {
        String sDteCurr, sDtePrev;
        StringBuilder sql = new StringBuilder(300);
        Statement stmt;
        ResultSet rs;
        int cnt;

        if (stockId.equals(acs_lastId) && dte.equals(acs_lastDte)) {
            return;
        } else {
            acs_lastId = stockId;
            acs_lastDte = dte;
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
    

}
