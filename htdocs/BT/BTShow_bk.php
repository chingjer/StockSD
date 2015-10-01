<?php

if (is_null($_GET["SYS"])) {
    die("必須提供 SYS=xxxx參數!");
}
$sys = $_GET["SYS"];

define("_TITLE", "[$sys]交易明細"); //網頁的title
define("_GRID_VIEW", $sys);
define("_TABLE", $sys);
define("_PRIMARY_KEY", ""); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "BTGrid.php"); //Grid物件繼承
//define("_DATAEDIT_PHP", "HDataEdit.php"); //DataEdit物件繼承
//define("_SHOW_HTML", "sysparm.html"); //DataEdit畫面檔
define("_TAB1", "交易一覽表"); //Tabs 1的標籤
define("_TAB2", ""); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", "績效統計"); //增加的 TABS

require_once("MyStk.php");
require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
$odb->set_charset("utf8");
$mode = $_POST["MODE"];
if (is_null($mode) or $mode == "GRID") {
    gridPage($mode);
}
if (is_null($mode)) {
    echo "</body></html>";
}

function gridPage($mode) {
    require_once(_GRID_PHP);
    global $odb, $sys;
    $oStk = new MyStk($odb);
    $oGrid = new BTGrid($oStk);
    if (is_null($mode)) {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("BTMenu.html");
    }
    $oGrid->init("", "", "", ""); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);
    $oGrid->sys = $sys;

    //---- Add URL GET
    $oGrid->currPageUrl .= "?SYS=" . $sys;

    if (empty($_POST["FILTER"])) {
        $oGrid->setFilter("1,*"); //scope=1 is 庫存
    } else {
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt = explode(",", $oGrid->getFilter());
    //********** 改寫這一段來篩選, 輸入畫面 override HGrid's showBeforeGrid()
    if ($filt[0] == "" || $filt[0] == "1") {
        $sWhere = "AND `p_stop` > 0 ";
        $sOrd = "order by `profit` DESC";
    } else {
        $sWhere = "";
        $sOrd = "order by dte, stockid";
    }
    if ($filt[1] != '*'){
        $sWhere .= "AND `datecode` = '$filt[1]' ";
    }
    if (!($sWhere==="")){
        $sWhere = "where " .substr($sWhere,3);
    }
    $sql = sprintf("SELECT * from %s %s %s", _GRID_VIEW, $sWhere, $sOrd);

    //echo $sql;
    $result = $odb->query($sql);
    if (!is_null($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (!is_null($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    echo "<span style='font-size:large;color:blue;font-weight:bold;'>【" . $sys. "】</span>";
    $oGrid->showGrid($result);
    $result->free();
}

?>
