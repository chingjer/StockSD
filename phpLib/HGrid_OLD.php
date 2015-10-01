<?php
require_once("MyStk.php");
class HGrid {

    protected $oDb;
    protected $oStk;
    protected $rptHeadStyle = "font-size: medum;color: #FF6600;font-weight: bold";
    protected $titleStyle = "font-size: medium;color: #800000;font-weight: normal";
    protected $tableHeadStyle = "background-color:#c0c0c0";
    protected $tableStyle = "border: 1px solid #0000ff;font-size: small;";
    protected $tdStyle = "border: 1px solid #999999;font-size: small;";
    protected $gridId = ""; //Grid 的 tab label
    protected $detailId = ""; //detail 的 tab label
    protected $colNames;
    protected $hideColPos; //array of 要隱藏的欄位位置
    protected $keysColPos; //array of 單筆的KEY
    protected $numTotCols; //總欄位數=rslt的欄位數+增加的欄位
    protected $numMore; //int 增加的欄位數目
    protected $moreColNames; //String[] of 增加欄位的標題
    protected $appendId; // 新增行是別，若不是""則增加一行
    protected $filter;
    protected $aTabs; //String[] 更多的活頁標籤(如{"操作說明","入會辦法"}
    private $strBuf;
    private $page, $old_page;
    private $page_rows;
    private $num_rows;
    private $isAjax;
    private $currPageUrl;
    private $isAdd;

    function __Construct($p_oStk)   {
        $this->oStk = $p_oStk;
        $this->numMore = -1;
        $this->oDb = $p_oStk->oDb;
        $this->strBuf = "";
        $this->page = 1;
        $this->old_page = 1;
        $this->page_rows = 10;
        $this->isAjax = false; // not at ajax post step
        $this->currPageUrl = substr($_SERVER["SCRIPT_NAME"], strrpos($_SERVER["SCRIPT_NAME"], "/") + 1);
        $this->aTabs = array();
        $this->isAdd = true;
    }

    public static function file_get_contents_utf8($fn) {
        $fn = mb_convert_encoding($fn, "BIG5", "UTF-8");
        $s1 = file_get_contents($fn);
        if (!mb_detect_encoding($s1,"UTF-8"))
          $s1 =  mb_convert_encoding($s1, "UTF-8", "BIG5");
        return $s1;
    }

    /**
     * 在顯示之前先檢查，如果符合該筆會被ByPass,這個方法通常被override
     *
     * @param rs rslt
     * @return true:ByPass, false: 正常顯示不跳過,s, 預設是return false
     */
    protected function isByPass($row) {
        return false;
    }

    public function setPage($n) {
        $this->old_page = $this->page;
        $this->page = $n;
    }

    public function getPage() {
        return $this->page;
    }

    public function setIsAjax($tf) {
        $this->isAjax = $tf;
    }

    public function setFilter($new) {
        $this->filter = $new;
    }

    public function getFilter() {
        return $this->filter;
    }

    public function setPageRows($new) {
        $this->page_rows = $new;
    }

    public function setTabs($sTabs) {
        $this->aTabs = explode(",", $sTabs);
    }

    public function getTabs() {
        return $this->aTabs;
    }

    /**
     * override 這個function 以加入額外的資訊，如link
     *
     * @param ix 第幾個Column(從1開始)
     * @param rslt rslt
     * @return 在< TD>之後顯示的內容，可以很複雜到加入jScript
     * @throws SQLException
     */
    protected function getColumn($ix, $row) {
        return $row[$ix];
    }

