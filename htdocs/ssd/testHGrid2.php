<?php

require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
$odb->set_charset("utf8");
$mode = $_POST["MODE"];
if (is_null($mode)) {
    require_once("header.inc");
}
if (is_null($mode) or $mode == "GRID") {
    gridPage();
} else if ($mode == "DETAIL") {
    detailPage();
} else if ($mode == "ADD") {
    doAdd();
} else if ($mode == "SAVE") {
    // $_POST["PAGE"]在mode="SAVE"時為 action, A:新增,U:Update
    doUpdate($_POST["PAGE"]);
} else if ($mode == "DEL") {
    doDelete();
}
if (is_null($mode)) {
    echo "</body></html>";
}

function gridPage() {
    require_once("HGrid.php");

    class OpenWinGrid extends HGrid {

        protected function getColumn($ix, $row) {
            if ($ix == 0) {
                // 鉅亨 HTML5 技術線圖
                $s1 = sprintf("<span class=link style=\"background-color: #ff9933;cursor:pointer\" "
                        . "onclick=openWin(\"http://www.cnyes.com/twstock/html5chart/"
                        . "%s.htm\",\"技術線圖\",event)>%s</span>", $row[0], $row[$ix]);
            } else if ($ix == 1) {
                // 奇摩股市/基本資料/營收獲利
                $s1 = sprintf("<span style=\"background-color: #99ffcc;cursor:pointer\" "
                        . "onclick=openWin(\"https://tw.stock.yahoo.com/d/s/earning_"
                        . "%s.html\",\"營收獲利\",event)>%s</span>", $row[0], $row[$ix]);
            } else if ($ix == 2) {
                // 鉅亨 三大法人近月買賣超
                $s1 = sprintf("<span style=\"background-color:#ffff66;cursor:pointer\" "
                        . "onclick=openWin(\"http://www.cnyes.com/twstock/Institutional/"
                        . "%s.html\",\"三大法人\",event)>%s</span>", $row[0], $row[$ix]);
            } else {
                $s1 = $row[$ix];
            }
            return $s1;
        }

        function showScript() {
            
        }

        function showBeforeGrid() {
            $filt= explode(",", $this->getFilter());
            echo <<<BEFORE_GRID
<div id=filter>
代號範圍：<input type="text" value="$filt[0]" size=4>(如2或23或233)
<button onclick="doFilter()">重新篩選</button> 
<p>                    
</div>            
BEFORE_GRID;
        }

    }

    global $odb;
    $oGrid = new OpenWinGrid($oDb);
    $oGrid->init("", "", "", "0,"); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId("股票代號檔一覽表");
    $oGrid->setDetailId("股票代號明細"); //if no detail use ""
    if (is_null($_POST["FILTER"])){
        $oGrid->setFilter("1");
    }else{
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt= explode(",", $oGrid->getFilter());
    $result = $odb->query("SELECT * from stkidTest where stockid like '" .
            $filt[0] . "%'");
    if (!is_null($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (!is_null($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    $oGrid->showGrid("TestHGrid-2", $result);
    $result->free();
}

function detailPage() {
    require_once("HDataEdit.php");
    global $odb;
    $oEdit = new HDataEdit($odb);
    $keys = explode(",", $_POST["INFO"]);
    $sql = "SELECT * from stkidTest where stockid ='" . $keys[0] . "'";
    $result = $odb->query($sql);
    if ($row = $result->fetch_array()) {
        $oEdit->show("stkid.html");
        $oEdit->loadData($row);
    }
    $result->free();
}

function doAdd() {
    require_once("HDataEdit.php");
    global $odb;
    $oEdit = new HDataEdit($odb);
    $oEdit->show("stkid.html");
}

function doUpdate($act) {
    require_once("HDataEdit.php");
    global $odb;
    $oEdit = new HDataEdit($odb);
    $oEdit->setTable("stkidTest", "stockid");
    if ($act == "U") {
        $oEdit->updateRow($_POST["INFO"]);
    } else if ($act == "A") {
        $oEdit->insertRow($_POST["INFO"]);
    }
    echo $oEdit->msg;
}

function doDelete() {
    require_once("HDataEdit.php");
    global $odb;
    $oEdit = new HDataEdit($odb);
    $oEdit->setTable("stkidTest", "stockid");
    $oEdit->deleteRow($_POST["INFO"]);
    echo $oEdit->msg;
}

?>
