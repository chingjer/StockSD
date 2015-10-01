<?php

if (!is_null($_POST["AJ_KEY"])) {
    SimplePostSvr();
    exit(0);
}

if (is_null($_GET["sys"]) or is_null($_GET["subsys"])) {
    die("必須傳遞sys,subsys參數");
} else {
    $sys = $_GET["sys"];
    $subsys = $_GET["subsys"];
}
define("_TITLE", "回溯測試參數管理"); //網頁的title
define("_GRID_VIEW", "v_bt_para");
define("_TABLE", "bt_para");
define("_PRIMARY_KEY", "ID"); //注意大小寫，須與資料庫一致
define("_GRID_PHP", "BTparaGrid.php"); //Grid物件繼承
define("_DATAEDIT_PHP", "BTparaHDE.php"); //DataEdit物件繼承
define("_SHOW_HTML", "bt_para.html"); //DataEdit畫面檔
define("_TAB1", "參數一覽"); //Tabs 1的標籤
define("_TAB2", "編輯"); //Tabs 2的標籤,""指沒有明細頁
define("_MORE_TABS", ""); //增加的 TABS

require_once("MyStk.php");
require_once("dbConfig.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
$odb->set_charset("utf8");
$mode = $_POST["MODE"];
if (is_null($mode) or $mode == "GRID") {
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
if (is_null($mode)) {
    echo "</body></html>";
}

function SimplePostSvr() {
    require_once("dbConfig.php");
    $odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
    $odb->set_charset("utf8");
    $key = $_POST["AJ_KEY"];
    $keyval = $_POST["AJ_VAL"];
    if ($key == "PARA_COPY") {
        $sql = "select * from bt_para where id=" . $keyval;
        if ($rslt = $odb->query($sql)) {
            $finfo = $rslt->fetch_fields();
            $srcFlds = "";
            foreach ($finfo as $v) {
                switch (strtolower($v->name)) {
                    case "id":
                        break;
                    case "num":
                        $srcFlds .= ",99";
                        break;
                    default:
                        $srcFlds .= "," . $v->name;
                }//switch
            }//foreach
            $flds = "";
            foreach ($finfo as $v) {
                switch (strtolower($v->name)) {
                    case "id":
                        break;
                    default:    
                        $flds .= "," . $v->name;
                }//switch
            }//foreach
        }//if
        $sql2 = sprintf("insert into bt_para(%s) select %s from bt_para" .
                " where id=%s", substr($flds, 1), substr($srcFlds, 1), $keyval);
        $msg = "";
        if ($odb->query($sql2) === true) {
            $msg = "複製成功！已複製一筆到參數編號=99，請修改該參數編號！";
        } else {
            $msg = "發生錯誤!\n" . $odb->error;
        }
        echo $msg;
    }else if($key == "PARA_DEL"){
        $sql = "delete from bt_para where id=" . $keyval;
        if ($odb->query($sql)=== true){
            $msg = "";
        }else{
            $msg = $odb->error;
        }
        echo $msg;
    }else if($key == "PARA_TOGGLE"){
        $sql = "select enabled from bt_para where id=" . $keyval;
        $rslt=$odb->query($sql);
        if ($row=$rslt->fetch_array()){
            if ($row["enabled"]==1){
                $toggle="0";
            } else{
                $toggle="1";
            }
            $sql = "update bt_para set enabled=". $toggle ." where id=" . $keyval;
            if ($odb->query($sql)=== true){
                $msg = "";
            }else{
                $msg = $odb->error;
            }            
        }else{
            $msg = $odb->error;
        }    
        echo $msg;
    }
}

function gridPage($mode) {
    require_once(_GRID_PHP);
    global $odb, $sys, $subsys;
    $oStk = new MyStk($odb);
    $oGrid = new BTparaGrid($oStk);
    if (is_null($mode)) {
        require_once("header.inc");
        $oGrid->showHead(_TITLE);
        require_once("BTMenu.html"); //or </head></body>
    }

    $oGrid->currPageUrl .= "?sys=" . $sys . "&subsys=" . $subsys;
    $oGrid->sys = $sys;
    $oGrid->subsys = $subsys;

    $oGrid->setGridId(_TAB1);
    $oGrid->setDetailId(_TAB2); //if no detail use ""
    $oGrid->setTabs(_MORE_TABS);

    /*
      if (empty($_POST["FILTER"])) {
      $oGrid->setFilter("1"); //scope=1 is 庫存
      } else {
      $oGrid->setFilter($_POST["FILTER"]);
      }
      $filt = explode(",", $oGrid->getFilter());
      if ($filt[0] == "" || $filt[0] == "1"){
      $sWhere = "where `出場價格` = 0";
      $sOrd = "order by `進場日期` DESC";
      } else{
      $sWhere = "where `出場價格` > 0";
      $sOrd = "order by `出場日期` DESC";
      }
      $sql = sprintf("SELECT * from %s %s %s", _GRID_VIEW, $sWhere, $sOrd);
     */
    $sql = sprintf("SELECT * from %s where sys='%s' AND subsys='%s' ORDER BY NUM DESC", _GRID_VIEW, $sys, $subsys);
    //echo $sql;
    //**********
    $result = $odb->query($sql);

    $oGrid->getVarColNames();
    //var_dump($oGrid->varColNames);
    $hide = $oGrid->getHideColPos($result);
    //echo ",hide=",$hide;
    $oGrid->init("編輯", $hide, "", "0");

    if (!is_null($_POST["PAGE"])) {
        $oGrid->setPage($_POST["PAGE"]);
        $oGrid->setIsAjax(true);
    }
    if (!is_null($_POST["PAGE_ROWS"])) {
        $oGrid->setPageRows($_POST["PAGE_ROWS"]);
    }
    $oGrid->showGrid("", $result);
    $result->free();
}

function initHDE() {
    require_once(_DATAEDIT_PHP);
    global $odb, $sys, $subsys;
    $oEdit = new BTparaHDE();
    $oEdit->init($odb, _TABLE, _PRIMARY_KEY);

    $oEdit->sys = $sys;
    $oEdit->subsys = $subsys;
    $oEdit->getVarColNames();

    return $oEdit;
}

function detailPage() {
    global $odb;
    $oEdit = initHDE();
    $keys = explode(",", $_POST["INFO"]);
    $sql = sprintf("SELECT * from %s where id = %s", _TABLE, $keys[0]);
    $result = $odb->query($sql);
    if ($row = $result->fetch_array()) {
        $oEdit->showAuto($result, 2);
        $oEdit->loadData($row);
    }
    $result->free();
}

/**
 * 新增，使用最後一筆記錄為初始值
 * @global mysqli $odb
 */
function doAdd() {
    global $odb, $sys, $subsys;
    $oEdit = initHDE();
    $sql = sprintf("SELECT * from %s where sys='%s' ORDER BY ID DESC LIMIT 1", _TABLE, $sys);
    //$sql = sprintf("SELECT * from %s where sys='%s' AND subsys='%s' ORDER BY NUM DESC", 
    //        _TABLE,$sys,$subsys);
    //$oEdit->setAddDefault();
    $result = $odb->query($sql);
    $oEdit->showAuto($result, 2);
    if ($row = $result->fetch_array()) {
        $oEdit->loadData($row);
    } else {
        $oEdit->setAddDefault();
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
