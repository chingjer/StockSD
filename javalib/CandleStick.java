
/**
 * 判斷陰陽線型態的類別
 *
 * @author huangtm
 */
class CandleStick {

    public static final int MAX_NUM = 10; //最多處理10天的K線型態，如母子是2天，晨星是三天...

    public static final int POPEN = 0;
    public static final int PHIGH = 1;
    public static final int PLOW = 2;
    public static final int PPRICE = 3;
    public static final int SC_MA10 = 4;

    /**
     * MAX_NUMS天的資料，acs{0]為當天的資料, acs[1]為前一天 acs[n][POPEN]為開盤, acs[n][PCLOSE]為收盤
     */
    protected double acs[][] = new double[MAX_NUM][5];

    protected char redBlack[] = new char[MAX_NUM]; //黑K('B')或紅K('R')
    protected double lenEntity[] = new double[MAX_NUM]; //實體長度
    protected double lenUpShadow[] = new double[MAX_NUM]; //上影線長度
    protected double lenDnShadow[] = new double[MAX_NUM]; //下影線長度
    protected double lenHL[] = new double[MAX_NUM]; //高低點整體長度
    protected double topEntity[] = new double[MAX_NUM]; //實體頂端
    protected double botEntity[] = new double[MAX_NUM]; //實體底部
    protected double rateDnShadowEntity[] = new double[MAX_NUM]; // 下影線長度/實體長度
    protected double rateUpShadowEntity[] = new double[MAX_NUM]; // 上影線長度/實體長度
    protected double rateUpShadowHL[] = new double[MAX_NUM]; // 上影線佔高低點深度比例
    protected double rateDnShadowHL[] = new double[MAX_NUM]; // 下影線佔高低點深度比例
    protected double rateHL_Price[] = new double[MAX_NUM]; // 整體長度HL與收盤之比
    protected double rateEntity_Price[] = new double[MAX_NUM]; // 實體與收盤之比
    protected double rateEntity_HL[] = new double[MAX_NUM]; // 實體與整體長度HL之比
    protected double mid_HL[] = new double[MAX_NUM]; // 價格區間中心價
    protected double mid_Entity[] = new double[MAX_NUM]; // 實體中心價

    protected String kType = "";

    /**
     * 判斷並取得k線型態代號 ""為沒有，"+槌子"則為槌子，餘類推
     */

    public String getKtype(double aa[][]) {

        kType = "";
        setStkData(aa);
        calcCandleData();
        while (true) {
            // ***** 反轉型態 *****

            //if (isSqueezeAlert(0)) break; //本型態頻率太高，暫時取消
            if (is3Inside(0)) {
                break;
            }
            if (is3Outside(0)) {
                break;
            }
            if (isStar(0)) {
                break;
            }
            if (is3Soldiers(0)) {
                break;
            }
            if (isGapTwo(0)) {
                break;
            }
            if (isDelibration(0)) {
                break;
            }
            //if (isInvertedHammer(0)) break; //太多，需確認，改用isInvHarami()
            if (isInvHammer(0)) {
                break;
            }
            if (isDarkCloudCover(0)) {
                break;
            }
            if (isDojiStar(0)) {
                break;
            }
            if (isOneSoldier(0)) {
                break;
            }
            if (isMatchingLowHigh(0)) {
                break;
            }
            if (isHomingPigeon(0)) {
                break;
            }
            if (isHammer(0)) {
                break;
            }
            if (isHarami(0)) {
                break;
            }
            if (isEngulfing(0)) {
                break;
            }

            // ****** 連續型態 *****
            // >3days
            if (isThreeMethod(0)) {
                break;
            }
            if (isMatHold(0)) {
                break;
            }

            // 3 days
            if (isTasukiGap(0)) {
                break;
            }
            if (isRestAftBattle(0)) {
                break;
            }

            // 2 days
            if (isSeparatingLines(0)) {
                break;
            }
            if (isThrusing(0)) {
                break;
            }

            break;
        } // while
        return kType;
    }

    protected void init() {
        /**
         * 資料起始
         */
        for (int i = 0; i < MAX_NUM; i++) {
            for (int j = 0; j < 4; j++) {
                acs[i][j] = -1;
            }
        }

    }

    /**
     * 設定 k線所參考到的股票盤後資料
     */
    protected void setStkData(double aa[][]) {

        init();
        for (int i = 0; i < aa.length; i++) {
            for (int j = 0; j < aa[0].length; j++) {
                acs[i][j] = aa[i][j];
            }
        }
    }

    /**
     * 設定每一天K線的資料,如是否為紅Ｋ，實體長度等
     */
    protected void calcCandleData() {
        for (int i = 0; i < MAX_NUM; i++) {
            if (acs[i][0] == -1) {
                break;
            }
            calcCandleData_1(i);
        }
    }

