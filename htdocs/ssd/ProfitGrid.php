<?php

require_once("SimpleGrid.php");

class ProfitGrid extends SimpleGrid {

    const LONG_BULL = 0; //庫存-長多
    const SHORT_BULL = 1; //庫存-短多
    const LONG_BEAR = 2; //庫存-長空
    const SHORT_BEAR = 3; //庫存-短空
    const INV = 4; //總庫存 
    const HIST = 5; // 歷史
    const GRANDTOT = 6; // 總計=庫存 + 歷史

    private $aSale; //現值或賣出
    private $aCost; //成本(買進價)
    private $aOtherCost; //其他成本(手續費、稅)
    private $aEarn; //盈虧
    private $aRisk; //風險(停損價-買進價)+otherCost
    private $mode; //目前計算部位，長多、短多...
    //--- 盈虧統計的欄位名稱
    private $typeId = "資券";
    private $buyId = "進場價";
    private $saleId = "出場價";
    private $stpId = "停損價";
    private $currId = "今收";
    private $qtyId = "數量";
    //---
    private $earnRate; //每一筆的盈虧比率

    public function initTot() {
        for ($i = 0; $i <= self::GRANDTOT; $i++) {
            $this->aSale[$i] = 0;
            $this->aCost[$i] = 0;
            $this->aOtherCost[$i] = 0;
            $this->aEarn[$i] = 0;
            $this->aRisk[$i] = 0;
        }
    }

    public function setMode($m) {
        $this->mode = $m;
    }

    protected function getMoreColumn($ix, $row) {
        if ($ix == 0) {
            $eRate = $this->earnRate;
            if ($eRate < 0) {
                $eRate = "<font color=red>" . $eRate . "%</font>";
            } else {
                $eRate .= "%";
            }
            return $eRate;
        } else {
            return "";
        }
    }
    function showGrandTotal(){
        $oStk = $this->oStk;
        $hc1 = "<font color=green>";
        $hc2 = "</font>";
        $gEarn = $this->aEarn[self::INV] + $this->aEarn[self::HIST];
        $gEarnRate = $gEarn * 100 / $oStk->fTotMoney;
        $gRiskTot = $oStk->fTotMoney * $oStk->fRiskTot;
        $remainRisk = $gRiskTot - $this->aRisk[self::INV] + $this->aEarn[self::HIST];
        
        $s1 = sprintf("<hr><h2>資金管理</h2>%s本期開始日%s = %s",$hc1,$hc2,$oStk->sHistBegdate);
        $s1 .= sprintf("<BR>%s期初資金(A)%s = %.2f(千元) ",$hc1,$hc2,$oStk->fTotMoney);
        $s1 .= sprintf("<BR>%s總計最大風險(B)%s = %.2f(千元)(%.0f%s)", 
                $hc1,$hc2,$gRiskTot, $oStk->fRiskTot*100,"%" );
        $s1 .= sprintf("<BR>%s單筆最大風險(C)%s = %.2f(千元)(%.0f%s)", 
                $hc1,$hc2,$oStk->fTotMoney * $oStk->fRiskOne, $oStk->fRiskOne*100,"%" );
        $hc3 = "";
        $hc4 = "";
        if ($gEarn < 0){
            $hc3 = '<font color=red size=+2>';
            $hc4 = '</font>';
        }
        $s1 .= sprintf("<BR>%s庫存加已賣出總盈虧(D)%s =%s %.2f(千元)(%.2f%s)%s", $hc1,$hc2,$hc3,$gEarn,$gEarnRate,"%",$hc4);
        $s1 .= sprintf("<BR>%s未軋平風險(E)%s = %.2f(千元)", $hc1,$hc2, $this->aRisk[self::INV]);
        $s1 .= sprintf("<BR>%s已賣出盈虧(F)%s = %.2f(千元)", $hc1,$hc2, $this->aEarn[self::HIST]);
        
        $remain = $oStk->fTotMoney + $this->aEarn[self::HIST] - $this->aCost[self::INV];
        $s1 .= sprintf("<BR>%s餘額%s = %.2f(千元)【(期初)%.2f + (歷史盈虧)%.2f - (庫存買進成本) %.2f】", $hc1,$hc2, $remain,
                $oStk->fTotMoney , $this->aEarn[self::HIST] , $this->aCost[self::INV]);
        
        $hc3 = "";
        $hc4 = "";
        if ($remainRisk < 0){
            $hc3 = '<font color=red size=+2>';
            $hc4 = '</font>';
        }
        $s1 .= sprintf("<BR>%s尚可承受風險(B - E + F)%s = %s%.2f%s(千元)", $hc1,$hc2,$hc3, $remainRisk,$hc4);
        $s1 .= "<p>";
        return $s1;
    }

