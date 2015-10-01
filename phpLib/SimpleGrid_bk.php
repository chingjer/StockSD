<?php

require_once("MyStk.php");

class SimpleGrid {

    public $oStk; // MyStk class
    public $currPageUrl;
    public $oDb; // MySqli class
    public $tableStyle = "border: 1px solid #666666;font-size: small;";
    public $tableHeadStyle = "background-color:#c0c0c0";
    public $tdStyle = "border: 1px solid #666666;font-size: small;";
    //protected $tdStyle = "";
    protected $gridId = ""; //Grid 的 tab label
    protected $detailId = ""; //detail 的 tab label
    protected $colNames;
    protected $rowspanColPos; //array of 要rowspan=2的欄位位置
    protected $hideColPos; //array of 要隱藏的欄位位置
    protected $keysColPos; //array of 單筆的KEY
    protected $numTotCols; //總欄位數=rslt的欄位數+增加的欄位
    protected $numMore; //int 增加的欄位數目
    protected $moreColNames; //String[] of 增加欄位的標題
    protected $appendId; // 新增行是別，若不是""則增加一行
    protected $TH_Colspan; //在Table Head line 前一行用來顯示合併欄，預設為""
    private $strBuf;

    function __Construct($p_oStk) {
        $this->oStk = $p_oStk;
        $this->numMore = -1;
        $this->oDb = $p_oStk->oDb;
        $this->strBuf = "";
        $this->currPageUrl = substr($_SERVER["SCRIPT_NAME"], strrpos($_SERVER["SCRIPT_NAME"], "/") + 1);
        $this->TH_Colspan = "";
        $this->rowspanColPos = array();
    }

    /**
     * 在顯示TABLE一行之前先計算檢查，如果不符合該筆會被ByPass,這個方法通常被override
     * 也可以利用這個方法作一些其他的動作，比如加總、統計等等...
     *
     * @param rs rslt
     * @return true:ok, false: by pass不顯示
     */
    protected function rowEval($row) {
        return true;
    }

    /**
     * override 這個function 以加入額外的資訊，如link
     *
     * @param ix 第幾個Column(從0開始)
     * @param rslt rslt
     * @return 在< TD>之後顯示的內容，可以很複雜到加入jScript
     * @throws SQLException
     */
        protected function getColumn($ix, $row) {
            $v = $row[$ix];
            if (is_numeric($v)){
                if ($v < 0) {
                    return "<font color=red>".$v."</font>";
                }
            }
            return $v;
        }
        /**
         * 欄位名稱
         * @param int $ix:第幾個field
         * @param obj-field $fld
         * @return String: Column Name
         */
        protected function getColumnName($ix,$fld){
            return $fld->name;            
        }
    /**
     * override 這個function 以加入額外的資訊，如[法人買賣超]等
     *
     * @param ix moreColumn的第幾個Column(從0開始)
     * @param rslt rslt
     * @return 在< TD>之後顯示的內容，可以很複雜到加入jScript
     * @throws SQLException
     */
    protected function getMoreColumn($ix, $rslt) {
        return "";
    }

    /**
     * 如果內容太多，可以增加一行
     *
     * @param rslt 查詢後的rslt
     * @param cols 該TABLE的總欄數=FieldColumnNum + moreColumnNum - hideColNum
     * @return 要顯示的字串
     * @throws SQLException
     */
    protected function getAppendLine($ResutSet, $cols) {
        return "";
    }

    public function showReportHead() {
        
    }

    public function setTHColspan($s) {
        $this->TH_Colspan = $s;
    }

    /**
     * Grid初始化
     *
     * @param String pMoreCol: rslt之外多出來的欄位，以","分隔，如"三大法人,近8季獲利成長率"
     * @param String pHideColPos: rslt要隱藏的欄位位置，以","分隔，從0開始，如"3,4"
     * @param String append_id: 要增加一行時其識別名稱，如"基本面概覽"
     * @param String pKeyColPos：必需要有，指定哪幾欄是primary key,如"0,3"，從0開始
     */
    public function init($pMoreCol, $pHideColPos, $append_id, $pKeysColPos) {
        $this->moreColNames = array();
        $this->numMore = 0;
        $this->keysColPos = array();
        if (!("" === $pMoreCol)) {
            $aa = explode(",", $pMoreCol);
            $this->numMore = count($aa);
            for ($i = 0; $i < $this->numMore; $i++) {
                $this->moreColNames[$i] = strtolower(trim($aa[$i]));
            }
        }
        $this->setHide($pHideColPos);
        if (!("" === $pKeysColPos)) {
            $bb = explode(",", $pKeysColPos);
            for ($i = 0; $i < count($bb); $i++) {
                $this->keysColPos[$i] = $bb[$i];
            }
        }
        $this->appendId = $append_id;
    }

