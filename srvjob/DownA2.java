
import java.io.*;
import java.net.*;

/**
 * 發行量加權股價指數歷史資料
 *
 * @version 1.0
 * @since 2015/3/21
 * @author t.m.Huang
 *
 */
public class DownA2 {

    final int FIELD_NUM = 2;
    final int DOWN_NUMS = 1;
    String dir;
    String filename[] = new String[DOWN_NUMS];
    String Url[] = new String[DOWN_NUMS];
    String urlParm[] = new String[DOWN_NUMS];
    String sStartDate[] = new String[5]; // [0]yyyy/mm/dd,[1]yyyy,[2]mm,[3]dd,[4]yyyymmdd
    String postParm[] = new String[FIELD_NUM];
    String postData[] = new String[FIELD_NUM];
    HttpURLConnection connection = null;

    DownA2(String args[]) {
        java.util.Date now = new java.util.Date();
        if (args.length >= 1) {
            dir = args[0];
        } else {
            dir = "./";
        }
        if (args.length >= 2) {
            sStartDate[0] = args[1];
        } else {
            sStartDate[0] = String.format("%1$tY/%1$tm/%1$td", now);
        }
    }

    public void doDownload() throws UnsupportedEncodingException, IOException {
        int i,iYear;
        File fi;
        seprateDate(sStartDate);
        iYear = Integer.parseInt(sStartDate[1])-1911;
        filename[0] = String.format("MI_5MINS_HIST%d%s.csv", iYear, sStartDate[2]);
        System.out.printf("file=%s%s\r\n", dir, filename[0]);
        for (i = 0; i < DOWN_NUMS; i++) {
            if (i == 0) {
                setPostParmName_0();
                setPostData_0();
                Url[0] = "http://www.twse.com.tw//ch/trading/indices/MI_5MINS_HIST/MI_5MINS_HIST.php";
            }
            urlParm[i] = "";
            for (int jx = 0; jx < postParm.length; jx++) {
                urlParm[i] += "&" + postParm[jx] + "=" + URLEncoder.encode(postData[jx], "UTF-8");
            }
            urlParm[i] = urlParm[i].substring(1);//去掉第一字'&'
            System.out.println(urlParm[i]);
            connection = MyFunc.excutePost(Url[i], urlParm[i]);
            MyFunc.download("", dir + filename[i], connection);

            fi = new File(dir, filename[i]);
            if (fi.length() < 400) {
                System.err.printf("\n*ERROR* 檔案(%s)大小有問題(=%d)！\n", filename[i], fi.length());
                System.exit(-1);
            } else {
                System.out.printf("\nOK! %s\n", filename[i]);
            }
        }
    } //main

    void setPostParmName_0() {
        postParm[0] = "myear";
        postParm[1] = "mmon";
    }
    void setPostData_0() {
        int iYear;
        iYear = Integer.parseInt(sStartDate[1])-1911;
        postData[0] = ""+iYear;/*yyyy/mm/dd*/
        postData[1] = sStartDate[2]; /*yyyy/mm/dd*/
        
    }

    void seprateDate(String dte[]) {
        String aStr[];
        aStr = dte[0].split("/");
        //dte[1] = String.valueOf(Integer.valueOf(aStr[0]) - 1911);
        dte[1] = aStr[0];
        dte[2] = String.valueOf(Integer.valueOf(aStr[1]) + 100).substring(1);
        //dte[2] = aStr[1];
        dte[3] = String.valueOf(Integer.valueOf(aStr[2]) + 100).substring(1);
        //dte[3] = aStr[2];
        dte[4] = "" + dte[1] + dte[2] + dte[3];
    }


    /**
     * 執行方式：java DownA2 [basedir] [yyyy/mm/dd] <br>
     * args[0]: 沒有[basedir]時預設為 ./<br>
     * args[1]: YearMonth, 沒有時預設為今日 <br>
     */
    public static void main(String[] args) {
        DownA2 oo = new DownA2(args);
        try {
            System.setErr(System.out);
            System.out.println("\r\n***** DownA2 (發行量加權股價指數歷史資料) *****");
            oo.doDownload();
        } catch (IOException e) {
            System.out.println("IOException ");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
} //main