    function showTotal($mode) {
        $lineColor = "blue";
        if ($this->aEarn[$mode] < 0) {
            $color = "red";
        } else {
            $color = $lineColor;
        }
        $eRate = $this->aEarn[$mode] * 100 / $this->oStk->fTotMoney;
        if ($mode != ProfitGrid::HIST) {
            $parts = $this->aCost[$mode] * 100 / $this->oStk->fTotMoney;
            $s1 = sprintf("<p style='color:%s'>" .
                    "部位=%.2f%s,成本=%.2f,現值=%.2f,盈虧=<font color=%s>%.2f(%.2f%s)</font>" .
                    "</p>", $lineColor, $parts, "%", $this->aCost[$mode], $this->aSale[$mode], $color, $this->aEarn[$mode], $eRate, "%"
            );
        }else{
            $s1 = sprintf("<p style='color:%s'>" .
                    "成本=%.2f,現值=%.2f,盈虧=<font color=%s>%.2f(%.2f%s)</font>" .
                    "</p>", $lineColor, $this->aCost[$mode], $this->aSale[$mode], $color, $this->aEarn[$mode], $eRate, "%"
            );
            
        }
        return $s1;
    }

    function CalcInvTot() {
        for ($i = 0; $i < self::INV; $i++) {
            $this->aCost[self::INV] += $this->aCost[$i];
            $this->aOtherCost[self::INV] += $this->aOtherCost[$i];
            $this->aSale[self::INV] += $this->aSale[$i];
            $this->aEarn[self::INV] += $this->aEarn[$i];
            $this->aRisk[self::INV] += $this->aRisk[$i];
        }
    }

    /**
     * 檢查是否不要顯示該行的同時作盈虧計算與加總計算
     * @param array $row: fetch_array()
     * @return boolean: tru - :ok, false - pass
     */
    function rowEval($row) {
        $sType = $row[$this->typeId];
        $cost = $row[$this->buyId] * $row[$this->qtyId]; //買進或放空價格
        $sale = $row[$this->saleId] * $row[$this->qtyId]; //出場價
        $stp = $row[$this->saleId] * $row[$this->qtyId]; //出場價當停損價
        if ($sale <= 0) {//庫存
            $sale = $row[$this->currId] * $row[$this->qtyId]; //收盤價
            $stp = $row[$this->stpId] * $row[$this->qtyId]; //停損價
        }
        if ($sType == '券') {//券空
            $earn = $cost - $sale;
            $risk = $stp - $cost;
        } else {
            $earn = $sale - $cost;
            $risk = $cost - $stp; // 如買10,stop=8,則風險為+2
        }
        $otherCost = $sale * ($this->oStk->fCostOp + $this->oStk->fCostTax) +
                $cost * $this->oStk->fCostOp;
        $earn -= $otherCost;
        $risk += $otherCost;
        $this->earnRate = round($earn / $cost * 100, 2);

        $this->aSale[$this->mode] += $sale;
        $this->aCost[$this->mode] += $cost;
        $this->aOtherCost[$this->mode] += $otherCost;
        $this->aEarn[$this->mode] += $earn;
        $this->aRisk[$this->mode] += $risk;

        return true; //not byPass 
    }

}
