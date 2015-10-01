
import java.io.*;
import java.net.*;

/**
 * <h1>下載上市與上櫃每日三大法人買賣超資料</h1>
 *
 * 執行方式：java dnStkDaily2 [basedir] [yyyy/m/d]<br>
 * 沒有[basedir]時預設為 ./<br>
 * 沒有[yyyy/m/d]時預設為今日 範例：java dnStkDaily2 ./ 2009/1/1
 *
 * @version 1.0
 * @since 2015/1/24
 * @author t.m.Huang
 *
 */
class dnStkDaily2 {

    public static void main(String[] args) {
        String dir;
        String filename[] = new String[2];
        String Url[] = new String[2];
        int i;
        String sNow, sYr, sYr2, sMth, sDay;
        java.util.Date now = new java.util.Date();
        File fi;
        String aStr[];
        HttpURLConnection connection = null;

        
        System.setErr(System.out);
        System.out.println("\r\n***** dnStkDaily2 (下載上市與上櫃每日三大法人買賣超資料) *****");
        if (args.length >= 2) {
            sNow = args[1];
        } else {
            sNow = String.format("%1$tY/%1$tm/%1$td", now);
        }

        if (args.length >= 1) {
            dir = args[0];
        } else {
            dir = "./";
        }
        System.out.println(sNow + " Dir:" + dir);

        aStr = sNow.split("/");
        sYr2 = String.valueOf(Integer.valueOf(aStr[0]) - 1911);
        sYr = aStr[0];
        sMth = String.valueOf(Integer.valueOf(aStr[1]) + 100).substring(1);
        sDay = String.valueOf(Integer.valueOf(aStr[2]) + 100).substring(1);

        //櫃買
        filename[0] = "G2_" + sYr2 + sMth + sDay + ".csv";
        /*
        Url[0] = String.format("http://www.gretai.org.tw/web/stock/3insti/daily_trade/"
                + "3itrade_hedge_download.php?l=zh-tw&t=D&d=%s/%s/%s&s=0,asc,0",
                sYr2, sMth, sDay);
        */
        Url[0] = String.format("http://www.tpex.org.tw/web/stock/3insti/daily_trade/"
                + "3itrade_hedge_download.php?l=zh-tw&se=EW&t=D&d=%s/%s/%s&s=0,asc,0",
                sYr2, sMth, sDay);

        filename[1] = "T2_" + sYr + sMth + sDay + "_1by_stkno.csv";
        Url[1] = String.format("http://www.twse.com.tw/ch/trading/fund/T86/print.php?"
                + "edition=ch&filename=genpage/%1$s%2$s/%1$s%2$s%3$s_1by_stkno.dat&type=csv"
                + "&select2=ALLBUT0999&qdate=%1$s%2$s%3$s", sYr, sMth, sDay);
        for (i = 0; i < 2; i++) {
            try {
                System.out.println(Url[i]);
                MyFunc.download(Url[i], dir + filename[i], connection);
                fi = new File(dir, filename[i]);
                if (fi.length() < 2 * 1024) {
                    System.err.printf("\n*ERROR* 檔案(%s)大小有問題(=%d)！\n", filename[i], fi.length());
                    System.exit(-1);
                } else {
                    System.out.printf("\nOK! %s\n", filename[i]);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
