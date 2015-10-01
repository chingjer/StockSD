
import java.io.*;
import java.net.*;

/**
 * 證交所/期貨/三大法人總表
 *
 * @version 1.0
 * @since 2015/1/20
 * @author t.m.Huang
 *
 */
public class DownA1 {

    final int FIELD_NUM = 16;
    final int DOWN_NUMS = 1;
    String dir;
    String filename[] = new String[DOWN_NUMS];
    String Url[] = new String[DOWN_NUMS];
    String urlParm[] = new String[DOWN_NUMS];
    String sStartDate[] = new String[5]; // [0]yyyy/mm/dd,[1]yyyy,[2]mm,[3]dd,[4]yyyymmdd
    String sEndDate[] = new String[5];
    String postParm[] = new String[FIELD_NUM];
    String postData[] = new String[FIELD_NUM];
    HttpURLConnection connection = null;

    DownA1(String args[]) {
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
        if (args.length >= 3) {
            sEndDate[0] = args[2];
        } else {
            sEndDate[0] = sStartDate[0];
        }
    }

    public void doDownload() throws UnsupportedEncodingException, IOException {
        int i;
        File fi;
        seprateDate(sStartDate);
        seprateDate(sEndDate);
        filename[0] = String.format("A_%s_%s.csv", sStartDate[4], sEndDate[4]);
        System.out.printf("file==%s%s\r\n", dir, filename[0]);
        for (i = 0; i < DOWN_NUMS; i++) {
            if (i == 0) {
                /*
                網頁位置：http://www.taifex.com.tw/chinese/3/dl_7_12_6.asp
                (三大法人-下載-總表-依日期)
                */
                setPostParmName_0();
                setPostData_0();
                Url[0] = "http://www.taifex.com.tw/chinese/3/7_12_6dl.asp";
            }
            urlParm[i] = "";
            for (int jx = 0; jx < postParm.length; jx++) {
                urlParm[i] += "&" + postParm[jx] + "=" + URLEncoder.encode(postData[jx], "UTF-8");
            }
            urlParm[i] = urlParm[i].substring(1);
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
        postParm[0] = "DATA_DATE";
        postParm[1] = "DATA_DATE1";
        postParm[2] = "DATA_DATE_Y";
        postParm[3] = "DATA_DATE_M";
        postParm[4] = "DATA_DATE_D";
        postParm[5] = "DATA_DATE_Y1";
        postParm[6] = "DATA_DATE_M1";
        postParm[7] = "DATA_DATE_D1";
        postParm[8] = "syear";
        postParm[9] = "smonth";
        postParm[10] = "sday";
        postParm[11] = "eyear";
        postParm[12] = "emonth";
        postParm[13] = "eday";
        postParm[14] = "datestart";
        postParm[15] = "dateend";
    }
    void setPostData_0() {
        final int SYY = 2;
        final int EYY = 5;
        final int SYEAR = 8;
        final int EYEAR = 11;
        postData[0] = sStartDate[0];/*yyyy/mm/dd*/
        postData[1] = sEndDate[0]; /*yyyy/mm/dd*/
        postData[14] = sStartDate[0];
        postData[15] = sEndDate[0];
        
        postData[SYY] = sStartDate[1];
        postData[SYY+1] = sStartDate[2];
        postData[SYY+2] = sStartDate[3];
        postData[EYY] = sEndDate[1];
        postData[EYY+1] = sEndDate[2];
        postData[EYY+2] = sEndDate[3];
        
        postData[SYEAR] = sStartDate[1];
        postData[SYEAR+1] = sStartDate[2];
        postData[SYEAR+2] = sStartDate[3];
        postData[EYEAR] = sEndDate[1];
        postData[EYEAR+1] = sEndDate[2];
        postData[EYEAR+2] = sEndDate[3];
        
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
     * 執行方式：java DownA1 [basedir] [fromDate][toDate] <br>
     * args[0]: 沒有[basedir]時預設為 ./<br>
     * args[1]: fromDate, 沒有時預設為今日yyyy/mm/dd <br>
     * args[2]: toDate,沒有時預設為args[1]，yyyy/mm/dd 
     */
    public static void main(String[] args) {
        DownA1 oo = new DownA1(args);
        try {
            System.setErr(System.out);
            System.out.println("\r\n***** DownA1 (證交所/期貨/三大法人總表) *****");
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

