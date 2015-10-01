<?php

if (is_null($_GET["SYS"])) {
    die("必須提供 SYS=xxxx參數!");
}
$sys = $_GET["SYS"];

define("_TITLE", "[$sys]測試統計"); //網頁的title
define("_GRID_VIEW", "v_bt_stat");
define("_TABLE", "bt_stat");
define("_PRIMARY_KEY", ""); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "BTGrid.php"); //Grid物件繼承
//define("_DATAEDIT_PHP", "HDataEdit.php"); //DataEdit物件繼承
//define("_SHOW_HTML", "sysparm.html"); //DataEdit畫面檔
define("_TAB1", "績效統計"); //Tabs 1的標籤
define("_TAB2", ""); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", "交易明細"); //增加的 TABS

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
}
if ($mode === "") {
    echo "</body></html>";
}

function gridPage($mode) {
    require_once(_GRID_PHP);
    global $odb, $sys;
    $oStk = new MyStk($odb);
    $oGrid = new BTGrid($oStk);
    $oGrid->sys = $sys;
    $oGrid->init("", "", "", ""); //如果是"0",必須打為"0,",這是PHP的Bug
    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);
    $oGrid->currPageUrl .= "?SYS=" . $sys;
    if ($mode === "") {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("BTMenu.html");
    }

    if (empty($_POST["FILTER"])) {
        $oGrid->setFilter("*,*,*"); 
    } else {
        $oGrid->setFilter($_POST["FILTER"]);
    }
    $filt = explode(",", $oGrid->getFilter());
    
    $sWhere = "where sys='$sys' ";
    if ($filt[0] != '*'){//subsys
        $sWhere .= "AND `subsys` = '$filt[0]' ";
    }
    if ($filt[1] != '*'){//datecode
        $sWhere .= "AND `datecode` = '$filt[1]' ";
    }
    if ($filt[2] != '*'){//num
        $sWhere .= "AND `參數編號` = '$filt[2]' ";
    }
    $sOrd = "order by subsys,`參數編號`,datecode";
    $sql = sprintf("SELECT * from %s %s %s", _GRID_VIEW, $sWhere, $sOrd);
    //echo $sql;
    $result = $odb->query($sql);
    if (isset($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (isset($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    //echo "<span style='font-size:large;color:blue;font-weight:bold;'>【" . $sys. "】</span>";

    $oGrid->showGrid("",$result);
    $result->free();
}

?>
