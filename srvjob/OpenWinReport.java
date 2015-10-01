
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author huangtm
 */
final class OpenWinReport extends MyReport {

    final int STOCK_ID = 1;
    final int STOCK_NAME = 2;
    final int STOCK_DATE = 3;

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
    String kd_rsi; // "KD","RSI","KD+RSI","none" 指有無死叉或金叉

    OpenWinReport(StkDb oStk) {
        super(oStk);
    }

    /**
     * 查詢的前3個欄位必須固定
     */
    @Override
    public String getColumn(int ix, ResultSet rslt) throws SQLException {
        String s1;
        String rpt;
        boolean isLight;
        int[] linkCol = {1, 2, 3};

        rpt = this.getReportId();
        linkCol[2] += isHide(linkCol[2]) ? 1 : 0;

        if (ix == linkCol[0]) {
            s1 = String.format("<span style=\"background-color: #ff9933;cursor:pointer\" "
                    + "onclick=openWin(\"http://www.cnyes.com/twstock/html5chart/"
                    + "%1$s.htm\",\"技術線圖\")>%1$s</span>", rslt.getString(ix));
        } else if (ix == linkCol[1]) {
            s1 = String.format("<span style=\"background-color: #99ffcc;cursor:pointer\" "
                    + "onclick=openWin(\"https://tw.stock.yahoo.com/d/s/earning_"
                    + "%s.html\",\"營收獲利\")>%s</span>", rslt.getString(1), rslt.getString(ix));
        } else if (ix == linkCol[2]) {
            s1 = String.format("<span style=\"background-color:#ffff66;cursor:pointer\" "
                    + "onclick=openWin(\"http://www.cnyes.com/twstock/Institutional/"
                    + "%s.html\",\"三大法人\")>%s</span>", rslt.getString(1), rslt.getString(ix));
        } else {
            isLight = false;
            if (rpt.contains("空頭候選股") && colNames[ix - 1].equals("成交量")) {
                if (rslt.getString(ix).contains("＊")) {
                    isLight = true;
                }
            }
            if (isLight) {
                s1 = "<span style='background-color: #99ffcc'>" + rslt.getString(ix) + "</span>";
            } else {
                s1 = rslt.getString(ix);
            }
        }
        return s1;
    }

