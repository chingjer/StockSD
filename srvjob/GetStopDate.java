
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * 上市上櫃暫停融券賣出資料每日更新
 * Usage: java GetStopDate [日期](格式:yyyy-mm-dd)
 * @since 2015/6/27
 * @author huangtm
 */
public class GetStopDate {
    StkDb oStk;
    Connection conn;
    MyWebPage wp;
    String sNow, sQdate;
    String sInsert;
    Statement stmt;
    int cnt_ins,cnt_err;
    
    GetStopDate(String[] args) throws SQLException {
        String aStr[];
        System.setErr(System.out);
        String sYr2, sMth, sDay;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        stmt = conn.createStatement();
        
        System.out.println("\r\n***** GetStopDate (上市上櫃暫停融券賣出資料每日更新) *****");
        if (args.length >= 1) {
            sNow = args[1];
        } else {
            //sNow = String.format("%1$tY/%1$tm/%1$td", now);
            sNow = oStk.currDate;
        }

        aStr = sNow.split("-");
        sYr2 = String.valueOf(Integer.valueOf(aStr[0]) - 1911);
        sMth = String.valueOf(Integer.valueOf(aStr[1]) + 100).substring(1);
        sDay = String.valueOf(Integer.valueOf(aStr[2]) + 100).substring(1);        
        sQdate = String.format("%s/%s/%s",sYr2,sMth,sDay);
    }
    /**
     * 將民國日轉為mysql Date 格式
     * @param s1 如104/06/09
     * @return 如2015-06-09
     */
    String cvSqlDate(String s1){
        String sYr;
        String aStr[];
        aStr = s1.split("/");
        sYr = String.valueOf(Integer.valueOf(aStr[0]) + 1911);
        return String.format("%s-%s-%s",sYr,aStr[1],aStr[2]);       
    }
    String getInsSql(String[] bb, int colRmk){
        int j;
        String rtn="";
        try{
            for(j=0;j<5;j++){
                bb[j] = bb[j].trim();
            }
            MyFunc.initSql();
            MyFunc.genSql(bb[0], "stockid", "'");
            MyFunc.genSql(cvSqlDate(bb[2]), "startdate", "'");
            MyFunc.genSql(cvSqlDate(bb[3]), "enddate", "'");
            MyFunc.genSql(bb[colRmk], "rmk", "'");
            MyFunc.genSqlEnd();
            rtn= MyFunc.getInsertSql("stopdate");        
        } catch (Exception ex) {
            System.err.println(bb[0]+" have an error!");
        }
        return rtn;
    }
    void InsertData(String sql){
        try {
            if (!("".equals(sql)))
            {
                stmt.executeUpdate(sql);
                cnt_ins++;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")){
                System.err.println(sql);
                cnt_err++;
            }else{
                System.err.println(e.getMessage());
                System.err.println("================================");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
    void start() throws SQLException {
        String url, str1,sql;
        String[] aa, bb;
        int i;
        
        sql = "delete from stopdate";
        stmt.executeUpdate(sql);        
        // ---- 上市 ---------------------
        url = "http://www.twse.com.tw/ch/trading/exchange/BFI84U/BFI84U.php#"; //合併
        wp = new MyWebPage(url,"big5");
        if (wp.pageContent.length() == 0) {
            return;
        }
        str1 = wp.ParseTable("得為融資融券有價證券停券預告表", 1, -1);
        aa = str1.split("\n"); // 分割行
        cnt_ins = 0;
        cnt_err = 0;
        for (i = 1; i < aa.length; i++) {
            bb = aa[i].split("\t"); //分割一行內欄位
            if (bb.length >= 5){
                InsertData(getInsSql(bb,4));
            }
        }
        System.out.printf("上市停券%d筆，新增發生錯誤%d筆\n", cnt_ins,cnt_err);
        
        // ------ 上櫃 --------------- 
        url = String.format("http://www.tpex.org.tw/web/stock/margin_trading/term/term_print.php?l=zh-tw&sd=%s&ed=&stkno=&s=0,asc,0",sQdate);
        //System.out.println(url);
        wp = new MyWebPage(url,"UTF-8");
        //System.out.println(wp.pageContent.toString());
        if (wp.pageContent.length() == 0) {
            return;
        }
        str1 = wp.ParseTable(sQdate, 1, -1);
        aa = str1.split("\n"); // 分割行
        cnt_ins = 0;
        cnt_err = 0;
        for (i = 1; i < aa.length; i++) {
            bb = aa[i].split("\t"); //分割一行內欄位
            if (bb.length >= 6){
                InsertData(getInsSql(bb,5));
            }
        }
        System.out.printf("上櫃停券%d筆，新增發生錯誤%d筆\n", cnt_ins,cnt_err);
        
    } // start()    
    public static void main(String[] args) {
        System.setErr(System.out);
        try {
            GetStopDate oo = new GetStopDate(args);
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
    
}