    /**
     * override 這個function 以加入額外的資訊，如[法人買賣超]等
     *
     * @param ix moreColumn的第幾個Column(從1開始)
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

    /**
     * Grid初始化
     *
     * @param String pMoreCol: rslt之外多出來的欄位，以","分隔，如"三大法人,近8季獲利成長率"
     * @param String pHideColPos: rslt要隱藏的欄位位置，以","分隔，從1開始，如"3,4"
     * @param String append_id: 要增加一行時其識別名稱，如"基本面概覽"
     * @param String pKeyColPos：必需要有，指定哪幾欄是primary key,如"0,3"，從0開始
     */
    public function init($pMoreCol, $pHideColPos, $append_id, $pKeysColPos) {
        if (!"" == $pMoreCol) {
            $aa = explode(",", $pMoreCol);
            $this->numMore = count($aa);
            for ($i = 0; $i < $this->numMore; $i++) {
                $this->moreColNames[$i] = strtolower(trim($aa[$i]));
            }
        } else {
            $this->numMore = 0;
            $this->moreColNames = array();
        }
        if (!"" == $pHideColPos) {
            $bb = explode(",", $pHideColPos);
            for ($i = 0; $i < count($bb); $i++) {
                $this->hideColPos[$i] = $bb[$i];
            }
        } else {
            $this->hideColPos = array();
        }
        if (!"" == $pKeysColPos) {
            $bb = explode(",", $pKeysColPos);
            for ($i = 0; $i < count($bb); $i++) {
                $this->keysColPos[$i] = $bb[$i];
            }
        } else {
            $this->keysColPos = array();
        }
        $this->appendId = $append_id;
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

    public function showStyle() {
        echo <<<STYLE
<style type="text/css">
    table { $this->tableStyle }
    td { $this->tdStyle }
    .link {
        padding: 5px;
        background-color: #ff9933;
        font-size: small;
        color: #FFFFFF;
        cursor:pointer;
    }
</style>
                
</style>
STYLE;
    }

    public function showScript() {
        
    }

    public function showHead($title) {
        echo "<title>" . $title . "</title>";
        $this->showStyle();
        $this->showScript();
        echo "</head>\n<body>\n";
    }

    /**
     * 
     * @param int $ix 除了hgrid,detail之外額外附加的活頁，0指第1個增加的活頁
     * @return String : html內容
     */
    public function showTabs($ix) {
        return "";
    }

    public function showTail() {
        $this->strBuf = "";
        if ("" != $this->getDetailId()) {
            $this->strBuf = "<div id='detail'> </div>";
        }

        for ($i = 0; $i < count($this->aTabs); $i++) {
            $this->strBuf .= sprintf("<div id='%s' >", $this->aTabs[$i]);
            $this->strBuf .= $this->showTabs($i);
            $this->strBuf .= sprintf("</div>");
        }
        $this->strBuf .= "</div>"; //for #tabs
        echo $this->strBuf;
    }

    public function genCtrlHeader() {
        $gridLabel = $this->getGridId();
        $detailLabel = $this->getDetailId();
        echo "<div id='tabs'><ul><li><a href='#hgrid'>$gridLabel</a></li>";
        if ("" != $detailLabel) {
            echo "<li><a href='#detail'>$detailLabel</a></li>";
        }

        for ($i = 0; $i < count($this->aTabs); $i++) {
            echo sprintf("<li><a href='#%s'>%s</a></li>", $this->aTabs[$i], $this->aTabs[$i]);
        }
        echo "</ul><div id='hgrid'>";
    }

    public function showBeforeGrid() {
        
    }

    public function genCtrlTail($isShow) {
        if ($isShow){
            echo "<button id='NEXTPAGE' onclick=doNextPage()>下一頁</button> ";
            echo "<button id='REVPAGE'  onclick=doPrevPage()>上一頁</button> ";
        }
        if ($this->isAdd) {
            echo "<button id='btnadd' >新　增</button> ";
        }
        echo "<input type='hidden' name='postFilter' id='postFilter' value=''></div>";
    }

    public function setGridId($id) {
        $this->gridId = $id;
    }

    public function setDetailId($id) {
        $this->detailId = $id;
        if ($id=="") $this->isAdd = false;
        else $this-$isAdd = true;
    }

    public function getGridId() {
        return $this->gridId;
    }

    public function getDetailId() {
        return $this->detailId;
    }

    public function getDb() {
        return $this->oDb;
    }

    public function getAppendId() {
        return $this->appendId;
    }

    public function showGrid($title, $rslt) {
        //echo "<br>showGrid() in";
        if (!$this->isAjax) {
            $this->showHead($title);
            $this->genCtrlHeader();
        }
        $this->showBeforeGrid();
        $this->num_rows = $rslt->num_rows;
        $this->strBuf = "";
        if ($numMore == -1) {
            die("在reportToFile()之前先呼叫init(pMoreCol)");
        }
        //if ($title != "") {
        //    $this->strBuf = sprintf("<P style=\"%s\">%s</P><P>", $this->titleStyle, $title);
        //}
        if ($rslt->num_rows == 0) {
            echo "沒有符合的資料！</div>";
            echo $this->getHiddenFields();
            $this->numMore = -1;
            $this->genCtrltail(false);
            if (!$this->isAjax) {
                $this->showTail();
            }
            return;
        }
        //echo $this->keysColPos[0]; 
        $this->numCols = $rslt->field_count;
        $this->numTotCols = $this->numCols + $this->numMore;
        $this->strBuf .= sprintf("<TABLE cellpadding='3' cellspacing='0' border='1'><TR style=\"%s\">", $this->tableHeadStyle);
        for ($i = 0; $i < $this->numCols; $i++) {
            $fld = $rslt->fetch_field();
            $this->colNames[$i] = $fld->name;
            if (!$this->isHide($i)) {
                $this->strBuf .= "<td>" . $this->colNames[$i];
            }
        }
        for ($i = 0; $i < $this->numMore; $i++) {
            $this->strBuf .= "<td>" . $this->moreColNames[$i];
        }
        $this->strBuf .="</TR>\n";
        if ($this->page > 1) {
            $pos = ($this->page - 1) * $this->page_rows;
            if ($rslt->data_seek($pos)) {
                $pos = ($this->ols_page - 1) * $this->page_rows;
                $rslt->data_seek($pos);
            }
        }
        //echo "page=".$this->page.",pos=$pos";
        $rx = 1;
        while ($row = $rslt->fetch_array() and $rx <= $this->page_rows) {
            if ($this->isByPass($row)) {
                continue;
            }
            $this->strBuf .= "<TR onmouseover='selectArow(this);' >";
            for ($i = 0; $i < $this->numCols; $i++) {
                if (!$this->isHide($i)) {
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
            }
            $this->strBuf .="</TR>\n";
            if (!"" == $this->appendId) {
                $this->strBuf .= $this->getAppendLine($rslt, $this->numTotCols - count($this->hideColPos));
            }
            $rx++;
        }
        $this->strBuf .= "</TABLE>\n";
        $this->strBuf .= "<P>第<INPUT TYPE='TEXT' SIZE='3' ID='PAGE' NAME='PAGE' VALUE=" .
                $this->page . " onchange=GoToPage()>\n";
        $this->strBuf .= "頁/共" . ceil($this->num_rows / $this->page_rows) . "頁 每頁行數 ";
        $this->strBuf .= "<INPUT TYPE='TEXT' size=3 ID='PAGE_ROWS' NAME='PAGE_ROWS' VALUE=" .
                $this->page_rows . " onchange=ChgPageRows()>\n";
        $this->strBuf .= $this->getHiddenFields();
        echo $this->strBuf;
        $this->numMore = -1;
        $this->genCtrltail(true);
        if (!$this->isAjax) {
            $this->showTail();
        }
    }

    function getHiddenFields() {
        $s1 = "<INPUT TYPE='HIDDEN' ID='NUM_ROWS' NAME='NUM_ROWS' VALUE=" . $this->num_rows . ">\n";
        $s1 .= "<INPUT TYPE='HIDDEN' ID='URL' VALUE='" . $this->currPageUrl . "'>\n";
        return $s1;
    }

}

?>