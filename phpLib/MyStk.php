<?php

class MyStk {

    public $oDb;
    public $sCurrDate;
    public $sHistBegdate;
    public $fCostOp;
    public $fCostTax;
    public $sPrivateLoc;
    public $sPublicLoc;
    public $fTotMoney;
    public $iTourDays;
    public $fRiskTot; //總風險(6%=0.06)
    public $fRiskOne; //單筆風險(1%=0.01)

    function __Construct($p_oDb) {
        $this->oDb = $p_oDb;
        $this->getSysParm();
    }

    public static function file_get_contents_utf8($fn) {
        $fn = mb_convert_encoding($fn, "BIG5", "UTF-8");
        $s1 = file_get_contents($fn);
        if (!mb_detect_encoding($s1, "UTF-8"))
            $s1 = mb_convert_encoding($s1, "UTF-8", "BIG5");
        return $s1;
    }

    /**
     * 取得幾天前股票日期
     * @param String $sDate 基準日期字串，如:'2015-03-01'
     * @param int $iDays 天數，注意：1=當日
     * @return String: 幾天前股票日期字串,如:'2015-03-01'
     */
    public function getPrevStockDate($sDate, $iDays) {
        $dteBeg = new DateTime($sDate);
        if ($iDays < 20) {
            $n = 40;
        } else {
            $n = $iDays * 2;
        }
        $dteBeg -> modify("-$n days");

        $sql = sprintf("select dte from stk where stockid ='1101' and " .
                " dte BETWEEN '%s' and '%s' order by dte DESC", $dteBeg->format('Y-m-d'), $sDate);
        $sRet = "";
        $rs = $this->oDb->query($sql);
        $i = 1;

        while ($row = $rs->fetch_row()) {
            $sRet = $row[0];
            //echo "<BR>".$i."= ".$sRet;
            if ($i >= $iDays) {
                break;
            }
            $i++;
        }
        return $sRet;
    }
    public function getNextStockDate($sDate, $iDays) {
        $dteEnd = new DateTime($sDate);
        if ($iDays < 20) {
            $n = 40;
        } else {
            $n = $iDays * 2;
        }
        $dteEnd -> modify("+$n days");
        $sql = sprintf("select dte from stk where stockid ='1101' and " .
                " dte BETWEEN '%s' and '%s' order by dte", $sDate, $dteEnd->format('Y-m-d'));
        $sRet = "";
        $rs = $this->oDb->query($sql);
        $i = 1;
        while ($row = $rs->fetch_row()) {
            $sRet = $row[0];
            //echo "<BR>".$i."= ".$sRet, "iDays=$iDays";
            if ($i >= $iDays) {
                break;
            }
            $i++;
        }
        return $sRet;
    }

    /**
     * 單筆交易的盈虧百分比(計入手續費與證交稅)
     * @param array $row $row=fetch_arrary()
     * @param String $typeId 資券別欄位名稱
     * @param String $buyId 進場價格欄位名稱
     * @param String $currId 收盤價格欄位名稱
     * @param String $saleId 出場價格欄位名稱
     * @return double: 盈虧百分比
     */
    public function calcProfit($row, $typeId, $buyId, $currId, $saleId) {
        $sType = $row[$typeId];
        $cost = $row[$buyId]; //買進或放空價格
        $sale = $row[$saleId]; //出場價
        if ($sale <= 0) {
            $sale = $row[$currId]; //收盤價
        }
        if ($sType == '券') {//券空
            $earn = $cost - $sale;
        } else {
            $earn = $sale - $cost;
        }
        $otherCost = $sale * ($this->fCostOp + $this->fCostTax) +
                $cost * $this->fCostOp;
        $earn -= $otherCost;
        $earnRate = round($earn / $cost * 100, 2);
        return $earnRate;
    }

    /**
     * 取得SYSPARM中的欄位
     */
    public function getSysParm() {
        $sql = "select * from SYSPARM";
        try {
            $rs = $this->oDb->query($sql);
            if ($row = $rs->fetch_array()) {
                $this->sCurrDate = $row["currdate"];
                $this->sHistBegdate = $row["hist_begdate"];
                $this->fCostOp = $row["cost_op"] / 1000.0;
                $this->fCostTax = $row["cost_tax"] / 1000.0;
                $this->sPrivateLoc = $row["private_loc"];
                $this->sPublicLoc = $row["public_loc"];
                $this->fTotMoney = $row["tot_money"];
                $this->iTourDays = $row["tourdays"];
                if (isset($row["risk_tot"]))
                    $this->fRiskTot = $row["risk_tot"];
                if (isset($row["risk_one"]))
                    $this->fRiskOne = $row["risk_one"];
            } else {
                die("沒有SYSPARM紀錄");
            }
        } catch (Exception $e) {
            die('Caught exception: ' . $e->getMessage() . "\n");
        }
    }

}

?>