
import java.sql.*;

interface StockTest {

    int PARA_MAXNUM = 25; // 最多參數數目
    String PARA_SEPRATOR = ","; // 參數分隔字元

    int DATE_MAXNUM = 30; // 最多日期區間組數
    int DATE_CODE = 0; // DateRangs[][0] 期間代號
    int DATE_BEG = 1; // DateRangs[][1] 期間開始
    int DATE_END = 2; // DateRangs[][2] 期間結束
    int DATE_ENABLED = 3; // DateRangs[][3] 本期間是否有效，"N"時不執行測試

    //--- 後續動作旗標
    int FLAG_CONTINUE = 0;  // 繼續
    int FLAG_STOP_NEXTOPEN = 1; // 隔日開盤賣出
    int FLAG_STOP_CLOSE = 5; // 收盤賣出

    //--- 參數固定位置
    int ADD_MAXTIMES = 0; // 總部位數，達到時不再加碼，除非有經過減碼(3)
    int ADD_CRI = 1; // 加碼水準，如每5%加碼一次(5%)
    int SUB_CRI = 2; // 減碼水準，如每3%加碼一次(3%)
    int MAX_LOSE = 3; // 部位>=NUM_CHECK時每一部位最少平均獲利, 0指不可處於虧損狀態(0#)
    int NUM_CHECK = 4; // 如3，指部位數超過3筆時，總獲利不可處於虧損狀態，否則即賣出(3)
    int FIRSTSTOP_CRI = 5; // 初始停損點
    int MAXDROP_CRI = 6; // 自高點回落n%出場，20指20%，-1指無效不計算
    int IN_DAYS = 7; //  n日內達到買進水準，1=隔日
    int BUY_MODE = 8; //  ("尾盤","盤中","隔開","隔低","開盤")
    int HOLDDAYS_CRI = 9; //持股天數，若未達CHG_CRI則賣出(5)
    int MIN_PROFIT = 10; //期間最小帳幅
    int CHG_CRI = 11; // 獲利n%以後改變出場規則
    int STOP_MA = 12; //變換停利點之平均線欄位名稱(如ma10，指跌破ma10*0.96時出場)(ma5)
    int FILT_MA = 13; //跌破平均線?%確認 (0.96)

    /*----- init -----*/
    void Start(); // 開始
    
    void setSys(); //設定系統代號與子系統代號

    void initOther(); // 起始化其他變動部分，如setFiles, setMyDatabase...

    void test1() throws SQLException; // 測試一組參數

    void setMyDatabase() throws SQLException; //設定資料庫

    void setFiles(String tbl_stat, String tbl_data, String txt_trace); //設定統計、資料與追蹤報表

    void setParm(); //設定參數群組

    void setTestDateRange(); // 將資料庫中[測試期間資料讀入

    void setTDR(String dateCodes, String YN);

    // 將datecodes指定的期間代號使有效("Y")或無效("N")，如setTDR("A,E","Y") 表示打開 A, E兩個期間
    void setTDR_ALL(String YN); // 將所有期間使有效("Y")或無效("N")

    void setForceStopDate(boolean isBear); // 設定強迫出場日期陣列

    String getFixParaSpec();

    void printPara(); //列印出參數組的明細

    /* ----- buy ----- */
    void getData() throws SQLException; // 選取符合買進條件資料

    void setBuy() throws SQLException; // 進場篩選，決定實際進場日期與買價

    public boolean chkOtherSetBuy() throws SQLException;  //其他的進場篩選

    public boolean getBuyPrice(double price) throws SQLException;//取得買點

    /* ----- Sale ----- */
    void setSale() throws SQLException; // 設定出場日期與賣價

    void setFirstStop() throws SQLException;//設定初次停損點

    double calcTotProfit(String stockid, java.util.Date dte, double currPrice) throws SQLException; //計算[庫存]+[已賣出]的實際獲利

    void chkIsMaxDrop() throws SQLException; // 檢查是否超過MAX_DROP_CRI

    void chkMinProfit() throws SQLException; // 檢查[庫存]+[已賣出]有無達到[最小獲利]水準

    boolean chkIsForceStopDate(java.util.Date dte); // 檢查是否強制出場

    void chkOtherStop(); // 檢查額外的出場條件，比如說：已經超過預定的持股天數

    void chkIsStop() throws SQLException; // 檢查是否符合不出場 true: 出場， false: 不出場

    void chkIsChg() throws SQLException; // 檢查是否要要改變出場規則？

    boolean chkIsNormal(); /// 檢查資料是否正常，比如減資或除權息，不正常則取消買進 

    /* ----- 加減碼 ----- */
    boolean chkIsAdd() throws SQLException; //是否加碼

    boolean incrementStock() throws SQLException; //加碼處理

    boolean chkIsReduce() throws SQLException; //是否減碼

    boolean decrementStock() throws SQLException; //加碼處理

    public int getInDays(); // return private variable in_days

    /* ----- 統計 ----- */
    void calcEarn() throws SQLException; // 計算每一筆交易的盈虧百分比

    void calcStat() throws SQLException; // 統計測試結果

}
