
/**
 * 功能：取得股票各種基本資料，使用抓取網頁的方式，每一季抓取季報資料一次，或每一個月抓取月營收資料一次。<br>
 * Usage: java GetBaicDatamode scope<br>
 * mode: 'A' - 全部重新處理, 'C' - 預設，只處理尚未處理者，'T' - 測試，只處理 1101,1102 兩筆<br>
 * scope: 'Q' - Quarter, 'M' - Month<br>
 * 注意事項：<br>
 * <ol>
 * <li>你必須在 stkid 中增加一個欄位 flgBasic Char(1)，本程式每做完一檔股票會將它設為 'Y'<br>
 * 程式執行時可能會出現一些Error，最常見的有兩個：1.Connect Error!重新再執行一次即可。2. 根本沒有這個代號<br>
 * ，可能是已下市，此時你必須將stkid的該代號紀錄刪掉，再重新執行。<br>
 * 重新執行時，因為有 flgBasic的紀錄，所以已經做過的(='Y')不會重新作。<br>
 * flgBasic 說明： N=待處理, Y=已處理, X=無基本資料, D=手動註記不處理
 * <li>理完後記得要執行 stkbb.mdb的程式模組mm_basic中的Calc_Yr_Basic()以及calc_PER()
 * </ol>
 */
import java.sql.*;
import java.util.*;

class GetBasicData {

    StkDb oStk;
    Connection conn;

    MyWebPage wp;
    String sInsert, sUpdate;
    String stockId;
    String mode, scope, q_m;
    boolean isTestMode = false;
    int iIns = 0, iUpd = 0, iErr = 0;
    String chSet = "big5";

    GetBasicData(String[] args) throws SQLException {
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        System.out.println("Usage: GetBaicData[mode] [scope][q_m]\n"
                + "mode:A=ALL,[C]=Continue,T=Test\n"
                + "scope:[Q]=Quarter data,M=month data\n"
                + "q_m:Quarter(like 103.3Q) or month()like:103/10)");
        mode = "C";
        scope = "Q";
        q_m = "";
        if (args.length >= 3) {
            q_m = args[2];
        }
        if (args.length >= 2) {
            scope = args[1].toUpperCase();
        }
        if (args.length >= 1) {
            mode = args[0].toUpperCase();
        }
        if (!"ACT".contains(mode)) {
            System.out.println("*** 參數 mode 錯誤，需為 A,C,T");
            System.exit(-1);
        }
        if (!"QM".contains(scope)) {
            // 原先尚可以為'A', 表同時處理季報與月報，加入q_m後必須指定季度或月份，無法使用'A'
            System.out.println("*** 參數 scope 錯誤，需為 Q,M");
            System.exit(-1);
        }
        System.out.printf("mode=%s,scope=%s,Quarter/month=%s\r\n", mode, scope, q_m);
    }

    /**
     * 將stkid中的處理過程記錄flgBasic起始化
     *
     * @throws SQLException
     */
    void initProcessFlag() throws SQLException {
        String sql, sNow;
        ResultSet rs;
        Statement stmt;
        int i;
        stmt = conn.createStatement();
        //--- 將 flgbasic 還原成'N'(尚未處理)，但註記為'X'者是永不執行
        sql = "UPDATE stkid SET flgbasic = 'N' where flgbasic <> 'X'";
        i = stmt.executeUpdate(sql);
        //--- 將 91開頭的flgbasic設為 Y,不處理,因為是TDR
        sql = "UPDATE stkid SET flgbasic = 'X' WHERE LEFT(stockid,2)='91'";
        stmt.executeUpdate(sql);
        //---  新股將其從' ' 設為 N
        sql = "UPDATE stkid SET flgbasic = 'N' WHERE trim(flgbasic)='' or flgbasic is null";
        stmt.executeUpdate(sql);
        //---讀取最近一天的日期
        System.out.println("最近日期是" + oStk.currDate);
        //--- 將stkid中日期不等於最近日期者標示為'X'不處理，可能已被下市或合併
        sql = "UPDATE stkid SET flgbasic = 'X' WHERE not dte = "
                + oStk.padCh(oStk.currDate);
        System.out.println(sql);
        stmt.executeUpdate(sql);
        //--- 將stkid中的TDR排除，如IML,KY安瑞等, 0開頭的是ETF
        sql = "UPDATE stkid SET flgbasic = 'X' WHERE stockid in ('3638','3664') "
                + " or mid(stockid,1,1) = '0'";
        stmt.executeUpdate(sql);
        System.out.println("設定stkid表格中的flgbasic完成");

    }

