<?php

require_once("SimpleGrid.php");

class HGrid extends SimpleGrid {

    protected $filter;
    protected $aTabs; //String[] 更多的活頁標籤(如{"操作說明","入會辦法"}
    private $page, $old_page;
    private $page_rows;
    private $num_rows;
    private $isAjax;
    private $isAdd;

    function __Construct($p_oStk) {
        parent::__Construct($p_oStk);
        $this->page = 1;
        $this->old_page = 1;
        $this->page_rows = 10;
        $this->isAjax = false; // not at ajax post step
        $this->aTabs = array();
        $this->isAdd = true;
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

    /**
     * 設定除了$GRID, #detail 以外的其他活頁標籤
     * @param type $sTabs
     */
    public function setTabs($sTabs) {
        if (!("" === $sTabs)) {
            $bb = explode(",", $sTabs);
            $this->aTabs = array();
            for ($i = 0; $i < count($bb); $i++) {
                $this->aTabs[$i] = $bb[$i];
            }
        } else {
            $this->aTabs = array();
        }
    }

    public function getTabs() {
        return $this->aTabs;
    }

    public function showStyle() {
        echo <<<STYLE
<style type="text/css">
    table { $this->tableStyle; }
    th { $this->tableHeadStyle }
    td { $this->tdStyle }
    .link {
        padding: 0px;
        background-color: #000000;
        font-size: small;
        color: #FFFFFF;
        cursor:pointer;
    }
</style>
STYLE;
    }

    public function showScript() {
        
    }

    public function showHead($title) {
        echo "<title>" . $title . "</title>";
        $this->showStyle();
        $this->showScript();
        //echo "</head>\n<body>\n";
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

    public function genCtrlTail($isShow) {
        if ($isShow) {
            echo "<button id='NEXTPAGE' onclick=doNextPage()>下一頁</button> ";
            echo "<button id='REVPAGE'  onclick=doPrevPage()>上一頁</button> ";
        }
        if ($this->isAdd) {
            echo "<button id='btnadd' >新　增</button> ";
        }
        echo "<input type='hidden' name='postFilter' id='postFilter' value=''></div>";
    }

    public function setDetailId($id) {
        $this->detailId = $id;
        if ($id == "")
            $this->isAdd = false;
        else
            $this - $isAdd = true;
    }

    public function getDetailId() {
        return $this->detailId;
    }

    public function getDb() {
        return $this->oDb;
    }

    public function showGrid($rslt) {
        //echo "<br>showGrid() in";
        if (!$this->isAjax) {
            //$this->showHead($title);
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
            echo "沒有符合的資料！";
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
        $this->strBuf .= "<TABLE cellpadding='3' cellspacing='0' border='1'>";
        if ("" != $this->TH_Colspan) {
            $this->strBuf .= sprintf("<TR>%s</TR>", $this->TH_Colspan);
        }
        $this->strBuf .= "<TR>";

        for ($i = 0; $i < $this->numCols; $i++) {
            $fld = $rslt->fetch_field();
            //$this->colNames[$i] = $fld->name;
            $this->colNames[$i] = $this->getColumnName($i, $fld);
            if (!$this->isHide($i)) {
                if (!$this->isRowSpan($i)) {
                    $this->strBuf .= "<th>" . $this->colNames[$i];
                }
            }
        }
        for ($i = 0; $i < $this->numMore; $i++) {
            if ($this->TH_Colspan == "") {
                $this->strBuf .= "<th>" . $this->moreColNames[$i];
            }
        }
        $this->strBuf .="</TR>\n";
        if ($this->page > 1) {
            $pos = ($this->page - 1) * $this->page_rows;
            if (!$rslt->data_seek($pos)) {
                $pos = ($this->old_page - 1) * $this->page_rows;
                $rslt->data_seek($pos);
            }
        }
        //echo "page=".$this->page.",pos=$pos";
        $rx = 1;
        while ($row = $rslt->fetch_array() and $rx <= $this->page_rows) {
            if (!$this->rowEval($row)) {
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