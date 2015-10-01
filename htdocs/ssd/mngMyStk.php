<?php

define("_TITLE", "股票交易管理"); //網頁的title
define("_GRID_VIEW", "v_mystk");
define("_TABLE", "mystk");
define("_PRIMARY_KEY", "ID"); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "MystkGrid.php"); //Grid物件繼承
define("_DATAEDIT_PHP", "MystkHDE.php"); //DataEdit物件繼承
define("_SHOW_HTML", "mystk.html"); //DataEdit畫面檔
define("_TAB1", "交易一覽"); //Tabs 1的標籤
define("_TAB2", "編輯"); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", "損益表,明細,操作說明"); //增加的 TABS

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
    global $odb;
    $oStk = new MyStk($odb);
    $oGrid = new MyStkGrid($oStk);
    if ($mode === "") {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("mystk_menu.html"); //or </head></body>
    }
    $oGrid->init("風險,盈虧,天數,MORE", "", "", "0,"); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);

    if (empty($_POST["FILTER"])) {
        $oGrid->setFilter("1"); //scope=1 is 庫存
    } else {
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt = explode(",", $oGrid->getFilter());
    //********** 改寫這一段來篩選, 輸入畫面 override HGrid's showBeforeGrid()
    if ($filt[0] == "" || $filt[0] == "1") {
        $sWhere = "where `出場價` = 0";
        $sOrd = "order by `進場日` DESC";
    } else {
        $sWhere = "where `出場價` > 0";
        $sOrd = "order by `出場日` DESC";
    }
    $sql = sprintf("SELECT * from %s %s %s", _GRID_VIEW, $sWhere, $sOrd);
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
    $oEdit = new MystkHDE();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $keys = explode(",", $_POST["INFO"]);
    $sql = "SELECT * from mystk where id =" . $keys[0] . "";
    $result = $odb->query($sql);
    if ($row = $result->fetch_array()) {
        $oEdit->show(_SHOW_HTML);
        $oEdit->loadData($row);
    }
    $result->free();
}

function doAdd() {

    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new MystkHDE();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $oEdit->show(_SHOW_HTML);
}

function doUpdate($act) {
    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new MystkHDE();
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
    $oEdit = new MystkHDE();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    $oEdit->deleteRow($_POST["INFO"]);
    echo $oEdit->msg;
}

?>
