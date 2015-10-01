
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public final class DailyReport {

    final int WAIT_RET_DAYS = 60;
    StkDb oStk;
    OpenWinReport oRpt;
    BufferedWriter bw;
    Connection conn;
    String outFile;
    private Statement stmt;
    private ResultSet rs;
    private String sql;
    String bscYr2;// 年報年度
    String bscQt; // 季報季度，103.3Q
    String bscGrMonth; // 月報月份，103/12

    DailyReport() throws SQLException, IOException {
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        oRpt = new OpenWinReport(oStk);
        outFile = oStk.public_loc + oStk.currDate + "盤後選股.htm";
        //bw = new BufferedWriter(new FileWriter(outFile),StandardCharsets.UTF_8);
        bw = Files.newBufferedWriter(Paths.get(outFile), StandardCharsets.UTF_8);
        getBscTime();
    }

    /**
     * 取得基本資料的時間，如季度、月份、年份
     */
    void getBscTime() throws SQLException {
        String sql1;
        Statement stmt1;
        ResultSet rs1;

        sql1 = "select yr2,qt,grmonth from stkbasic where stockid='2330'";
        stmt1 = conn.createStatement();
        rs1 = stmt1.executeQuery(sql1);
        if (rs1.next()) {
            bscYr2 = rs1.getString("yr2");
            bscQt = rs1.getString("qt");
            bscGrMonth = rs1.getString("grmonth");
        }
    }

    /**
     * 大量強勢股 (lvstg)
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_strong() throws SQLException, IOException {
        String title = "表1:大量強勢股";
        String url = "http://jesse0606.pixnet.net/blog/post/40101982";
        
        sql = "SELECT * from v_strong ";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
        }
        System.out.println(title + " Done!");

        rs.close();
    } // rpt_strong

    /**
     * 價格波動率突破(bbbro)
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_squeeze() throws SQLException, IOException {
        String title = "表2:價格波動率突破";
        String url = "http://jesse0606.pixnet.net/blog/post/40116784";
        sql = "SELECT * from v_squeeze";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
        }
        System.out.println(title + " Done!");
        
        rs.close();
    } // rpt_squeeze

    /**
     *
     * @param days
     * @param ord
     * @param grp_limit 前幾名
     * @throws SQLException
     * @throws IOException
     */
    void rpt_group1(int days, String ord, int grp_limit) throws SQLException, IOException {
        String sql1, ss, sql2, cl1, det;
        Statement stmt1, stmt2;
        ResultSet rs1, rs2;
        int i;
        int rnk;

        sql1 = "SELECT * from v_pricechg order by `" + days + "日漲幅(%)` " + ord + " LIMIT " + grp_limit;
        stmt1 = conn.createStatement();
        stmt2 = conn.createStatement();
        rs1 = stmt1.executeQuery(sql1);
        ss = "<TABLE cellpadding='3' cellspacing='0'>";
        ss += "<TR bgcolor=lightcyan><TD COLSPAN=4>近" + days + "日類股平均漲幅(" + oStk.currDate + ")</TD>";
        ss += String.format("<TR style=\"%s\">", oRpt.tableHeadStyle);
        ss += "<TD>排名<TD>類股<TD>平均漲跌(%)<TD>備註";
        bw.write(ss + "</TR>");
        bw.newLine();
        rnk = 0;
        while (rs1.next()) {
            cl1 = rs1.getString("CL1");
            ss = String.format("<TR><TD>%d<TD>%s<TD>%.2f<TD>",
                    ++rnk, cl1, rs1.getDouble(days + "日漲幅(%)"));
            sql2 = String.format("select * from v_pricechg_det "
                    + "where cl1='%s' order by updown%d %s LIMIT 5", cl1, days, ord);
            rs2 = stmt2.executeQuery(sql2);
            det = "";
            while (rs2.next()) {
                det += String.format("、%s%s(%.2f)", rs2.getString("stockid"),
                        rs2.getString("stkname"), rs2.getDouble("updown" + days));
            }
            ss += det.substring(1);
            bw.write(ss + "</TR>");
            bw.newLine();
            rs2.close();
        }
        bw.write("</TABLE>");
        rs1.close();

    }

    /**
     * 類股強弱勢一覽表
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_group() throws SQLException, IOException {
        final int GRP_LIMIT = 6; // rpt_group1() 只列出前幾名類股        
        String title;

        title = "表3:強勢類股分析";
        bw.write(String.format("<P style=\"%s\">%s</P><P>", oRpt.titleStyle, title));
        rpt_group1(5, "DESC", GRP_LIMIT);
        rpt_group1(20, "DESC", GRP_LIMIT);
        System.out.println("表3:強勢類股分析 Done!");

        title = "表4:弱勢類股分析";
        bw.write(String.format("<P style=\"%s\">%s</P><P>", oRpt.titleStyle, title));
        rpt_group1(5, "", GRP_LIMIT);
        rpt_group1(20, "", GRP_LIMIT);

        System.out.println("表4:弱勢類股分析 Done!");
    } // rpt_squeeze

    /**
     * 計算要列印「創新高成長股」報表前還需要補足的資料， 如YRMAX_DTE,YRMIN,CHGSCOPE等
     *
     * @throws SQLException
     */
    void setNewHighData() throws SQLException {
        java.util.Date dCurrDate;
        String begDate, endDate;
        Date dte;
        String sql2, stockid;
        Statement stmt2, stmtBasic;
        ResultSet rs2, rsBasic;
        double yrmax = -1d, yrmin = -1d, chgScope = -1d;
        sql = String.format("SELECT * from v_newhigh where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        dCurrDate = oStk.strToDate(oStk.currDate);
        begDate = oStk.getPrevStockDate(dCurrDate, 480 + WAIT_RET_DAYS);
        endDate = oStk.getPrevStockDate(dCurrDate, WAIT_RET_DAYS);
        stmt2 = oStk.conn.createStatement();
        stmtBasic = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        while (rs.next()) {
            yrmax = rs.getDouble("近年高點");
            stockid = rs.getString("stockid");
            rsBasic = stmtBasic.executeQuery("select * from stkbasic where stockid='" + stockid + "'");
            if (!rsBasic.next()) {
                System.err.printf("*ERROR-01* in rpt_newhigh(), stkbasic no found stockid=%s\r\n", stockid);
                System.exit(-1);
            }
            //-----尋找2年最高價的日期將其紀錄在stkbasic的[YRMAX_DTE]中
            sql2 = String.format("select dte from stk where stockid = '%s' "+
                    "and dte between %s and %s and p_high = %.2f",
                    stockid, oStk.padCh(begDate), oStk.padCh(endDate), yrmax);
            rs2 = stmt2.executeQuery(sql2);
            if (!rs2.next()) {
                System.err.printf("*ERROR-02* in rpt_newhigh(), sql=\r\n%s\r\n", sql2);
                continue;
                //System.exit(-1);
            }
            dte = rs2.getDate("dte");
            rsBasic.updateDate("YRMAX_DTE", dte);
            //-----尋找最高價後的最低點
            sql2 = String.format("select min(p_low) as minlow from stk where stockid='%s' and dte > %s",
                    stockid, oStk.dateToStrCh(dte));
            rs2 = stmt2.executeQuery(sql2);
            if (!rs2.next()) {
                System.err.printf("*ERROR-03* in rpt_newhigh(), sql=\r\n%s\r\n", sql2);
                System.exit(-1);
            }
            yrmin = rs2.getDouble("minlow");
            rsBasic.updateDouble("YRMIN", yrmin);
            //--- chgScopr是範圍(高點到低點，必須<=30%)
            if (yrmin == 0) {
                chgScope = 999.0;
            } else {
                chgScope = Double.parseDouble(String.format("%.2f", (yrmax - yrmin) * 100 / yrmax));
            }
            rsBasic.updateDouble("chgscope", chgScope);
            rsBasic.updateRow();
            rsBasic.close();
            rs2.close();
        } //while       
    }

    /**
     * 新高成長股
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_newhigh() throws SQLException, IOException {
        String title = "表5:新高成長股";
        String url = "http://jesse0606.pixnet.net/blog/post/40721899";
        
        setNewHighData();
        sql = String.format("SELECT * from v_newhigh where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人", "3,14,15,16", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
            bw.write("<p>※三大法人：[買賣力指標]今日買賣超</P>");
        }
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_strong

    /**
     * EBOX
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_ebox() throws SQLException, IOException {
        String title = "表6:EBOX";
        String url = "http://jesse0606.pixnet.net/blog/post/40159930";
        sql = String.format("SELECT * from v_ebox where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人", "3,20,21,22", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
        }
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_strong

    void rpt_bbrev() throws SQLException, IOException {
        String title = "表7:bbrev底部反轉";
        String url = "http://jesse0606.pixnet.net/blog/post/42154274";
        sql = "SELECT * from v_bbrev";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
        }
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_bbrev

    /**
     * RSI6 多頭背離
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_dvgn1() throws SQLException, IOException {
        String title = "表8: RSI6多頭背離";
        String url = "http://jesse0606.pixnet.net/blog/post/42160334";
        
        sql = "SELECT * from v_dvgn1 where `日期` = " + oStk.padCh(oStk.currDate);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("停券日", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        
        rs.close();
    } // rpt_dvgn1()
    
    /**
     * candleStick
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_candle() throws SQLException, IOException {
        String title = "表9: 陰陽線(多或空)";
        String url = "http://jesse0606.pixnet.net/blog/post/41055847";
        
        sql = "SELECT * from v_candleStick";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("進場,停損,指標交叉,優先建議,停券日", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        
        rs.close();
    } // rpt_candle()
    /**
     * 近月三大法人買超股
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_overbuy() throws SQLException, IOException {
        String title = "表十:近月三大法人買超股";
        String url = "http://jesse0606.pixnet.net/blog/post/41969213";
        sql = String.format("SELECT * from v_overbuy where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("", "3,12,13,14", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_dbox
    

    /**
     * DBOX (放空)
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_dbox() throws SQLException, IOException {
        String title = "表X1:dBOX(空)";
        String url = "http://jesse0606.pixnet.net/blog/post/41458106";
        sql = String.format("SELECT * from v_dbox where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人,停券日", "3,14,15,16", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_dbox

    /**
     * BearCandidate (放空)
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_BearCandidate() throws SQLException, IOException {
        String title = "表X2:空頭候選股(空)";
        String url = "http://jesse0606.pixnet.net/blog/post/42125906";
        sql = String.format("SELECT * from v_BearCandidate where yr2='%s' and qt='%s' and grmonth='%s'",
                this.bscYr2, this.bscQt, this.bscGrMonth);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("三大法人,停券日", "3,13,14,15", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
            bw.write("<p>※成交量：有＊者為>10日均量*4</P>");
        }
        System.out.println(title + " Done!");
        rs.close();
    } //rpt_BearCandidate()

    void rpt_bbdrev() throws SQLException, IOException {
        String sPrevDte;
        String title = "表X3:bbdrev頭部反轉";
        String url = "http://jesse0606.pixnet.net/blog/post/42155582";
        
        sPrevDte = oStk.getPrevStockDate(oStk.strToDate(oStk.currDate), 5);
        sql = "SELECT * from v_bbdrev where dte >= " + oStk.padCh(sPrevDte)
                + " AND `近日收盤` >= `收盤`";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("近日%b,三大法人", "", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        
        rs.close();
    } // rpt_bbdrev
    /**
     * RSI6 空頭背離
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_dvgn2() throws SQLException, IOException {
        String title = "表X4: RSI6空頭背離";
        String url = "http://jesse0606.pixnet.net/blog/post/42165983";
        sql = "SELECT * from v_dvgn2 where `日期` = " + oStk.padCh(oStk.currDate);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        oRpt.init("停券日", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        oRpt.reportToFile(oRpt.getReportId(), rs, bw);
        System.out.println(title + " Done!");
        rs.close();
    } // rpt_dvgn2()
        /**
     * BearCandidate (放空)
     *
     * @throws SQLException
     * @throws IOException
     */
    void rpt_Bear5Lay() throws SQLException, IOException {
        String title = "表X5:五雷轟頂候選股(空)";
        String url = "http://jesse0606.pixnet.net/blog/post/42313267";
        //sql = String.format("SELECT * from v_Bear5Lay where yr2='%s' and qt='%s' and grmonth='%s'",
        //        this.bscYr2, this.bscQt, this.bscGrMonth);
        sql = String.format("SELECT * from v_5LayNoBsc");
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        //oRpt.init("三大法人", "3,12,13,14", "");
        oRpt.init("三大法人,停券日", "3", "");
        oRpt.setReportId(String.format("<a href='%s' target='_blank'>%s</a>",url,title));
        if (oRpt.reportToFile(oRpt.getReportId(), rs, bw)){
            //bw.write("<p>※</P>");
        }
        System.out.println(title + " Done!");
        rs.close();
    } //rpt_Bear5Lay()
    

    /**
     * 執行方式: java DailyReport
     *
     */
    void start(String args[]) throws SQLException, IOException {
        oRpt.reportHead(bw, oStk.currDate + "盤後選股");
        rpt_strong();
        rpt_squeeze();
        rpt_group();
        rpt_newhigh();
        rpt_ebox();
        rpt_bbrev();
        rpt_dvgn1();
        rpt_candle();
        rpt_overbuy();
        
        rpt_dbox();
        rpt_BearCandidate();
        rpt_bbdrev();
        rpt_dvgn2();
        rpt_Bear5Lay();
        oRpt.reportTail(bw);
    }

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** DailyReport() *****");
        try {
            DailyReport oo = new DailyReport();
            oo.start(args);
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
}
