<?php
/**
 * 可以翻頁的簡單式瀏覽，與HGrid知不同點為<br>
 * <ul>
 * <li>沒有活頁(div)
 * <li>可在不同div中同時存在數個SimplePage頁面
 * </ul>
 * ※注意：必須使用在HGrid.php 的活頁當中，不能單獨使用。
 * @author t.m.Huang
 */

require_once("HGrid.php");
class SimplePage extends HGrid {

    protected $info;
    private $div;

    function __Construct($p_oStk,$div) {
        parent::__Construct($p_oStk);
        $this->div = $div; 
    }
    public function setDiv($divName){
        $this->div = $divName;
    }
    public function getDiv(){
        return $this->div;
    }
    public function showPageCtrl() {
        $div = $this->getDiv();
        if ($div===""){
            die("你必須先setDiv(divName)！");
        }
        $s1 = "";
        $s1 .= "<P>第<INPUT TYPE='TEXT' SIZE='3' ID='PAGE$div' NAME='PAGE$div' VALUE=" .
                $this->page . " onchange=GoToPage('$div',this)>\n";
        $s1 .= "頁/共" . ceil($this->num_rows / $this->page_rows) . "頁 每頁行數 ";
        $s1 .= "<INPUT TYPE='TEXT' size=3 ID='PAGE_ROWS$div' NAME='PAGE_ROWS$div' VALUE=" .
                $this->page_rows . " onchange=ChgPageRows('$div',this)>\n";
        $s1 .= "<button id='NEXTPAGE$div' onclick=doNextPage('$div',this)>下一頁</button> ";
        $s1 .= "<button id='REVPAGE$div'  onclick=doPrevPage('$div',this)>上一頁</button> ";
        
        $s1 .= "<INPUT TYPE='HIDDEN' ID='NUM_ROWS$div' NAME='NUM_ROWS$div' VALUE=" . 
                $this->num_rows . ">\n";
        $s1 .= "<INPUT TYPE='HIDDEN' ID='URL$div' VALUE='" . $this->currPageUrl . "'>\n";
        $s1 .= "<input type='HIDDEN' name='$this->info$div' id='$this->info$div' value=''></div>";
        $s1 .= "<input type='HIDDEN' name='postFilter$div' id='postFilter$div' value=''></div>";
        echo $s1;
        //echo $this->debugHtml($s1);
    }
    public function getDb() {
        return $this->oDb;
    }

    public function showGrid($title,$rslt) {
        $this->showBeforeGrid();
        $this->num_rows = $rslt->num_rows;
        $this->strBuf = "";
        if ($title != "") {
            $this->strBuf .= "<p>" . $title;
        }
        if ($this->numMore == -1) {
            die("在showGrid()之前先呼叫init(pMoreCol)");
        }
        if ($rslt->num_rows == 0) {
            echo "沒有符合的資料！";
            $this->numMore = -1;
            //$this->showPageCtrl();
            return;
        }
        $this->strBuf .= $this->getTableStr($rslt);
        echo $this->strBuf;
        $this->numMore = -1;
        $this->showPageCtrl();
        //echo $this->debugHtml($this->strBuf);
    }

}

?>