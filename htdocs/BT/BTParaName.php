<?php

define("_TITLE", "交易系統參數登錄"); //網頁的title
define("_GRID_VIEW", "bt_paraname");
define("_TABLE", "bt_paraname");
define("_PRIMARY_KEY", "sys,subsys"); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "ParaNameGrid.php"); //Grid物件繼承
define("_DATAEDIT_PHP", "HDataEdit.php"); //DataEdit物件繼承
define("_SHOW_HTML", ""); //DataEdit畫面檔
define("_TAB1", "參數名稱一覽"); //Tabs 1的標籤
define("_TAB2", "編輯"); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", ""); //增加的 TABS

require_once("MyStk.php");
require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
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
    $oGrid = new ParaNameGrid($oStk);
    if ($mode === "") {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("BTMenu.html"); //or </head></body>
    }
    $oGrid->init("參數設定", "", "", "0,1");
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);
    $sql = sprintf("SELECT * from %s ", _GRID_VIEW);
    $result = $odb->query($sql);
    if (isset($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (isset($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    $oGrid->showGrid("",$result);
    $result->free();
}

function initHDE() {
    require_once(_DATAEDIT_PHP);
    global $odb;
    $oEdit = new HDataEdit();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);
    return $oEdit;
}

function detailPage() {
    global $odb;
    $oEdit = initHDE();
    $keys = explode(",", $_POST["INFO"]);
    $sql = sprintf("SELECT * from %s where sys = '%s' and subsys='%s'", _TABLE, $keys[0], $keys[1]);
    //echo $sql;
    $result = $odb->query($sql);
    if ($row = $result->fetch_array()) {
        $oEdit->showAuto($result, 1);
        $oEdit->loadData($row);
    }
    $result->free();
}

/**
 * 新增，使用最後一筆記錄為初始值
 * @global mysqli $odb
 */
function doAdd() {
    global $odb;
    $oEdit = initHDE();
    $sql = sprintf("SELECT * FROM %s where sys='candle'", _TABLE);
    $result = $odb->query($sql);
    $oEdit->showAuto($result, 1);
    //--- carry on, 讀進最後一筆資料為預設值
    $num_rows = $result->num_rows;    
    if ($num_rows > 0) {
        $result->data_seek($num_rows - 1);
        if ($row = $result->fetch_array()) {
            $oEdit->loadData($row);
        }
    }
}

function doUpdate($act) {
    $oEdit = initHDE();
    if ($act == "U") {
        $oEdit->updateRow($_POST["INFO"]);
    } else if ($act == "A") {
        $oEdit->insertRow($_POST["INFO"]);
    }
    echo $oEdit->msg;
}

function doDelete() {
    $oEdit = initHDE();
    $oEdit->deleteRow($_POST["INFO"]);
    echo $oEdit->msg;
}

?>