    /**
     * 檢查是否季度、月份相同 Check Quarter or month is same<br>
     * 將已經處理過的季度或月份設為已處理 一般來說，只需使用 flgBasic就已經可以處理接續作業的功能
     * 因為各公司的財報不是同時上傳，有時會因讀不到而發生錯誤 本來程式應該能夠Handle這些錯誤，偷懶的方法是將flgBasic設為' '就可跳過
     * 但如果發生錯誤太多，希望重新全部做過(mode='A')，責所有的' '都會被重設為'N'(未執行)
     * 但即使是mode='A'，以下的季度、月份判斷就可以跳過原先以正常作完的個股
     *
     * @return true:相同，false:不相同
     */
    boolean checkQoMIsSame() throws SQLException {
        String sql;
        ResultSet rs;
        Statement stmt;
        boolean isSame = false;
        String q_m_read;
        sql = "select qt,grmonth from stkbasic where stockid = '" + stockId + "'";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            switch (scope) {
                case "Q":
                    q_m_read = rs.getString("qt");
                    if (!rs.wasNull() && q_m_read.equals(q_m)) {
                        isSame = true;
                    }
                    break;
                case "M":
                    q_m_read = rs.getString("grmonth");
                    if (!rs.wasNull() && q_m_read.equals(q_m)) {
                        isSame = true;
                    }
                    break;
            }
        }
        return isSame;
    }

    private boolean updData() throws SQLException {
        boolean isOk = true;
        String sql;
        Statement stmt;
        stmt = conn.createStatement();
        if (stmt.executeUpdate(sUpdate) != 1) {
            if (stmt.executeUpdate(sInsert) != 1) {
                iErr += 1;
                isOk = false;
            } else {
                iIns += 1;
            }
        } else {
            iUpd += 1;
        }
        if (isOk) {
            sql = "update stkid set flgbasic='Y' where stockid='" + stockId + "'";
            stmt.executeUpdate(sql);
        }
        return isOk;
    }

    /**
     * 抓取財務比率季表資料
     */
    private void GetFinancialRatio_Quarter() {
        String url, token;
        int i;
        StringTokenizer st;
        url = "http://www.emega.com.tw/z/zc/zcr/zcr_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);
        if (wp.pageContent.length() == 0) {
            return;
        }

        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }
        st = wp.tableRow("期別");
        if (st != null && st.hasMoreElements()) {
            token = st.nextToken();
            MyFunc.genSql(token, "qt", "'");
        }
        st = wp.tableRow("每股稅後淨利(元)");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 6) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "qeps_" + i, "");
        }
        st = wp.tableRow("稅後淨利成長率");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 9) {
            token = wp.adjustToken(st.nextToken());
            //System.out.println("(" + i + ")" + token);
            MyFunc.genSql(token, "qeg_" + i, "");
        }

        st = wp.tableRow("流動比率");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "cur_r", "");
        }
        st = wp.tableRow("速動比率");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "quick_r", "");
        }
    }

    /**
     * 取得12個月營收加總，以及營收累計年增率(GRACC)，以及前三個月每個月的年增率(GRYOY_?)
     */
    private void GetOperatingRevenue_Month() {
        String url, str1;
        String[] aa, bb;
        long n1, n2;
        double[] gryoy = new double[4];
        String grmonth = "";
        int i;
        StringTokenizer st;

        url = "http://www.emega.com.tw/z/zc/zch/zch_" + stockId + ".asp.htm"; //合併
        wp = new MyWebPage(url,chSet);;
        if (wp.pageContent.length() == 0) {
            return;
        }
        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }
        //str1 = wp.ParseTable("累計營收", 1, 3);
        str1 = wp.ParseTable("累計營收", 1, 11);
        //System.out.println (str1);
        aa = str1.split("\n"); // 分割行
        n2 = 0l;
        for (i = 0; i < aa.length; i++) {
            bb = aa[i].split("\t"); //分割一行內欄位
            // --- 處理月營收
            if (i == 0) {
                grmonth = bb[0];
                gryoy[0] = Double.parseDouble(wp.adjustToken(bb[6])); // 累計年增率
            }
            if (i >= 0 && i <= 2) {
                gryoy[i + 1] = Double.parseDouble(wp.adjustToken(bb[4])); // 年增率(前三個月)
            }
            if (i <= 12) {//12個月營收加總
                n1 = Long.parseLong(wp.adjustToken(bb[1]));
                n2 += n1; // 加總12個月營收
            }
        }
        MyFunc.genSql(String.valueOf(n2), "rev12", "");
        MyFunc.genSql(grmonth, "grmonth", "'");
        MyFunc.genSql(String.valueOf(gryoy[0]), "gracc", "");
        for (i = 1; i <= 3; i++) {
            MyFunc.genSql(String.valueOf(gryoy[i]), "gryoy_" + i, "");
        }
    }

    /**
     * 抓取資產負債年表資料<br>
     * Note: called by GetIncomeAccount(), and output nF, nI
     *
     * @param nF []固定資產 OUT PARA
     * @param nI []長期投資 OUT PARA
     */
    private void GetBalanceSheet(double[] nF, double[] nI) {
        String url, token;
        int i;
        StringTokenizer st;

        url = "http://www.emega.com.tw/z/zc/zcp/zcpb/zcpb_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);;
        if (wp.pageContent.length() == 0) {
            return;
        }
        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }
        st = wp.tableRow("長期投資");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i <= 5) {
            token = wp.adjustToken(st.nextToken());
            if (i == 1 || i == 5) {
                MyFunc.genSql(token, "long_inv_" + i, "");
            }
            nI[i - 1] = Double.parseDouble(token);
        }
        MyFunc.sbReplace(wp.pageContent, "固定資產重估增值", "X", false);
        MyFunc.sbReplace(wp.pageContent, "固定資產累計折舊", "X", false);
        MyFunc.sbReplace(wp.pageContent, "固定資產損失準備", "X", false);
        st = wp.tableRow("固定資產");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i <= 5) {
            token = wp.adjustToken(st.nextToken());
            if (i == 1 || i == 5) {
                MyFunc.genSql(token, "fix_ass_" + i, "");
            }
            nF[i - 1] = Double.parseDouble(token);
        }

    }

    /**
     * 抓取損益年表資料
     */
    private void GetIncomeAccount() {
        String url, token, sVal1;
        double[] nF = new double[8];//固定資產
        double[] nI = new double[8];//長期投資
        double[] nE = new double[8];
        double[] nR = new double[4];
        double nRR;
        int i;
        StringTokenizer st;

        for (i = 0; i < nF.length; i++) {
            nF[i] = 0d;
            nI[i] = 0d;
            nE[i] = 0d;
        }
        for (i = 0; i < nR.length; i++) {
            nR[i] = 0d;
        }
        GetBalanceSheet(nF, nI); //抓取資產負債年表資料
        url = "http://www.emega.com.tw/z/zc/zcq/zcqa/zcqa_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);
        if (wp.pageContent.length() == 0) {
            return;
        }

        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }

        st = wp.tableRow("本期稅後淨利");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i <= 4) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "nprofita_" + i, "");
            nE[i - 1] = Double.parseDouble(token);
        }

        nRR = 0.;
        for (i = 0; i < 4; i++) {
            nR[i] = (nF[i] + nI[i] - nF[i + 4] - nI[i + 4]) / (nE[i] + nE[i + 1] + nE[i + 2] + nE[i + 3]);
            //System.out.println (nR[i]);
            nRR += nR[i];
        }
        nRR = nRR / 4 * 100;
        sVal1 = String.valueOf(nR[0] * 100);
        if (sVal1.equals("NaN") || sVal1.equals("Infinity") || sVal1.equals("-Infinity")) {
            sVal1 = "0";
        }
        //System.out.println(nR[0]*100);
        MyFunc.genSql(sVal1, "reinv_rate4", "");//四年盈再率(Reinvestment Rate)
    }

    /**
     * 抓取財務比率年表資料 ***
     */
    private void GetFinancialRatio_Year() {
        String url, token;
        StringTokenizer st;
        int i;
        url = "http://www.emega.com.tw/z/zc/zcr/zcra/zcra_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);
        if (wp.pageContent.length() == 0) {
            return;
        }

        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }
        st = wp.tableRow("期別");
        if (st != null && st.hasMoreElements()) {
            token = st.nextToken();
            MyFunc.genSql(token, "yr2", "'");
        }

        st = wp.tableRow("每股稅後淨利(元)");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 6) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "eps_" + i, "");
        }

        st = wp.tableRow("股東權益報酬率");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 6) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "roe_" + i, "");
        }

    }

    /**
     * 抓取現金流量
     */
    private void GetCashFlow() {
        String url, token;
        StringTokenizer st;
        int i;
        url = "http://www.emega.com.tw/z/zc/zc3/zc3_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);
        if (wp.pageContent.length() == 0) {
            return;
        }

        if (-1 != wp.pageContent.indexOf("查無")) {
            return;
        }
        st = wp.tableRow("期末現金及約當現金");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "cashbeop", "");//期末現金及約當現金(Cash Balances - End of Period)
        }

        /*st = wp.tableRow("期別");//GetFinancialRatio_Quarter()已處理*/
        st = wp.tableRow("本期產生現金流量");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 5) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "cashchg_" + i, "");//最近第n期產生現金流量(Change in Cash Flow)
        }

    }

    /**
     * 基本分析/基本資料
     */
    private void GetBsc() {
        StringTokenizer st;
        String token, url;
        int i;
        url = "http://www.emega.com.tw/z/zc/zca/zca_" + stockId + ".asp.htm";
        wp = new MyWebPage(url,chSet);
        st = wp.tableRow("每股淨值(元)");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "nav", "");//每股淨值
        }

        st = wp.tableRow("負債比例");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "debt_ratio", "");
        }
        //st = wp.tableRow("流通股本(億)");
        st = wp.tableRow("股本(億, 台幣)");
        if (st != null && st.hasMoreElements()) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "outcap", "'");
        }

        MyFunc.sbReplace(wp.pageContent, "前一年度配股", "前一XX配股", false);
        st = wp.tableRow("年度");
        if (st != null && st.hasMoreElements()) {
            token = st.nextToken();
            MyFunc.genSql(token, "yr", "'");
        }
        MyFunc.sbReplace(wp.pageContent, "現金股利(元)", "現X股利(元)", false);
        st = wp.tableRow("現金股利");
        i = 0;
        while (st != null && st.hasMoreElements() && ++i < 7) {
            token = wp.adjustToken(st.nextToken());
            MyFunc.genSql(token, "cashdiv_" + i, "'");//現金股利
        }

    }

    void start() throws SQLException {
        ResultSet rs;
        Statement stmt;
        String sql = "";
        if (mode.equals("A")) {
            initProcessFlag();
        }
        switch (mode) {
            case "A":
            case "C":
                sql = "Select * From stkid where flgbasic='N' order by stockid";
                //System.out.println( "選取資料的SQL為" + sql );
                break;
            case "T":
                sql = "Select * From stkid where stockid = '1101'";
                isTestMode = true;
                break;
        }
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            try {
                stockId = rs.getString("stockid");
                if (checkQoMIsSame() && !isTestMode) {
                    continue;
                }
                System.out.println(stockId);
                //System.err.println(stockId);
                MyFunc.initSql();
                MyFunc.genSql(stockId, "stockid", "'");
                if (scope.equals("Q")) {
                    GetIncomeAccount(); //抓取損益年表、資產負債表資料
                    GetFinancialRatio_Year(); //抓取財務比率年表資料
                    GetFinancialRatio_Quarter(); //抓取財務比率季表資料
                    GetCashFlow();//抓取現金流量
                    GetBsc();//抓取基本資料
                }
                if (scope.equals("M")) {
                    GetOperatingRevenue_Month(); //抓取月營收資料
                }
                MyFunc.genSqlEnd();
                sInsert = MyFunc.getInsertSql("stkbasic");
                sUpdate = MyFunc.getUpdateSql("stkbasic", String.format(
                        "where stockid = '%s'", stockId));
                //System.out.println(sUpdate);
                if (!updData()) {
                    System.err.printf("Id=%s updata() error!\r\nINS_SQL=%s\r\nUPD_SQL=%s\r\n",
                            stockId, sInsert, sUpdate);
                }
            } catch (Exception e) {
                iErr++;
                System.err.println(e.getMessage());
            }
        } // while
        System.out.printf("Upd#=%d, Ins#=%d, Err#=%d\r\n", iUpd, iIns, iErr);
        System.out.println("===== done! =====");
    } // start()

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** GetBasicData() *****");
        try {
            GetBasicData oo = new GetBasicData(args);
            oo.start();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println("================================");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    } //main

}//class
