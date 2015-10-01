
import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;

/**
 * <h1>盤後資料轉入資料庫</h1>
 * 從證交所與櫃買中心下載的盤後資料轉入資料庫 允許多個檔案同時轉入， 此時需注意檔名的先後順序。
 *
 * @author huangtm
 * @version 2.0
 */
class StockCsv {

    StkDb oStk;
    Statement stmt;
    String baseDir;
    String sInsert, sUpdate;
    int cnt_all, cnt_ins, cnt_id;

    /**
     * @param pBaseDir 盤後資料下載檔目錄，如：C:\mystk\import\
     */
    StockCsv(String pBaseDir) {
        baseDir = pBaseDir;
        try {
            oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
            stmt = oStk.conn.createStatement();
            oStk.init("P");
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 讀取 baseDir目錄中所有A11*.csv(證交所) , RSTA*.csv(櫃買) 然後依序處理
     */
    @SuppressWarnings("unchecked")
    public void Import() throws
            FileNotFoundException, SQLException, IOException {
        File ImportDir;
        File[] aFiles;
        String s1, ext;
        int i, j;
        String sFilter = ".csv;.CSV";

        ImportDir = new File(baseDir);
        if (!ImportDir.exists()) {
            System.err.println("沒有這個目錄 " + ImportDir);
            System.exit(-1);
        }
        aFiles = ImportDir.listFiles();
        if (aFiles.length == 0) {
            System.out.println("No Files Match!");
            System.exit(-1);
        }
        Arrays.sort(aFiles, new Comparator() {
            @Override
            public int compare(Object f1, Object f2) {
                return ((File) f1).getName().compareTo(((File) f2).getName());
            }
        });
        for (i = 0; i < aFiles.length; i++) {
            if (!aFiles[i].isDirectory()) {
                s1 = aFiles[i].getName();
                j = s1.indexOf(".");
                ext = s1.substring(j);
                if (s1.substring(0, 3).equals("A11") && sFilter.contains(ext)) {
                    System.out.println(s1);
                    Import1_TWSE(aFiles[i]);
                } else if (s1.substring(0, 4).equals("RSTA") && sFilter.contains(ext)) {
                    System.out.println(s1);
                    Import1_RSTA(aFiles[i]);
                }
            }
        } // for
    } // Import()

    /**
     * 讀取csv檔案中含有年月日那一行中的日期
     *
     * @param sLine csv檔案中的一行資料
     * @return 日期字串，如"2014/01/31"(Access) 或 "2014/01/31"(MySql)
     */
    String getCsvDate(String sLine) {
        String sDate, yy, mm, dd;
        int i;
        sLine = sLine.replace("\"", "");//將引號去掉
        i = sLine.indexOf("年");
        yy = sLine.substring(0, i);
        mm = sLine.substring(i + 1, i + 3);
        i = sLine.indexOf("月") + 1;
        dd = sLine.substring(i, i + 2);
        if (yy.length() < 4) {
            sDate = "" + (1911 + Integer.parseInt(yy));
        } else {
            sDate = yy;
        }
        sDate += oStk.date_delimiter + mm + oStk.date_delimiter + dd;
        System.out.printf("TWSE sDate=%s\n", sDate);
        return sDate;

    }

    /**
     * RTSA的日期格式，如"104/01/12"
     *
     * @param sLine
     * @return "2015-01-12" or "2015/01/12"
     */
    String getCsvDate2(String sLine) {
        String aa[], sDate, yy, mm, dd;
        int i;
        aa = sLine.substring(0, 9).split("/");
        sDate = String.format("%s%s%s%s%s",
                "" + (1911 + Integer.parseInt(aa[0])), oStk.date_delimiter,
                aa[1], oStk.date_delimiter, aa[2]);
        System.out.printf("RTSA sDate=%s\n", sDate);
        return sDate;
    }

    /**
     * 移除雜亂的字元，如"\"",",","=","--"等
     *
     * @param sLine 從csv讀進來的一行字
     * @return 移除雜亂字元後的字串
     */
    private String removeMessyChar(String sLine) {
        char ch[];
        StringBuilder sb2 = new StringBuilder(200);
        String s2;
        boolean isdeli = false;

        ch = sLine.toCharArray();
        MyFunc.initStr(sb2);
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == '\"') {
                isdeli = !isdeli;
            } else {
                if (isdeli) {
                    if (ch[i] != ',') {
                        sb2.append(ch[i]);
                    }
                } else {
                    sb2.append(ch[i]);
                }
            }
        } // for
        s2 = sb2.toString().replace("--", "00");
        s2 = s2.replace("0-", "00");
        s2 = s2.replace("=", "");
        
        return s2;
    }