    public function setHide($pHideColPos) {
        if (!("" === $pHideColPos)) {
            $bb = explode(",", $pHideColPos);
            $this->hideColPos = array();
            for ($i = 0; $i < count($bb); $i++) {
                $this->hideColPos[$i] = $bb[$i];
            }
        } else {
            $this->hideColPos = array();
        }
    }
    
    public function setRowSpan($sCol) {
        if (!("" === $sCol)) {
            $bb = explode(",", $sCol);
            $this->rowspanColPos = array();
            for ($i = 0; $i < count($bb); $i++) {
                $this->rowspanColPos[$i] = $bb[$i];
            }
        } else {
            $this->rowspanColPos = array();
        }
    }

    public function isHide($jx) {
        $is_hide = false;
        for ($i = 0; $i < count($this->hideColPos); $i++) {
            if ($this->hideColPos[$i] == $jx) {
                $is_hide = true;
                break;
            }
        }
        return $is_hide;
    }
    public function isRowSpan($jx) {
        $isYes = false;
        for ($i = 0; $i < count($this->rowspanColPos); $i++) {
            if ($this->rowspanColPos[$i] == $jx) {
                $isYes = true;
                break;
            }
        }
        return $isYes;
    }

    public function isKey($jx) {
        $is_key = false;
        for ($i = 0; $i < count($this->keysColPos); $i++) {
            if ($this->keysColPos[$i] == $jx) {
                $is_key = true;
                break;
            }
        }
        return $is_key;
    }

    public function showBeforeGrid() {
        
    }

    public function setGridId($id) {
        $this->gridId = $id;
    }

    public function getGridId() {
        return $this->gridId;
    }

    public function getAppendId() {
        return $this->appendId;
    }

    public function getDb() {
        return $this->oDb;
    }

    public function showGrid($title, $rslt) {
        $this->showBeforeGrid();
        $this->strBuf = "";
        if ($title != "") {
            $this->strBuf .= "<p>" . $title;
        }
        if ($numMore == -1) {
            die("在reportToFile()之前先呼叫init(pMoreCol)");
        }
        if ($rslt->num_rows == 0) {
            return "無資料！";
        }
        $this->numCols = $rslt->field_count;
        $this->numTotCols = $this->numCols + $this->numMore;
        //$this->strBuf .= sprintf("<TABLE cellpadding='3' cellspacing='0' border='1' style=\"%s\">", $this->tableStyle);
        //$this->strBuf .= sprintf("<TR style=\"%s\">", $this->tableHeadStyle);
        $this->strBuf .= "<TABLE cellpadding='3' cellspacing='0' border='1'>";
        if ("" != $this->TH_Colspan) {
            $this->strBuf .= sprintf("<TR>%s</TR>", $this->TH_Colspan);
        }
        $this->strBuf .= "<TR>";

        for ($i = 0; $i < $this->numCols; $i++) {
            $fld = $rslt->fetch_field();
            $this->colNames[$i] = $this->getColumnName($i, $fld);
            if (!$this->isHide($i)) {
                if (!$this->isRowSpan($i)){
                    $this->strBuf .= "<th>" . $this->colNames[$i];
                }
            }
        }
        for ($i = 0; $i < $this->numMore; $i++) {
                if ($this->TH_Colspan ==""){
                    $this->strBuf .= "<th>" . $this->moreColNames[$i];
                }
        }
        $this->strBuf .="</TR>\n";
        while ($row = $rslt->fetch_array()) {
            if (!$this->rowEval($row)) {
                continue;
            }
            $this->strBuf .= "<TR onmouseover='selectArow(this);' >";
            for ($i = 0; $i < $this->numCols; $i++) {
                if (!$this->isHide($i)) {
                    /*
                      if ($this->isKey($i)) {
                      $td = "<TD class='KEYS' style=\"" . $this->tdStyle . "\">";
                      } else {
                      $td = "<TD style=\"" . $this->tdStyle . "\">";
                      } */
                    if ($this->isKey($i)) {
                        $td = "<TD class='KEYS'>";
                    } else {
                        $td = "<TD>";
                    }

                    $this->strBuf .= $td . $this->getColumn($i, $row);
                    //echo $this->getColumn($i, $row). " " ;
                }
            }
            for ($i = 0; $i < $this->numMore; $i++) {
                $this->strBuf .= "<TD>" . $this->getMoreColumn($i, $row);
                //$this->strBuf .= "<TD>xx" ;
            }
            $this->strBuf .="</TR>\n";
            if (!"" == $this->appendId) {
                $this->strBuf .= $this->getAppendLine($rslt, $this->numTotCols - count($this->hideColPos));
            }
        }
        $this->strBuf .= "</TABLE>\n" .
                "<INPUT TYPE='HIDDEN' ID='URL' VALUE='" . $this->currPageUrl . "'>\n";

        return $this->strBuf;
    }

}

?>