    /**
     * 設定一天K線的資料，如實體長度等 ix 指第幾天,0=第1天
     */
    protected void calcCandleData_1(int ix) {
        if (acs[ix][POPEN] > acs[ix][PPRICE]) {
            redBlack[ix] = 'B';//黑K
        } else {
            redBlack[ix] = 'R';//紅K
        }
        if (redBlack[ix] == 'B') //黑K
        {
            topEntity[ix] = acs[ix][POPEN];
            botEntity[ix] = acs[ix][PPRICE];
        } else //紅K
        {
            topEntity[ix] = acs[ix][PPRICE];
            botEntity[ix] = acs[ix][POPEN];
        }
        lenEntity[ix] = Math.abs(acs[ix][POPEN] - acs[ix][PPRICE]);
        lenUpShadow[ix] = acs[ix][PHIGH] - topEntity[ix];
        lenDnShadow[ix] = botEntity[ix] - acs[ix][PLOW];
        rateDnShadowEntity[ix] = lenDnShadow[ix] / (lenEntity[ix] == 0d ? 0.0001 : lenEntity[ix]);
        rateUpShadowEntity[ix] = lenUpShadow[ix] / (lenEntity[ix] == 0d ? 0.0001 : lenEntity[ix]);
        lenHL[ix] = acs[ix][PHIGH] - acs[ix][PLOW];
        mid_HL[ix] = (acs[ix][PHIGH] + acs[ix][PLOW]) / 2;
        mid_Entity[ix] = (acs[ix][POPEN] + acs[ix][PPRICE]) / 2;
        if (lenHL[ix] == 0d) {
            rateUpShadowHL[ix] = 0;
            rateDnShadowHL[ix] = 0;
            rateEntity_HL[ix] = 100;
        } else {
            rateUpShadowHL[ix] = lenUpShadow[ix] / lenHL[ix];
            rateDnShadowHL[ix] = lenDnShadow[ix] / lenHL[ix];
            rateEntity_HL[ix] = lenEntity[ix] / lenHL[ix];
        }

        rateHL_Price[ix] = lenHL[ix] / acs[ix][PPRICE]; //高低點整體長度/收盤價
        rateEntity_Price[ix] = lenEntity[ix] / acs[ix][PPRICE]; //實體長度/收盤價

    }