    /**
     * 利用取得的SQL字串實際異動 stk, sthid 最後更新 SYSPARM 中的 currentdate 欄位
     *
     * @param sUpdate
     * @param sInsert
     * @param aa csv一行分解為陣列，aa[0]是stockid
     * @param sDate
     * @param cl 類別，1=上市，2=上櫃
     * @throws SQLException
     */
    private void updData(String aa[], String sDate,String cl, String sPrice) throws SQLException {
        String sql2;
        ResultSet rs;
        cnt_all++;
        if (stmt.executeUpdate(sInsert) != 1) {
            System.err.println(sInsert);
            System.exit(-1); // 不應該有不成功情形
            //stmt.executeUpdate(sUpdate);
            }
        else{
            cnt_ins++;            
        }
        rs = stmt.executeQuery("select * from stkid where stockid='" + aa[0] + "'");
        if (!rs.next()) { // 新增 stkid
            sql2 = String.format("insert into stkid (stockid,stkname,cl,dte,price) values('%s','"
                    + "%s','%s',%s,%s)",aa[0],aa[1],cl,oStk.padCh(sDate),sPrice);
            stmt.executeUpdate(sql2);
        } else  { //更新股名
            sql2 = String.format("update stkid set stkname='%s',dte=%s, cl='%s',price=%s where stockid='%s'",
                    aa[1], oStk.padCh(sDate), cl,sPrice,aa[0]);
            stmt.executeUpdate(sql2);
            cnt_id++;
        }
        // 更新SYSPARM的currdate
        if (aa[0].equals("1101")
                && oStk.strToDate(oStk.currDate).before(oStk.strToDate(sDate))) {
            sql2 = "update sysparm set currdate = " + oStk.padCh(sDate);
            stmt.executeUpdate(sql2);
        }

    }

    /**
     * 處理證交所一個盤後資料檔
     *
     * @param fi 徵交所網頁selType=ALL or ALLBUT0999
     */
    private void Import1_TWSE(File fi) throws
            FileNotFoundException, SQLException, IOException {
        BufferedReader inf;
        String s1, s2, sDate;
        Boolean isBegin, isValid;
        int fileType;
        int lines, i;
        String[] aa;
        Double vol;

        cnt_all = 0;
        cnt_ins = 0;
        cnt_id = 0;
        fileType = (fi.getName().contains("BUT")) ? 2 : 1;
        sDate = "";
        inf = new BufferedReader(new FileReader(fi));
        isBegin = false;
        lines = 0;
        while ((s1 = inf.readLine()) != null) {
            if (!isBegin) {
                if (fileType == 1 && s1.contains("每日收盤行情")) {
                    sDate = getCsvDate(s1);
                    inf.readLine();
                    isBegin = true;
                } else if (fileType == 2 && s1.contains("恢復交易者。")) {
                    s1 = inf.readLine();
                    sDate = getCsvDate(s1);
                    inf.readLine();
                    inf.readLine();
                    isBegin = true;
                }
                if (isBegin) {
                    stmt.executeUpdate(
                            "delete from stk where dte=" + oStk.padCh(sDate));
                }
            } else if (isBegin) {
                //--- 解決字串中的,號，如"200,450,000"==> 200450000
                lines = lines + 1;
                aa = removeMessyChar(s1).split(",");
                isValid = true;
                if (aa.length < 8) {
                    isValid = false;
                }
                if (aa[0].length() == 0) {
                    isValid = false;
                //} else if (aa[0].trim().length() != 4 && aa[0].substring(0, 1).equals("0")) {
                //    isValid = false;
                }
                if (isValid) { // update or insert to table stk
                    for (i = 0; i < aa.length; i++) {
                        aa[i] = aa[i].trim();
                    }
                    //證券代號0,證券名稱1,成交股數2,成交筆數3,成交金額4,開盤價5,最高價6,最低價7,收盤價8
                    //sSqlDate = "STR_TO_DATE('" + sDate + "', '%Y/%m/%d')";
                    MyFunc.initSql();
                    MyFunc.genSql(sDate, "dte", "'");
                    MyFunc.genSql(aa[0], "stockid", "'");
                    MyFunc.genSql(aa[8], "price", "");
                    MyFunc.genSql(aa[5], "p_open", "");
                    MyFunc.genSql(aa[6], "p_high", "");
                    MyFunc.genSql(aa[7], "p_low", "");
                    aa[2] = aa[2].replace(",", "");
                    aa[2] = aa[2].replace("\"", "");
                    aa[2] = aa[2].replace(" ", "");
                    vol = Double.parseDouble(aa[2]) / 1000d;
                    s1 = Double.toString(vol);

                    MyFunc.genSql(s1, "vol", "");
                    MyFunc.genSqlEnd();
                    sInsert = MyFunc.getInsertSql("stk");
                    sUpdate = MyFunc.getUpdateSql("stk", String.format(
                            "where stockid = '%s' and dte='%s'", aa[0], sDate));
                    updData(aa, sDate, "1",aa[8]);
                } //  update or insert to table stk

            } // elseif begin
        } // while
        inf.close();
        System.out.printf("ALL#=%d, INS#=%d, Upd Stkid#=%d\r\n",cnt_all,cnt_ins,cnt_id);
    } // function

