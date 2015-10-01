<?php

$str1 = <<<PAGE_HEAD
<html>
<head>
    <meta charset="utf-8">
    <title>損益表</title>
    <link href="/jslib/htm.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div align=left>
<h1>損益表</h1>
PAGE_HEAD;

require_once("dbConfig.php");
require_once("ProfitGrid.php");
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
$odb->set_charset("utf8");
$oStk = new MyStk($odb);
$oGrid = new ProfitGrid($oStk);
$oGrid->initTot();
$str1 .= "<h2>庫存</h2>";
$str1 .= showCalc(ProfitGrid::LONG_BULL);
$str1 .= showCalc(ProfitGrid::SHORT_BULL);
$str1 .= showCalc(ProfitGrid::LONG_BEAR);
$str1 .= showCalc(ProfitGrid::SHORT_BEAR);
$str1 .= "<hr><h2>庫存合計</h2>";
$oGrid->CalcInvTot();
$str1 .= $oGrid->showTotal(ProfitGrid::INV);
$str1 .= "<h2>歷史</h2>";
$str1 .= showCalc(ProfitGrid::HIST);
$str1 .= $oGrid->showGrandTotal();
$str1 .= "</div>";

if ($_POST["AJ_VAL"] == "FILE") {
    $outFile = $oGrid->oStk->sPrivateLoc . $oGrid->oStk->sCurrDate . "交易報告.html";
    $fn = mb_convert_encoding($outFile, "BIG5", "UTF-8");
    $num = file_put_contents($fn, $str1 . "</body></html");
    echo "<font color=red>已經儲存到 $outFile($num 字)!</font>";
} else {
    $str1 .= "<button id='FileProfit'>儲存檔案</button>";
    echo $str1;
}

/**
 * show and calculate total
 * @global ProfitGrid $oGrid
 * @global mysqli $odb
 * @param int $mode, ProfitGrid consts
 * @return string 交易一覽表
 */
function showCalc($mode) {
    global $oGrid, $odb;
    $heading = array("長多", "短多", "長空", "短空", "", "");
    $oGrid->init("盈虧", "9,10", "", "0,"); //如果是"0",必須打為"0,",這是PHP的Bug
    if ($mode == ProfitGrid::LONG_BULL) {
        $sWhere = "where `出場價` = 0 AND substring(`模式`,1,1)='L'";
    } else if ($mode == ProfitGrid::SHORT_BULL) {
        $sWhere = "where `出場價` = 0 AND substring(`模式`,1,1)='A'";
    } else if ($mode == ProfitGrid::LONG_BEAR) {
        $sWhere = "where `出場價` = 0 AND substring(`模式`,1,1)='M'";
    } else if ($mode == ProfitGrid::SHORT_BEAR) {
        $sWhere = "where `出場價` = 0 AND substring(`模式`,1,1)='B'";
    } else if ($mode == ProfitGrid::HIST) {
        $oGrid->setHide("8");
        $sWhere = "where `出場價` > 0";
    } else {
        return "";
    }
    $oGrid->setMode($mode);
    $sOrd = "order by `進場日` DESC";
    $sql = sprintf("SELECT * from v_mystk %s %s", $sWhere, $sOrd);
    $result = $odb->query($sql);
    $content = "";
    if ($result->num_rows > 0) {
        if ($heading[$mode] != "")
            $content = "<h3>" . $heading[$mode] . "</h3>";
        $content .= $oGrid->showGrid("", $result);
        $content .= $oGrid->showTotal($mode);
    }
    $result->free();
    return $content;
}
?>