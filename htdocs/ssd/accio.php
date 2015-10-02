<?php
define("_TITLE", "帳戶進出管理"); //網頁的title
define("_GRID_VIEW", "accio");
define("_TABLE", "accio");
define("_PRIMARY_KEY", "ID"); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "HGrid.php"); //Grid物件繼承
define("_DATAEDIT_PHP", "HDataEdit.php"); //DataEdit物件繼承
define("_SHOW_HTML", ""); //DataEdit畫面檔
define("_TAB1", "進出一覽"); //Tabs 1的標籤
define("_TAB2", "資料編輯"); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", ""); //增加的 TABS

require_once("MyStk.php");
require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
$odb->set_charset("utf8");
if (isset($_POST["MODE"])) {
    $mode = $_POST["MODE"];
} else {
    $mode = "";
}

if ($mode === "" or $mode == "GRID") {
    gridPage($mode);
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
if ($mode === "") {
    echo "</body></html>";
}

function gridPage($mode) {
    require_once(_GRID_PHP);
    
// -------- accnoGrid Class --------------
class accnoGrid extends HGrid {
    function showBeforeGrid() {
        $filt = explode(",", $this->getFilter());
        //-----產生[accno]下拉式選單
        $sql = sprintf("select accno,accname from accno order by accno");
        $rs = $this->oDb->query($sql);
        $optAccno = "<option value='*'>ALL</option>";
        $accno = $filt[0];
        while ($row = $rs->fetch_array()) {
            $sel = $row["accno"] == $accno ? " selected " : "";
            $optAccno .= sprintf("<option value='%s' %s>%s %s</option>", $row["accno"], $sel, 
                    $row["accno"],$row["accname"]);
        }
        echo <<<BEFORE_GRID
<div id=filter>
    <font color=brown>帳號：</font>
    <select size=1 id="accno_sel" name="accno_sel">$optAccno</select>
    &nbsp;&nbsp;<button onclick="doFilter()">重新篩選</button> 
    <p>
</div>            
BEFORE_GRID;
    }
}
//----- end of accnoGrid Class -------------------
    
    global $odb;
    $oStk = new MyStk($odb);
    $oGrid = new accnoGrid($oStk);
    //$oGrid->setISAdd(false);
    if ($mode === "") {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("mystk_menu.html"); //or </head></body>
    }
    $oGrid->init("", "", "", "0"); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);
    
    // --- filter section ---
    if (empty($_POST["FILTER"])) {
        $oGrid->setFilter("*"); 
    } else {
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt = explode(",", $oGrid->getFilter());
    $sWhere = "";
    if ($filt[0] != '*'){//subsys
        $sWhere .= "where `account` = '$filt[0]' ";
    }
    // -----
    $sql = sprintf("SELECT * from %s %s order by ID DESC", _GRID_VIEW, $sWhere);
    //echo $sql;
    //**********
    $result = $odb->query($sql);
    if (isset($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (isset($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    $oGrid->showGrid("", $result);
    $result->free();
}

function detailPage() {
    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new HDataEdit();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $keys = explode(",", $_POST["INFO"]);
    $sql = "SELECT * from accio where id =" . $keys[0] . "";
    $result = $odb->query($sql);
    if ($row = $result->fetch_array()) {
        $oEdit->showAuto($result,1);
        $oEdit->loadData($row);
    }
    $result->free();
}

function doAdd() {

    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new HDataEdit();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $sql = sprintf("SELECT * from %s LIMIT 1", _TABLE);
    $result = $odb->query($sql);
    $oEdit->showAuto($result, 1);
}

function doUpdate($act) {
    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new HDataEdit();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    if ($act == "U") {
        $oEdit->updateRow($_POST["INFO"]);
    } else if ($act == "A") {
        $oEdit->insertRow($_POST["INFO"]);
    }
    echo $oEdit->msg;
}

function doDelete() {
    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new HDataEdit();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $oEdit->deleteRow($_POST["INFO"]);
    echo $oEdit->msg;
}

?>
<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

