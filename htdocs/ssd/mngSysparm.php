<?php

define("_TITLE", "系統參數管理"); //網頁的title
define("_GRID_VIEW", "sysparm");
define("_TABLE", "sysparm");
define("_PRIMARY_KEY", "ID"); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "HGrid.php"); //Grid物件繼承
define("_DATAEDIT_PHP", "HDataEdit.php"); //DataEdit物件繼承
define("_SHOW_HTML", ""); //DataEdit畫面檔
define("_TAB1", "一覽"); //Tabs 1的標籤
define("_TAB2", "編輯"); //Tabs 2的標籤,""指沒有明細頁
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
    global $odb;
    $oStk = new MyStk($odb);
    $oGrid = new HGrid($oStk);
    $oGrid->setISAdd(false);
    if ($mode === "") {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("mystk_menu.html"); //or </head></body>
    }
    $oGrid->init("", "", "", "0"); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);

    $sql = sprintf("SELECT * from %s order by ID", _GRID_VIEW);
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
    $sql = "SELECT * from sysparm where id =" . $keys[0] . "";
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
    $oEdit->show(_SHOW_HTML);
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