    @Override
    public String getMoreColumn(int ix, ResultSet rslt) throws SQLException {
        String s1, ktype;
        Connection conn = rslt.getStatement().getConnection();
        switch (moreColNames[ix - 1]) {
            case "三大法人":
                s1 = getTppii(conn, rslt.getString(STOCK_ID), rslt.getString(STOCK_DATE));
                break;
            case "停券日":
                s1 = getStopDate(conn, rslt.getString(STOCK_ID));
                break;
            case "近日%b":
                s1 = String.format("%.2f", getCurrPtb(rslt));
                break;
            case "進場":
                ktype = rslt.getString("ktype");
                if (ktype.contains("-吊人")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 0.97);
                }else if (ktype.contains("-夜星")) {
                    s1 = String.format("%.2f", acs[1][ACS_PPRICE] * 0.99);
                } else if (ktype.contains("+槌子")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 1.02);
                } else if (ktype.contains("+多頭母子")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 1.02);
                } else if (ktype.contains("+單白兵")||ktype.contains("+晨星")
                        ||ktype.contains("-烏雲罩頂")||ktype.contains("-空頭母子")) {
                    s1 = "開盤";
                } else if (ktype.contains("-空頭吞噬")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 0.96);
                } else if (ktype.contains("-單黑鴉")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 1.0);
                } else if (ktype.contains("+貫穿線")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 1.06);
                } else {
                    s1 = "還沒設計";
                }
                break;
            case "停損":
                ktype = rslt.getString("ktype");
                if (ktype.contains("-吊人")) {
                    s1 = String.format("%.2f", acs[0][ACS_PHIGH] * 1.01);
                } else if (ktype.contains("+槌子")) {
                    s1 = String.format("%.2f", acs[0][ACS_PLOW] * 0.96);
                }else if (ktype.contains("+晨星")) {
                    s1 = String.format("%.2f", acs[1][ACS_PLOW] * 0.99);
                }else if (ktype.contains("-夜星")) {
                    s1 = String.format("%.2f", acs[1][ACS_PHIGH] * 1.01);
                } else if (ktype.contains("+多頭母子")) {
                    s1 = String.format("%.2f", acs[0][ACS_POPEN] * 0.99);
                } else if (ktype.contains("-空頭母子")) {
                    s1 = String.format("%.2f", acs[0][ACS_POPEN] * 1.01);
                } else if (ktype.contains("-空頭吞噬")) {
                    s1 = String.format("%.2f", acs[0][ACS_PHIGH] * 1.01);
                } else if (ktype.contains("+單白兵")) {
                    s1 = String.format("%.2f", acs[0][ACS_PLOW] * 1.02);
                } else if (ktype.contains("-單黑鴉")) {
                    s1 = String.format("%.2f", acs[0][ACS_PHIGH] * 0.98);
                } else if (ktype.contains("-烏雲罩頂")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 1.0);
                } else if (ktype.contains("+貫穿線")) {
                    s1 = String.format("%.2f", acs[0][ACS_PPRICE] * 0.98);
                } else {
                    s1 = "沒設計";
                }
                break;
            case "優先建議":
                ktype = rslt.getString("ktype");
                if (ktype.contains("-吊人")) {
                    s1 = "跌越大";
                } else if (ktype.contains("+槌子")) {
                    s1 = "漲越大";
                }else if (ktype.contains("+晨星")) {
                    s1 = "十字";
                }else if (ktype.contains("-夜星")) {
                    s1 = "十字，跌越大";
                } else if (ktype.contains("+多頭母子")) {
                    s1 = "漲越大，十字優先";
                } else if (ktype.contains("-空頭吞噬")) {
                    s1 = "跌越大";
                } else if (ktype.contains("-烏雲罩頂")) {
                    s1 = "RSI6大於70 > KD9大於70 >";
                } else if (ktype.contains("+貫穿線")) {
                    s1 = "4天內達到買點";
                } else {
                    s1 = "";
                }
                break;
            case "指標交叉":                
                s1 = kd_rsi;
                break;
            default:
                s1 = "";
        }
        return s1;
    }

    /**
     * 取得今日的%b
     *
     * @param rslts
     * @return
     * @throws SQLException
     */
    public double getCurrPtb(ResultSet rslts) throws SQLException {
        StkDb oStk;
        double ptb = -99.0;
        java.util.Date dCurrDate;
        String sql;
        Statement stmt;
        ResultSet rs;

        oStk = this.getStkDb();
        dCurrDate = oStk.strToDate(oStk.currDate);
        sql = String.format("select percentb from stk where stockid='%s' and dte = %s",
                rslts.getString("stockid"), oStk.padCh(oStk.currDate));
        stmt = oStk.conn.createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            ptb = rs.getDouble("percentb");
        }
        return ptb;

    }

    public String getTppii(Connection conn, String stockid, String dte) throws SQLException {
        String sql;
        Statement stmt;
        ResultSet rs;
        stmt = conn.createStatement();
        String s1 = "";
        sql = String.format("select * from v_bsidx "
                + "where stockid='%s'", stockid);
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            s1 = String.format("%s外%.0f,投%.0f,自%.0f,計%.0f",
                    rs.getString("bsidx"),
                    rs.getDouble("a1"), rs.getDouble("b1"),
                    rs.getDouble("c1"), rs.getDouble("t1"));
        }
        rs.close();
        stmt.close();
        return s1;
    }
    public String getStopDate(Connection conn, String stockid) throws SQLException {
        String sql;
        Statement stmt;
        ResultSet rs;
        stmt = conn.createStatement();
        String s1 = "";
        sql = String.format("select * from stopdate "
                + "where stockid='%s'", stockid);
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            s1 = String.format("%s~%s(%s)",
                    rs.getString("startdate"),
                    rs.getString("enddate"),
                    rs.getString("rmk"));
        }
        rs.close();
        stmt.close();
        return s1;
    }

    @Override
    public String getAppendLine(ResultSet rslts, int cols) throws SQLException {
        String s1;
        String id = this.getAppendId();
        StkDb oStk;
        switch (id) {
            case "SAMPLE":
                oStk = this.getStkDb();
                s1 = "<TR style='background-color:#FFFFCC'>";
                s1 += String.format("<TD>法人買賣超<TD COLSPAN=%d>", cols - 4);
                s1 += getTppii(oStk.conn, rslts.getString(STOCK_ID), rslts.getString(STOCK_DATE));
                s1 += "<TD COLSPAN=4>示範getAppendLine可以多欄";
                break;
            default:
                s1 = "";
        }
        return s1;
    }

    @Override
    public boolean isByPass(ResultSet rslts) throws SQLException {
        String rpt = this.getReportId();
        Statement stmt;
        ResultSet rs;
        java.util.Date dCurrDate, dte;
        String sDate, sql;
        StkDb oStk;
        double ptb;
        boolean is_bypass = false;
        String ktype;
        //if ("表2:".equals(rpt.substring(0, 3))) {//價格波動率
        if (rpt.contains("表2:")) {//價格波動率
            //System.out.println("Enter isByPass()" + rslts.getString("stockid"));
            oStk = this.getStkDb();
            dCurrDate = oStk.strToDate(oStk.currDate);
            sDate = oStk.getPrevStockDate(dCurrDate, 6);
            sql = String.format("select va5 from stk where stockid='%s' and dte = %s",
                    rslts.getString("stockid"), oStk.padCh(sDate));
            stmt = oStk.conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                // 今日5日均量 <= 五天前5日均量則跳過不處理
                if (rslts.getDouble("5日均量") <= rs.getDouble("va5") * 3) {
                    is_bypass = true; // by pass
                    //System.out.println("*** Pass It ***" + rslts.getString("stockid"));
                }
            }
        } else if (rpt.contains("表5:新高")) {//新高成長股
            //System.out.println("Enter isByPass()" + rslts.getString("stockid"));
            oStk = this.getStkDb();
            dCurrDate = oStk.strToDate(oStk.currDate);
            sDate = oStk.getPrevStockDate(dCurrDate, 60);
            try {
                dte = rslts.getDate("新高日");
            } catch (SQLException e) {
                dte = null;
            }
            if (dte == null || dte.after(oStk.strToDate(sDate))) {
                is_bypass = true; // by pass
                //System.out.printf("%s, %s Pass It!\r\n ", rpt, rslts.getString("stockid"));
            }
            stmt = oStk.conn.createStatement();
            sql = String.format("select * from tppii_sum where stockid='%s'", rslts.getString("stockid"));
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getDouble("t5") <= 0d) { //三大法人5日買賣超<0
                    is_bypass = true;
                    //System.out.printf("%s, %s Pass It!\r\n ", rpt, rslts.getString("stockid"));
                }
            } else {
                is_bypass = true;
            }
        } else if (rpt.contains("表7:bbrev底部反轉")) {
            setArrayCS(rslts.getString("stockid"), rslts.getDate("dte"));      
            if (!isGoldenCross("RSI",0)) {
                is_bypass = true;
            }
        } else if (rpt.contains("表X3:")) {

            oStk = this.getStkDb();
            dCurrDate = oStk.strToDate(oStk.currDate);
            if (rslts.getDate("dte").equals(dCurrDate)) {
                return true; //今日的略過
            }
            ptb = getCurrPtb(rslts);
            if (ptb != -99.0) {
                if (rslts.getDouble("%b") < ptb) {//前日%b < 今日%b
                    is_bypass = true; // by pass
                    //System.out.println("*** bbdrev Pass It ***" + rslts.getString("stockid"));
                }
            } else {
                is_bypass = true;
            }
        } else if (rpt.contains("陰陽線")) {
            setArrayCS(rslts.getString("stockid"), rslts.getDate("dte"));
            ktype = rslts.getString("ktype");
            kd_rsi = "";
            if (ktype.substring(0,1).equals("-")){
                if (isDeadCross("KD",0)) {
                    kd_rsi += "+KD";
                }
                if (isDeadCross("RSI",0)) {
                    kd_rsi += "+RSI";
                }    
            }else{
                if (isGoldenCross("KD",0)) {
                    kd_rsi += "+KD";
                }
                if (isGoldenCross("RSI",0)) {
                    kd_rsi += "+RSI";
                }                               
            }
            if (!kd_rsi.equals("")){
                kd_rsi = kd_rsi.substring(1);
            }
            if (ktype.contains("-夜星") || ktype.contains("+晨星")
                    || ktype.contains("+多頭母子")) { 
                if (!(acs[0][ACS_VOL] > acs[0][ACS_VA10]*2)){
                    is_bypass = true;                    
                }
            } else if (ktype.contains("-烏雲罩頂")) { 
                if (!isDeadCross("RSI", 0)) {
                    is_bypass = true;
                } else if (!(acs[0][ACS_VOL] > acs[0][ACS_VA10]*2)){
                    is_bypass = true;                    
                }
            } else if (ktype.contains("-空頭吞噬")) {//-RSI OR -KD
                if (!(isDeadCross("RSI", 0) || isDeadCross("KD", 0))) {
                    is_bypass = true;
                }

            } else if (ktype.contains("+單白兵")) {//+RSI OR +KD
                if (!(isGoldenCross("RSI", 0) || isGoldenCross("KD", 0))) {
                    is_bypass = true;
                }
            }
        }
        return is_bypass;
    }

    void setArrayCS(String stockId, java.util.Date dte) throws SQLException {
        String sDteCurr, sDtePrev;
        StringBuilder sql = new StringBuilder(300);
        Statement stmt;
        ResultSet rs;
        int cnt;

        for (int i = 0; i < CandleStick.MAX_NUM; i++) {
            for (int j = 0; j < ACS_DATANUM; j++) {
                acs[i][j] = -1d;
            }
        }
        sDteCurr = oStkDb.dateToStr(dte);
        sDtePrev = oStkDb.getPrevStockDate(dte, 4);
        MyFunc.initStr(sql);
        sql.append(String.format(
                "select * from stk "
                + "where stockid='%s'  and dte between %s and %s order by dte desc",
                stockId, oStkDb.padCh(sDtePrev), oStkDb.padCh(sDteCurr)));
        //System.out.println(sql);
        stmt = oStkDb.conn.createStatement();
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
