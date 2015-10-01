
import java.io.*;
import java.net.*;

/**
 * 選擇權每日交易行情下載
 * URL:https://www.taifex.com.tw/chinese/3/3_2_3.asp
 *
 * @version 1.0
 * @since 2015//6/10
 * @author t.m.Huang
 *
 */
public class DownA3 {

    final int FIELD_NUM = 18;
    final int DOWN_NUMS = 1;
    String dir;
    String filename[] = new String[DOWN_NUMS];
    String Url[] = new String[DOWN_NUMS];
    String urlParm[] = new String[DOWN_NUMS];
    String sStartDate[] = new String[5]; // [0]yyyy/mm/dd,[1]yyyy,[2]mm,[3]dd,[4]yyyymmdd
    String postParm[] = new String[FIELD_NUM];
    String postData[] = new String[FIELD_NUM];
    HttpURLConnection connection = null;

    DownA3(String args[]) {
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
        filename[0] = String.format("CMDTY_%d%s.csv", iYear, sStartDate[2]);
        System.out.printf("file=%s%s\r\n", dir, filename[0]);
        for (i = 0; i < DOWN_NUMS; i++) {
            if (i == 0) {
                setPostParmName_0();
                setPostData_0();
                Url[0] = "https://www.taifex.com.tw/chinese/3/3_2_3_b.asp";
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
        int jx=0;
        postParm[0] = "goday";
        postParm[1] = "DATA_DATE";
        postParm[2] = "DATA_DATE1";
        postParm[3] = "DATA_DATE_Y";
        postParm[4] = "DATA_DATE_M";
        postParm[5] = "DATA_DATE_D";
        postParm[6] = "DATA_DATE_Y1";
        postParm[7] = "DATA_DATE_M1";
        postParm[8] = "DATA_DATE_D1";
        postParm[9] = "syear";
        postParm[10] = "smonth";
        postParm[11] = "sday";
        postParm[12] = "syear1";
        postParm[13] = "smonth1";
        postParm[14] = "sday1";
        postParm[15] = "datestart";
        postParm[16] = "dateend";
        postParm[17] = "COMMODITY_ID";
    }
    void setPostData_0() {
        int jx=0;
        postData[0] = "";
        postData[1] = sStartDate[0];
        postData[2] = sStartDate[0];
        postData[3] = "";
        postData[4] = "";
        postData[5] = "";
        postData[6] = "";
        postData[7] = "";
        postData[8] = "";
        postData[9] = "";
        postData[10] = "";
        postData[11] = "";
        postData[12] = "";
        postData[13] = "";
        postData[14] = "";
        postData[15] = "";
        postData[16] = "";
        postData[17] = "all";
        
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
     * 執行方式：java DownA3 [basedir] [yyyy/mm/dd] <br>
     * args[0]: 沒有[basedir]時預設為 ./<br>
     * args[1]: YearMonth, 沒有時預設為今日 <br>
     */
    public static void main(String[] args) {
        DownA3 oo = new DownA3(args);
        try {
            System.setErr(System.out);
            System.out.println("\r\n***** DownA3 (選擇權每日交易行情下載) *****");
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