    /**
     * 是否為多頭趨勢
     */
    protected boolean isBull(int ix) {
        if (acs[ix][SC_MA10] >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否為空頭反轉
     */
    protected boolean isBearReverse() {
        if (acs[0][SC_MA10] < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否為長日，實體長度 >= 收盤*5%
     */
    protected boolean isLong(int ix) {
        //if (rateEntity_Price[ix] < 0.05) return false; 

        if (lenEntity[ix] > mid_HL[ix] * 0.05 && lenEntity[ix] > lenHL[ix] * 0.5) {
            return true; //實體>中心價5%且實體>價格區間50%
        } else {
            return false;
        }
    }

    /**
     * 是否為短日，實體長度 <= 收盤*2%
     */
    protected boolean isShort(int ix) {
        //if (rateEntity_Price[ix] > 0.02) return false; 
        if (lenEntity[ix] <= mid_HL[ix] * 0.02) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否為十字線
     */
    protected boolean isCross(int ix) {
        if (rateEntity_HL[ix] > 0.03) {
            return false;
        }
        return true;
    }

    /**
     * 槌子/吊人
     */
    protected boolean isHammer(int nShift) {
        int days = 1; // 型態日數
        int ix1 = days - 1 + nShift;

        if (!isShort(ix1)) {
            return false; //必需為短實體
        }
        if (rateDnShadowEntity[ix1] < 2.0) {
            return false;//下影線必須為實體兩倍以上
        }
        if (rateUpShadowHL[ix1] > 0.1) {
            return false;//上影線不可超過整體程度10%
        }
        if (rateHL_Price[ix1] < 0.05) {
            return false; //高低點整體長度不可小於收盤價5%
        }
        if (isBearReverse()) {
            kType = "+槌子";
        } else {
            kType = "-吊人";
        }
        return true;
    } // isHammer

    /**
     * 飛鴿還巢與鷹撲
     */
    protected boolean isHomingPigeon(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天,acs[1]-長黑
        int ix2 = days - 2 + nShift; //第2天,acs[0]-短實體，不論紅黑
        double mid;

        if (!isLong(ix1)) {
            return false; // 第1根必須是長實體(#1)
        }
        if (!(topEntity[ix1] >= topEntity[ix2] && botEntity[ix1] <= botEntity[ix2])) {
            return false; //長日實體完全包含次日實體(#2)
        }
        if (isBearReverse()) //空頭反轉--飛鴿還巢
        {
            if (redBlack[ix1] != 'B') {
                return false; // 第1根必須是黑K(#3)
            }
            if (redBlack[ix2] != 'B') {
                return false; // 第2根與第1根顏色相反(#4)
            }
            mid = botEntity[ix1] + (lenEntity[ix1] * 0.4);
            //若限制在中點以上符合者會非常少，所以只規定為40%以上
            if (topEntity[ix2] < mid) {
                return false; // 第2天實體頭部必須在長黑中點以上(#5)
            }
            if (lenEntity[ix2] / lenEntity[ix1] > 0.7) {
                return false; //短日實體長度不得大於長日的 70%(#6)
            }
            kType = "+飛鴿還巢";
        } else //鷹撲
        {
            if (redBlack[ix1] != 'R') {
                return false; // 第1根必須是紅K(#3)
            }
            if (redBlack[ix2] != 'R') {
                return false; // 第2根與第1根顏色相反(#4)
            }
            mid = topEntity[ix1] - (lenEntity[ix1] * 0.4);
            //若限制在中點以上符合者會非常少，所以只規定為40%以上
            if (botEntity[ix2] > mid) {
                return false; // 第2天實體底部必須在長紅中點以下(#5)
            }
            if (rateEntity_HL[ix2] <= 0.5) {
                return false; // 第2根也必須是稍長實體(#6)
            }
            kType = "-鷹撲";
        }
        return true;
    }

    /**
     * 低價配或高價配
     */
    protected boolean isMatchingLowHigh(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天

        if (!isLong(ix1)) {
            return false; // 第1根需為長實體(#1)
        }
        if (Math.abs(acs[ix1][PPRICE] - acs[ix2][PPRICE]) > acs[ix1][PPRICE] * 0.001) {
            return false; // 兩根的收盤相等(千分之一內視為相同)(#2)
        }
        if (isBearReverse()) //空頭反轉--低價配
        {
            if (!(redBlack[ix1] == 'B' && redBlack[ix2] == 'B')) {
                return false; // 第1根與第2根都是黑K(#3)
            }
            kType = "+低價配";
        } else //高價配
        {
            if (!(redBlack[ix1] == 'R' && redBlack[ix2] == 'R')) {
                return false; // 第1根與第2根都是紅K(#3)
            }
            if (!(rateUpShadowHL[ix1] < 0.01 && rateUpShadowHL[ix2] < 0.01)) {
                return false; // 兩根的上影線都很短或沒有(#4)
            }
            kType = "-高價配";
        }
        return true;
    }

    /**
     * 單白兵與單黑鴉
     */
    protected boolean isOneSoldier(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        double midHL;

        midHL = (acs[ix1][PHIGH] + acs[ix1][PLOW]) / 2;
        if (lenHL[ix1] <= midHL * 0.015) {
            return false; // 第1根必須為長K線(>中心價*1.5%)(#1)
        }
        if (!(isLong(ix1) && lenEntity[ix1] > lenHL[ix1] * 0.5)) {
            return false; // 第1根必須為長實體(#2)
        }
        if (isBearReverse()) //空頭反轉--單白兵
        {
            if (!(redBlack[ix1] == 'B' && redBlack[ix2] == 'R')) {
                return false; // 第1根必須是黑K，第2根是紅K(#3)
            }			// 第2根開盤價>=第1根收盤價，第2根幾乎以最高價收盤且高於第1根最高價(#4)
            if (!(acs[ix2][POPEN] >= acs[ix1][PPRICE]
                    && acs[ix2][PPRICE] > acs[ix1][PHIGH]
                    && rateUpShadowHL[ix2] < 0.01)) {
                return false;
            }
            kType = "+單白兵";
        } else //單黑鴉
        {
            if (!(redBlack[ix1] == 'R' && redBlack[ix2] == 'B')) {
                return false; // 第1根必須是紅K，第2根是黑K(#3)
            }			// 第2根開盤價<=第1根收盤價，第2根幾乎以最低價收盤且低於第1根最低價(#4)
            if (!(acs[ix2][POPEN] <= acs[ix1][PPRICE]
                    && acs[ix2][PPRICE] < acs[ix1][PLOW]
                    && rateDnShadowHL[ix2] < 0.01)) {
                return false;
            }
            kType = "-單黑鴉";
        }
        return true;
    }

    /**
     * 星形十字
     */
    protected boolean isDojiStar(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天

        if (!(acs[ix2][POPEN] == acs[ix2][PPRICE])) {
            return false; // 第2根需為十字，開盤=收盤(#1)
        }
        if (acs[ix2][PHIGH] == acs[ix2][PPRICE] || acs[ix2][PLOW] == acs[ix2][PPRICE]) {
            return false; // 第2根須有上下影線構成十字(#2)
        }
        if (!isLong(ix1)) {
            return false; // 第1根需為長實體(#3)
        }
        if (isBearReverse()) //空頭反轉--多頭星形十字
        {
            if (redBlack[ix1] != 'B') {
                return false; // 第1根必須是黑K(#4)
            }
            if (!(acs[ix2][POPEN] < acs[ix1][PPRICE])) {
                return false; // 第2根必須跳空開盤(#5)
            }
            if (rateHL_Price[ix1] > 0.07) {
                return false; //影線不可太長，尤其是多頭星形十字(#6)
            }
            kType = "+星形十字";
        } else //空頭星形十字
        {
            if (redBlack[ix1] != 'R') {
                return false; // 第1根必須是紅K(#4)
            }
            if (!(acs[ix2][POPEN] > acs[ix1][PPRICE])) {
                return false; // 第2根必須跳空開盤(#5)
            }
            if (rateHL_Price[ix1] > 0.1) {
                return false; //影線不可太長(#6)
            }
            kType = "-星形十字";
        }
        return true;
    }

    /**
     * 第3天確認後的倒狀槌子/流星
     */
    protected boolean isInvHammer(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (!isInvertedHammer(1)) {
            return false; // 前兩天需為倒狀槌子或流星
        }
        if (kType.substring(0, 1).equals("+")) //倒狀槌子確認
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] > acs[ix2][PPRICE]) // 第3日價格收高
            {
                kType = "+倒狀槌子3";
                return true;
            }
        } else //空頭母子或空頭母子十字
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] < acs[ix2][PPRICE]) // 第3日價格收低
            {
                kType = "-流星3";
                return true;
            }
        }
        return false;
    }

    /**
     * 倒狀槌子/流星 本型態發生頻率太高，必須加以確認 使用 isInvHarami()來代替
     */
    protected boolean isInvertedHammer(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天,acs[1]-長黑
        int ix2 = days - 2 + nShift; //第2天,acs[0]-短實體，不論紅黑
        double mid;

        if (!isShort(ix2)) {
            return false; //必需為短實體(#1)
        }
        if (rateDnShadowHL[ix2] >= 0.01) {
            return false;//下影線小於整個價格區間10%(#2)
        }
        if (isBearReverse()) //空頭反轉--倒狀槌子
        {
            if (redBlack[ix1] != 'B') {
                return false; // 第1根必須是黑K(#3)
            }
            if (redBlack[ix2] != 'R') {
                return false; // 第2根符合反轉方向(#4)
            }
            if (rateUpShadowEntity[ix2] < 3.0) {
                return false;//上影線必須為實體三倍以上(morris說2倍)(#5)
            }
            if (!(botEntity[ix1] > topEntity[ix2])) {
                return false; // 第1跟與第2跟必須有跳空(morris說不需要缺口)(#6)
            }
            kType = "+倒狀槌子";
        } else //流星
        {
            if (redBlack[ix1] != 'R') {
                return false; // 第1根必須是紅K(#3)
            }
            if (redBlack[ix2] != 'B') {
                return false; // 第2根符合反轉方向(#4)
            }
            if (rateUpShadowEntity[ix2] < 3.0) {
                return false;//上影線必須為實體三倍以上(#5)
            }
            if (!(topEntity[ix1] < botEntity[ix2])) {
                return false; // 第1跟與第2跟必須有跳空(#6)
            }
            kType = "-流星";

        }
        return true;
    }

    /**
     * 母子
     */
    protected boolean isHarami(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天,acs[1]-長黑
        int ix2 = days - 2 + nShift; //第2天,acs[0]-短實體，不論紅黑
        double mid;

        if (isBearReverse()) //空頭反轉--多頭母子
        {
            if (redBlack[ix1] != 'B') {
                return false; // 第1根必須是黑K(#1)
            }
            if (redBlack[ix2] != 'R') {
                return false; // 第2根與第1根顏色相反(#2)
            }
            mid = botEntity[ix1] + (lenEntity[ix1] * 0.4);
            //若限制在中點以上符合者會非常少，所以只規定為40%以上
            if (acs[ix2][PPRICE] < mid) {
                return false; // 第2天收盤必須在長黑中點以上(#3)
            }
        } else //空頭母子
        {
            if (redBlack[ix1] != 'R') {
                return false; // 第1根必須是紅K(#1)
            }
            if (redBlack[ix2] != 'B') {
                return false; // 第2根與第1根顏色相反(#2)
            }
            mid = topEntity[ix1] - (lenEntity[ix1] * 0.4);
            //若限制在中點以上符合者會非常少，所以只規定為40%以上
            if (acs[ix2][PPRICE] > mid) {
                return false; // 第2天收盤必須在長紅中點以下(#3)
            }
        }
        if (!isLong(ix1)) {
            return false; // 第1根必須是長實體(#4)
        }
        if (!(topEntity[ix1] >= topEntity[ix2] && botEntity[ix1] <= botEntity[ix2])) {
            return false; //長日實體完全包含短日實體(#5)
        }
        if (lenEntity[ix2] / lenEntity[ix1] > 0.7) {
            return false; //短日實體長度不得大於長日的 70%(#6)
        }
        if (isBearReverse()) {
            kType = "+多頭母子";
        } else {
            kType = "-空頭母子";
        }
        if (isCross(ix2)) {
            kType += "十字";
        }
        return true;
    } //isHarami()

    /**
     * 貫穿線與烏雲罩頂
     */
    protected boolean isDarkCloudCover(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        double mid;

        if (!(isLong(ix1) && isLong(ix2))) {
            return false; // 兩跟都是長實體(#1)
        }
        if (isBearReverse()) //空頭反轉--貫穿PiercingLine
        {
            if (redBlack[ix1] != 'B') {
                return false; // 第1根必須是黑K(#2)
            }
            if (redBlack[ix2] != 'R') {
                return false; // 第2根與第1根顏色相反(#3)
            }
            mid = botEntity[ix1] + (lenEntity[ix1] * 0.5);
            if (acs[ix2][PPRICE] <= mid) {
                return false; // 第2天收盤必須在長黑中點以上(#4)
            }
            if (!(acs[ix2][POPEN] < acs[ix1][PPRICE])) {
                return false; // 開盤需低於昨日收盤(#5)
            }
            if (topEntity[ix2] >= topEntity[ix1]) {
                return false; // 如果第2根實體頭部超過第1跟就變成吞噬了(#6)
            }
            kType = "+貫穿線";
        } else //烏雲罩頂
        {
            if (redBlack[ix1] != 'R') {
                return false; // 第1根必須是紅K(#2)
            }
            if (redBlack[ix2] != 'B') {
                return false; // 第2根與第1根顏色相反(#3)
            }
            mid = botEntity[ix1] + (lenEntity[ix1] * 0.5);
            if (acs[ix2][PPRICE] >= mid) {
                return false; // 第2天收盤必須在長黑中點以下(#4)
            }
            if (!(acs[ix2][POPEN] > acs[ix1][PHIGH])) {
                return false; // 開盤需高於昨日高點(不是收盤)(#5)
            }
            if (botEntity[ix2] <= botEntity[ix1]) {
                return false; // 如果第2根實體底部低於第1跟就變成吞噬了(#6)
            }
            kType = "-烏雲罩頂";
        }
        return true;
    }

    /**
     * 吞噬
     */
    protected boolean isEngulfing(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天,acs[1]-長實體
        int ix2 = days - 2 + nShift; //第2天,acs[0]-短實體

        if (isBearReverse()) //空頭反轉
        {
            if (redBlack[ix1] != 'B') {
                return false; //第一根短實體應反應趨勢[RULE#1]
            }
            if (redBlack[ix2] != 'R') {
                return false; //第2天長實體相反 [#2]
            }
        } else {
            if (redBlack[ix1] != 'R') {
                return false; //第一根短實體應反應趨勢[RULE#1]
            }
            if (redBlack[ix2] != 'B') {
                return false; //第2天長實體相反 [#2]
            }
        }
        if (rateEntity_Price[ix1] > 0.035) {
            return false; // 第1根必須是可以長一點(3.5%)的短實體vs(2%)[#3]
        }
        if (rateEntity_Price[ix2] < 0.06) {
            return false; // 第2根必須是比較長一點(6%)的長實體vs(5%)[#4]
        }
        if (!(topEntity[ix2] >= topEntity[ix1]
                && botEntity[ix2] <= botEntity[ix1])) {
            return false; // 第2根實體完全吞噬第2根實體[#5a]
        }
        if ((topEntity[ix2] == topEntity[ix1]
                && botEntity[ix2] == botEntity[ix1])) {
            return false; // 兩個實體的上端和下端不能皆相等[#5b]
        }
        if (lenEntity[ix1] * 1.3 > lenEntity[ix2]) {
            return false; // 兩支線形的實體至少要相差30%以上(強力陰陽線IIIp69)[#6]
        }
        if (isBearReverse()) {
            kType = "+多頭吞噬";
        } else {
            kType = "-空頭吞噬";
        }
        return true;
    }

    /**
     * 晨星/夜星
     */
    protected boolean isStar(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (!isLong(ix1)) {
            return false;// 第1根必需為長實體(#1)
        }
        if (rateEntity_Price[ix2] > 0.035) {
            return false; // 第2根必須是短實體(#2)
        }
        if (isBearReverse()) //空頭反轉--晨星
        {
            if (redBlack[ix1] != 'B') {
                return false; //第1根短實體應反應趨勢 (#3)
            }
            if (redBlack[ix3] != 'R') {
                return false; //第3根顏色應與第1根相反 (#4)
            }
            if (topEntity[ix3] <= botEntity[ix1]) {
                return false; // 第3根實體應該穿入第1根實體 (#5)
            }
            if (!(topEntity[ix2] < botEntity[ix1] && topEntity[ix2] < botEntity[ix3])) {
                return false; // 第2根實體與第1,3根實體都必須有Gap(#6)
            }
        } else //夜星
        {
            if (redBlack[ix1] != 'R') {
                return false; //第1根短實體應反應趨勢(#3)
            }
            if (redBlack[ix3] != 'B') {
                return false; //第3天顏色應與第1根相反(#4)
            }
            if (botEntity[ix3] >= topEntity[ix1]) {
                return false; // 第3根實體應該穿入第1根實體(#5)
            }
            if (!(botEntity[ix2] > topEntity[ix1] && botEntity[ix2] > topEntity[ix3])) {
                return false; // 第2根實體與第1,3根實體都必須有Gap(#6)
            }
        }

        if (isBearReverse()) {
            kType = "+晨星";
        } else {
            kType = "-夜星";
        }
        if (isCross(ix2)) {
            kType += "十字";
        }

        return true;
    }

    /**
     * 物極必反
     */
    protected boolean isSqueezeAlert(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (isBearReverse()) //空頭反轉
        {
            if (redBlack[ix1] != 'B') {
                return false; //第一根實體應反應趨勢
            }
        } else {
            if (redBlack[ix1] != 'R') {
                return false; //第一根實體應反應趨勢
            }
        }
        //if (rateEntity_Price[ix] < 0.05) 
        //	return false; //第一支最好是長線型，不過沒有嚴格要求
        if (!(acs[ix2][PHIGH] < acs[ix1][PHIGH]
                && acs[ix3][PHIGH] < acs[ix2][PHIGH])) {
            return false; // 高點需每日往下
        }
        if (!(acs[ix2][PLOW] > acs[ix1][PLOW]
                && acs[ix3][PLOW] > acs[ix2][PLOW])) {
            return false; // 低點需每日往上
        }
        if (isBearReverse()) {
            kType = "+物極必反";
        } else {
            kType = "-物極必反";
        }
        return true;
    }

    /**
     * 內困三日翻紅或翻黑
     */
    protected boolean is3Inside(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (!isHarami(1)) {
            return false; // 前兩天需為母子
        }
        if (kType.substring(0, 1).equals("+")) //多頭母子或多頭母子十字
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] > acs[ix2][PPRICE]) // 第3日價格收高
            {
                kType = "+內困三日翻紅";
                return true;
            }
        } else //空頭母子或空頭母子十字
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] < acs[ix2][PPRICE]) // 第3日價格收低
            {
                kType = "-內困三日翻黑";
                return true;
            }
        }
        return false;
    }

    /**
     * 外側三日上升或下跌
     */
    protected boolean is3Outside(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (!isEngulfing(1)) {
            return false; // 前兩天需為吞噬
        }
        if (kType.substring(0, 1).equals("+")) //多頭吞噬
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] > acs[ix2][PPRICE]) // 第3日價格收高
            {
                kType = "+外側三日上升";
                return true;
            }
        } else //空頭吞噬
        {
            kType = ""; //清除判斷前日型態的結果
            if (acs[ix3][PPRICE] < acs[ix2][PPRICE]) // 第3日價格收高
            {
                kType = "-外側三日下降";
                return true;
            }
        }
        return false;
    }

    /**
     * 三白兵或三烏鴉
     */
    protected boolean is3Soldiers(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (!(isLong(ix1) && isLong(ix2) && isLong(ix3))) {
            return false; // 需為三支長實體
        }
        if (!(acs[ix2][POPEN] > botEntity[ix1]
                && acs[ix2][POPEN] < topEntity[ix1]
                && acs[ix3][POPEN] > botEntity[ix2]
                && acs[ix3][POPEN] < topEntity[ix2])) {
            return false; // 每一天開盤需落在前一天實體內
        }
        if (acs[ix1][SC_MA10] < 0) //空頭反轉
        {
            if (!(redBlack[ix1] == 'R' && redBlack[ix2] == 'R'
                    && redBlack[ix3] == 'R')) {
                return false; // 需為三支紅K
            }
            if (!(acs[ix2][PPRICE] > acs[ix1][PPRICE]
                    && acs[ix3][PPRICE] > acs[ix2][PPRICE])) {
                return false; // 每天都創新高收盤價
            }
            if (!(acs[ix1][PHIGH] < acs[ix1][PPRICE] * 1.02
                    && acs[ix2][PHIGH] < acs[ix2][PPRICE] * 1.02
                    && acs[ix3][PHIGH] < acs[ix3][PPRICE] * 1.02)) {
                return false; // 每支線形的收盤價落在最高價附近
            }
            kType = "+三白兵";
        } else {
            if (!(redBlack[ix1] == 'B' && redBlack[ix2] == 'B'
                    && redBlack[ix3] == 'B')) {
                return false; // 需為三支黑K
            }
            if (!(acs[ix2][PPRICE] < acs[ix1][PPRICE]
                    && acs[ix3][PPRICE] < acs[ix2][PPRICE])) {
                return false; // 每天都創新低收盤價
            }
            if (!(acs[ix1][PLOW] > acs[ix1][PPRICE] * 0.98
                    && acs[ix2][PLOW] > acs[ix2][PPRICE] * 0.98
                    && acs[ix3][PLOW] > acs[ix3][PPRICE] * 0.98)) {
                return false; // 每支線形的收盤價落在最低價附近
            }
            if (acs[ix2][POPEN] > acs[ix1][PPRICE] * 0.98
                    && acs[ix3][POPEN] > acs[ix2][PPRICE] * 0.98) {
                kType = "-三胎鴉"; // 每天開盤接近昨天收盤
            } else {
                kType = "-三烏鴉";
            }
        }
        return true;
    }

    /**
     * 雙鴨躍空 或 雙兔跳空
     */
    protected boolean isGapTwo(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;

        if (acs[ix1][SC_MA10] < 0) //空頭反轉
        {
            if (!(isLong(ix1) && redBlack[ix1] == 'B')) {
                return false; // 第1根為長黑(#1)
            }
            if (!(redBlack[ix2] == 'R' && redBlack[ix3] == 'R')) {
                return false; // 第2,3根為紅K(#2)
            }
            if (!(botEntity[ix1] > topEntity[ix2])) {
                return false; // 第2根需向下跳空9(#3)
            }
            if (!(botEntity[ix1] > topEntity[ix3])) {
                return false; // 第3根與第1根也需跳空(#4)
            }
            if (!(topEntity[ix3] > topEntity[ix2]
                    && botEntity[ix3] < botEntity[ix2])) {
                return false; // 第3根實體吞噬第2根(#5)
            }
            if (!(acs[ix3][PHIGH] > acs[ix2][PHIGH]
                    && acs[ix3][PLOW] < acs[ix2][PLOW])) {
                return false; // 第3根高低點吞噬第2根高低點(#6)
            }
            if ((botEntity[ix1] - topEntity[ix2]) <= lenHL[ix1] * 0.1) {
                return false; // 第1,2根的缺口需大於第1天價格區間的10%(#7)
            }
            kType = "+雙兔跳空";

        } else // 雙鴨躍空
        {
            if (!(isLong(ix1) && redBlack[ix1] == 'R')) {
                return false; // 第1根為長紅(#1)
            }
            if (!(redBlack[ix2] == 'B' && redBlack[ix3] == 'B')) {
                return false; // 第2,3根為黑K(#2)
            }
            if (!(botEntity[ix2] > topEntity[ix1])) {
                return false; // 第2根需向上跳空(#3)
            }
            if (!(botEntity[ix3] > topEntity[ix1])) {
                return false; // 第3根與第1根也需跳空(#4)
            }
            if (!(topEntity[ix3] > topEntity[ix2]
                    && botEntity[ix3] < botEntity[ix2])) {
                return false; // 第3根實體吞噬第2根(#5)
            }
            kType = "-雙鴨躍空";
        }
        return true;
    }

    /**
     * 步步為營
     *
     * @param nShift
     * @return
     */
    protected boolean isDelibration(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift;
        double gap;

        if (!(isShort(ix3) && lenEntity[ix3] < lenEntity[ix2] * 0.5)) {
            return false; // 第3跟為短實體且實體小於第2根實體50%(#1)
        }
        if ((lenHL[ix3] < lenHL[ix2] * 0.75)) {
            return false; // 第3根交易區間明顯小於第2根，不到其75%(#2)
        }
        if (acs[ix1][SC_MA10] < 0) //空頭反轉，多頭步步為營
        {
            if (!(isLong(ix1) && redBlack[ix1] == 'B')) {
                return false; // 第1根為長黑(#3)
            }
            if (!(isLong(ix2) && redBlack[ix2] == 'B')) {
                return false; // 第2根也必須是長黑(#4)
            }
            gap = botEntity[ix2] - topEntity[ix3];
            if (!(gap > 0 && gap < lenHL[ix2] * 0.2)) {
                return false; // 第2根與第3根有缺口，但小於第2根價格區間的20%(#5)
            }
            kType = "+步步為營";
        } else //空頭步步為營
        {
            if (!(isLong(ix1) && redBlack[ix1] == 'R')) {
                return false; // 第1根為長紅(#3)
            }
            if (!(isLong(ix2) && redBlack[ix2] == 'R')) {
                return false; // 第2根也必須是長紅(#4)
            }
            gap = botEntity[ix3] - topEntity[ix2];
            if (!(gap > 0 && gap < lenHL[ix2] * 0.2)) {
                return false; // 第2根與第3根有缺口，但小於第2根價格區間的20%(#5)
            }
            kType = "-步步為營";
        }
        return true;
    }

    /**
     * 隔離線 或 分割線
     */
    protected boolean isSeparatingLines(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天

        if (isShort(ix1) || isShort(ix2)) // 若要求線形為長實體可能會找不到，所以只要求非為短實體
        {
            return false;
        }

        if (redBlack[ix1] == redBlack[ix2]) // 第1支線形與第1支線形顏色需相反
        {
            return false;
        }
        if (acs[ix1][POPEN] != acs[ix2][POPEN]) // 兩支線形開盤價需相同
        {
            return false;
        }
        if (isBull(days)) //多頭
        {
            if (redBlack[ix1] != 'B') // 第1根與既有趨勢相反
            {
                return false;
            }
            if (rateDnShadowHL[ix2] > 0.02) {
                return false; // 第2支線需為多頭執帶(沒有下影線), 但幾乎沒有，所以放寬下影線限制
            }			//if (acs[ix2][POPEN] != acs[ix2][PLOW]) // 第2支線需為多頭執帶(沒有下影線)
            //	return false;
        } else //空頭
        {
            if (redBlack[ix1] != 'R') // 第1根與既有趨勢相反
            {
                return false;
            }
            if (rateUpShadowHL[ix2] > 0.02) {
                return false; // 第2支線需為空頭執帶(沒有上影線), 但幾乎沒有，所以放寬上影線限制
            }			//if (acs[ix2][POPEN] != acs[ix2][PHIGH]) // 第2支線需為空頭執帶(沒有上影線)
            //	return false;
        }
        if (isBull(days)) {
            kType = "+隔離線";
        } else {
            kType = "-隔離線";
        }
        return true;
    }

    /**
     * 頸上線、頸內線、戳入線
     */
    protected boolean isThrusing(int nShift) {
        int days = 2; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天

        if (!(rateEntity_HL[ix1] >= 0.5 && rateEntity_HL[ix2] >= 0.5)) {
            return false; // 第1,2支線形實體必須夠長(>價格區間50%)
        }
        if (isBull(days + nShift)) //多頭
        {
            if (!(redBlack[ix1] == 'R' && redBlack[ix2] == 'B')) // 第1根長紅，第二根為黑K
            {
                return false;
            }
            if (rateDnShadowHL[ix2] > 0.02) {
                return false; // 第2支線需為多頭執帶(沒有下影線), 稍微放寬影線限制不然會很少
            }
            if (acs[ix2][POPEN] <= acs[ix1][PHIGH]) {
                return false; // 第2根開盤 > 第1根最高價
            }
            if (acs[ix2][PPRICE] == acs[ix1][PHIGH]) {
                kType = "+頸上線";
            } else if (acs[ix2][PPRICE] > acs[ix1][PPRICE]) {
                return false; // 第2支收盤必須 <= 第1支收盤
            } else if (acs[ix2][PPRICE] > (acs[ix1][PPRICE] - lenEntity[ix1] * 0.2)) {
                kType = "+頸內線";
            } else if (acs[ix2][PPRICE] > mid_Entity[ix1]) {
                kType = "+戳入線"; // 第2根收於第1根收盤~實體中點
            } else {
                return false;
            }

        } else //空頭
        {
            if (!(redBlack[ix1] == 'B' && redBlack[ix2] == 'R')) // 第1根長黑，第二根為紅K
            {
                return false;
            }
            if (rateUpShadowHL[ix2] > 0.02) {
                return false; // 第2支線需為空頭執帶(沒有上影線), 稍微放寬影線限制不然會很少
            }
            if (acs[ix2][POPEN] >= acs[ix1][PLOW]) {
                return false; // 第2根開盤 < 第1根最低價
            }
            if (acs[ix2][PPRICE] == acs[ix1][PLOW]) {
                kType = "-頸上線";
            } else if (acs[ix2][PPRICE] < acs[ix1][PPRICE]) {
                return false; // 第2支收盤必須 >= 第1支收盤
            } else if (acs[ix2][PPRICE] < (acs[ix1][PPRICE] + lenEntity[ix1] * 0.2)) {
                kType = "-頸內線";
            } else if (acs[ix2][PPRICE] < mid_Entity[ix1]) {
                kType = "-戳入線"; // 第2根收於第1根收盤~實體中點
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 上、下肩帶缺口
     */
    protected boolean isTasukiGap(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift; //第3天

        if (isBull(days + nShift)) //多頭
        {
            if (!(redBlack[ix1] == 'R' && redBlack[ix2] == 'R' && redBlack[ix3] == 'B')) {
                return false; // 第1,2根為紅K，第3根為黑K
            }
            if (!(botEntity[ix2] > topEntity[ix1])) {
                return false; // 第2根跳空
            }
            if (!(acs[ix3][PPRICE] < botEntity[ix2] && acs[ix3][PPRICE] > topEntity[ix1])) {
                return false; // 第3根收在缺口內(不碰觸影線)
            }
            if (!(acs[ix3][POPEN] > botEntity[ix2] && acs[ix3][POPEN] < topEntity[ix2])) {
                return false; // 第3根開盤在第2根實體內
            }
            kType = "+上肩帶缺口";
        } else //空頭
        {
            if (!(redBlack[ix1] == 'B' && redBlack[ix2] == 'B' && redBlack[ix3] == 'R')) {
                return false; // 第1,2根為黑K，第3根為紅K
            }
            if (!(topEntity[ix2] < botEntity[ix1])) {
                return false; // 第2根跳空
            }
            if (!(acs[ix3][PPRICE] > topEntity[ix2] && acs[ix3][PPRICE] < botEntity[ix1])) {
                return false; // 第3根收在缺口內(不碰觸影線)
            }
            if (!(acs[ix3][POPEN] > botEntity[ix2] && acs[ix3][POPEN] < topEntity[ix2])) {
                return false; // 第3根開盤在第2根實體內
            }
            kType = "-下肩帶缺口";
        }
        return true;
    }

    /**
     * 偃鼓息兵
     */
    protected boolean isRestAftBattle(int nShift) {
        int days = 3; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift; //第3天

        if (isBull(days + nShift)) //多頭
        {
            if (!(redBlack[ix1] == 'R' && isLong(ix1) && isShort(ix2) && isShort(ix3))) {
                return false; // 第1根為長紅，第2,3根為短實體
            }
            if (!(acs[ix2][PPRICE] < acs[ix1][PHIGH] && acs[ix2][PPRICE] > mid_HL[ix1])) {
                return false; // 第2根收盤在第1根價格區間上半部
            }
            if (!(acs[ix3][PPRICE] < acs[ix1][PHIGH] && acs[ix3][PPRICE] > mid_HL[ix1])) {
                return false; // 第3根收盤在第1根價格區間上半部
            }
            if (!(acs[ix3][PLOW] > mid_HL[ix1])) {
                return false; // 第3根低點必須高於第1根價格區間中點
            }
            if (!(topEntity[ix2] > acs[ix1][PPRICE])) {
                return false; // 第2根時體上端高於第1根收盤
            }
            if (!(acs[ix2][PLOW] < acs[ix1][PHIGH])) {
                return false; // 第2根最低價必須低於第1根最高價
            }
            if (!(acs[ix3][POPEN] < acs[ix2][PHIGH] && acs[ix3][POPEN] > acs[ix2][PLOW])) {
                return false; // 第3根開盤需在第2根價格區間
            }
            if (!(acs[ix3][PPRICE] < acs[ix2][PHIGH] && acs[ix3][PPRICE] > acs[ix2][PLOW])) {
                return false; // 第3根收盤需在第2根價格區間
            }
            kType = "+偃鼓息兵";
        } else //空頭
        {
            return false; // 沒有對應空頭型態
        }
        return true;
    }

    /**
     * 上升三法/下降三法
     */
    protected boolean isThreeMethod(int nShift) {
        int days = -1; // 型態日數(變動，但最少5天)
        int ix1; // 第1天

        // 確認天數--尋找第一根長實體	
        for (int i = 1; i < MAX_NUM - 1; i++) {
            if (isLong(i)) {
                days = i + 1;
                break;
            }
        }
        if (days < 5) {
            return false; // 至少要5天
        }
        ix1 = days - 1;

        for (int i = 1; i < days - 1; i++) {
            if (topEntity[i] > acs[ix1][PHIGH] * 1.005 || botEntity[i] < acs[ix1][PLOW] * 0.995) {
                return false; // 第1根與第2根之間的實體必須在第1根的價格區間(高低點)之間(可稍為超越)
            }
        }
        if (isBull(days + nShift)) //多頭
        {
            if (!(redBlack[0] == 'R' && acs[0][POPEN] > acs[1][PPRICE])) {
                return false; // 最後一根為紅K，開盤價高於前一天收盤
            }
            if (!(isLong(ix1) && redBlack[ix1] == 'R')) {
                return false; // 第1根必須實長紅
            }
            if (!(acs[0][PPRICE] > acs[ix1][PPRICE])) {
                return false; // 最後1根收盤必須 > 於第1根
            }
            kType = "+上升三法";
        } else //空頭
        {
            if (!(redBlack[0] == 'B' && acs[0][POPEN] < acs[1][PPRICE])) {
                return false; // 最後一根為黑K，開盤價低於前一天收盤
            }
            if (!(isLong(ix1) && redBlack[ix1] == 'B')) {
                return false; // 第1根必須實長黑
            }
            if (!(acs[0][PPRICE] < acs[ix1][PPRICE])) {
                return false; // 最後1根收盤必須 < 於第1根
            }
            kType = "-下降三法";
        }
        return true;
    }

    /**
     * 執墊
     */
    protected boolean isMatHold(int nShift) {
        int days = 5; // 型態日數
        int ix1 = days - 1 + nShift; //第1天
        int ix2 = days - 2 + nShift; //第2天
        int ix3 = days - 3 + nShift; //第3天
        int ix4 = days - 4 + nShift; //第4天
        int ix5 = days - 5 + nShift; //第5天

        if (isBull(days + nShift)) //多頭
        {
            if (!(redBlack[ix1] == 'R' && isLong(ix1)
                    && redBlack[ix5] == 'R' && isLong(ix5))) {
                return false; // 第1,5根都是長紅
            }
            if (!(acs[ix5][POPEN] > acs[ix4][PPRICE])) {
                return false; // 第5根開盤高於前一天收盤
            }
            if (!(botEntity[ix2] > topEntity[ix1] && redBlack[ix2] == 'B')) {
                return false; // 第2根收黑但與前一天實體存在缺口
            }
            if (!(acs[ix3][PPRICE] < acs[ix2][PPRICE] && acs[ix4][PPRICE] < acs[ix3][PPRICE])) {
                return false; // 第3,4根收盤越來越低
            }
            if (!(acs[ix3][PPRICE] < topEntity[ix1] && acs[ix4][PPRICE] > botEntity[ix1])) {
                return false; // 第3,4根收盤在第1根線形的實體內
            }
            for (int i = 1; i < days; i++) {
                if (!(acs[0][PPRICE] > acs[i][PHIGH])) {
                    return false; // 最後1根收盤高於前面幾根的最高點,[0]為最後1根
                }
            }

            kType = "+多頭執墊";
        } else //空頭
        {
            if (!(redBlack[ix1] == 'B' && isLong(ix1)
                    && redBlack[ix5] == 'B' && isLong(ix5))) {
                return false; // 第1,5根都是長紅
            }
            if (!(acs[ix5][POPEN] < acs[ix4][PPRICE])) {
                return false; // 第5根開盤低於前一天收盤
            }
            if (!(topEntity[ix2] < botEntity[ix1] && redBlack[ix2] == 'R')) {
                return false; // 第2根收紅但與前一天實體存在缺口
            }
            if (!(acs[ix3][PPRICE] > acs[ix2][PPRICE] && acs[ix4][PPRICE] > acs[ix3][PPRICE])) {
                return false; // 第3,4根收盤越來越高
            }
            if (!(acs[ix3][PPRICE] > botEntity[ix1] && acs[ix4][PPRICE] < topEntity[ix1])) {
                return false; // 第3,4根收盤在第1根線形的實體內
            }
            for (int i = 1; i < days; i++) {
                if (!(acs[0][PPRICE] < acs[i][PLOW])) {
                    return false; // 最後1根收盤低於前面幾根的最低點,[0]為最後1根
                }
            }

            kType = "-空頭頭執墊";
        }
        return true;
    }

    /* ---- copy modal
     protected boolean isDelibration(int nShift)
     {
     int days = 3; // 型態日數
     int ix1=days-1 + nShift; //第1天
     int ix2=days-2 + nShift; //第2天
     int ix3=days-3 + nShift; 

     if (isBearReverse()) //空頭反轉
     {
     }
     else
     {
     }
     if (isBearReverse()) kType = "+步步為營";
     else kType = "-步步為營";
     return true;
     }
     */
}
