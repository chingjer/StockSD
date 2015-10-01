<?php

if (is_null($_GET["SYS"])) {
    die("必須提供 SYS=xxxx參數!");
}
$sys = $_GET["SYS"];

define("_TITLE", "[$sys]交易明細"); //網頁的title
define("_GRID_VIEW", $sys);
define("_TABLE", $sys);
define("_PRIMARY_KEY", ""); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "BTShowDetGrid2.php"); //Grid物件繼承

require_once("MyStk.php");
require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
$odb->set_charset("utf8");
$mode = $_POST["MODE"];
if (is_null($mode) or $mode == "GRID") {
    gridPage($mode);
}

function gridPage($mode) {
    require_once(_GRID_PHP);
    global $odb, $sys;
    
    if (is_null($_POST["DIV"])){
        die("NO POST['DIV']");
    }else{
        $div = $_POST["DIV"];
    }
    $oStk = new MyStk($odb);
    $oGrid = new BTShowDetGrid2($oStk,$div);
    $oGrid->init("", "", "", ""); 
    $oGrid->sys = $sys;
    $oGrid->currPageUrl .= "?SYS=" . $sys;

    if (empty($_POST["FILTER"])) {
        $oGrid->setFilter("1,*"); //scope=1 is 庫存
    } else {
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt = explode(",", $oGrid->getFilter());
    //********** 改寫這一段來篩選, 輸入畫面 override BTShowDetGrid's showBeforeGrid()
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
    }
    if (!is_null($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    $oGrid->showGrid("",$result);
    $result->free();
}

?>