    private void Import1_RSTA(File fi) throws
            FileNotFoundException, SQLException, IOException {
        BufferedReader inf;
        String s1, sDate;
        int lines, i;
        String[] aa;
        Double vol;

        cnt_all = 0;
        cnt_ins = 0;
        cnt_id = 0;
        sDate = "";
        inf = new BufferedReader(new FileReader(fi));
        lines = 0;
        while ((s1 = inf.readLine()) != null) {
            lines++;
            if (lines == 1) {
                sDate = getCsvDate2(s1);
            }
            if (s1.contains("上櫃家數")) {
                break;
            }
            if (lines >= 3) {
                //--- 解決字串中的,號，如"200,450,000"==> 200450000
                //if (lines > 8)	{ break;}
                aa = removeMessyChar(s1).split(",");
                if (aa.length < 9 || aa[0].length() != 4) {
                } else {
                    // 代號0,名稱1,收盤2 ,漲跌3,開盤4 ,最高5 ,最低6,均價7 ,成交股數8
                    MyFunc.initSql();
                    MyFunc.genSql(sDate, "dte", "'");
                    MyFunc.genSql(aa[0], "stockid", "'");
                    MyFunc.genSql(aa[2], "price", "");
                    MyFunc.genSql(aa[4], "p_open", "");
                    MyFunc.genSql(aa[5], "p_high", "");
                    MyFunc.genSql(aa[6], "p_low", "");
                    aa[8] = aa[8].replace(",", "");
                    aa[8] = aa[8].replace("\"", "");
                    aa[8] = aa[8].replace(" ", "");
                    //i = (int)(Integer.parseInt(aa[8]) / 1000);
                    //s1 = Integer.toString(i);
                    vol = Double.parseDouble(aa[8]) / 1000d;
                    s1 = Double.toString(vol);

                    MyFunc.genSql(s1, "vol", "");
                    MyFunc.genSqlEnd();
                    sInsert = MyFunc.getInsertSql("stk");
                    sUpdate = MyFunc.getUpdateSql("stk", String.format(
                            "where stockid = '%s' and dte='%s'", aa[0], sDate));
                    updData(aa, sDate,"2", aa[2]);
                }
            } // elseif begin
        } // while
        inf.close();
        System.out.printf("ALL#=%d, INS#=%d, Upd Stkid#=%d\r\n",cnt_all,cnt_ins,cnt_id);
} // function

    /**
     * 執行方式: java StockCsv [importdir] 如果沒有[importdir] 則預設為目前目錄，");
     *
     * @param args [importdir]
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {

        String sBaseDir;
        System.setErr(System.out);

        System.out.println("\r\n***** StockCsv (轉入csv檔) *****");
        if (args.length > 0) {
            sBaseDir = args[0];
        } else {
            sBaseDir = "./";
        }
        //System.out.println ("Dir is "+sBaseDir);
        StockCsv csv = new StockCsv(sBaseDir);
        try {
            csv.Import();
        } catch (FileNotFoundException e) {
            System.err.println("FileNot Found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (SQLException e) {
            System.err.println(csv.sInsert);
            System.err.println("SQL Error");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("IOException ");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    } //main

};
