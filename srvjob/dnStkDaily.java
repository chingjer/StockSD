
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * 下載上市與上櫃每日盤後資料
 * <p>
 * 修改紀錄：<br>
 * 2.0 -- 改用 InputStratm(Byte Stream二元檔) 讀取，原使用 BufferedReader
 * (CharacterStream文字檔)<br>
 * 2.1 -- 修正Graitai網址錯誤與加上檔案大小判斷<br>
 * 2.2 -- 改用 TWSE POST 方式<br>
 * 2.21 -- TWSE 下載檔案用 SEL_TYPE 常數定義
 *
 * 執行方式：java dnStkDaily [basedir] [yyyy/m/d] 沒有[basedir]時預設為 ./
 * 沒有[yyyy/m/d]時預設為今日 範例：java dnStkDaily ./ 2009/1/1
 *
 * @version 2.21
 * @since 2015/1/7
 * @author t.m.Huang
 *
 */
class dnStkDaily {

    public static void main(String[] args) {
        final int GRATAI = 0; // 上櫃
        final int TWSE = 1; // 上市
        final String SEL_TYPE = "ALLBUT0999";

        String dir;
        String filename[] = new String[2];
        String Url[] = new String[2];
        int i;
        String sNow, sYr, sYr2, sMth, sDay;
        java.util.Date now = new java.util.Date();
        File fi;
        String postParm[] = {"download", "qdate", "selectType"};
        String postData[] = {"csv", "", SEL_TYPE};
        String urlParm[] = new String[2];
        HttpURLConnection connection = null;

        String aStr[];
        System.setErr(System.out);
        System.out.println("\r\n***** dnStkDaily (下載盤後資料csv檔) *****");
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

        filename[0] = "RSTA3104_" + sYr2 + sMth + sDay + ".csv";
        Url[0] = String.format("http://www.gretai.org.tw/ch/stock/aftertrading/DAILY_CLOSE_quotes/stk_quote_download.php?"
                + "l=zh-tw&d=%s/%s/%s&s=0,asc,0", sYr2, sMth, sDay);
        filename[1] = "A112" + sYr + sMth + sDay + SEL_TYPE;
        //Url[1] = String.format("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/MI_INDEX3_print.php?"
        //        + "genpage=genpage/Report%s/%s.php&type=csv", sYr + sMth, filename[1]);
        filename[1] = filename[1] + ".csv";
        Url[1] = "http://www.twse.com.tw//ch/trading/exchange/MI_INDEX/MI_INDEX.php";
        for (i = 0; i < 2; i++) {

            try {
                if (i == TWSE) {
                    postData[1] = String.format("%s/%s/%s", sYr2, sMth, sDay);
                    urlParm[i] = "";
                    for (int jx = 0; jx < postParm.length; jx++) {
                        urlParm[i] += "&" + postParm[jx] + "=" + URLEncoder.encode(postData[jx], "UTF-8");
                    }
                    urlParm[i] = urlParm[i].substring(1);
                    System.out.println(urlParm[i]);
                    connection = MyFunc.excutePost(Url[i], urlParm[i]);
                    MyFunc.download("", dir + filename[i], connection);
                } else {
                    MyFunc.download(Url[i], dir + filename[i], connection);
                }
                fi = new File(dir, filename[i]);
                if (fi.length() < 50 * 1024) {
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
