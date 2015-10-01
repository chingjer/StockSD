<?php

require_once("dbConfig.php");
require_once("SimpleGrid.php");
if (is_null($_POST["AJ_VAL"])) {
    die("you must post a stockid!");
}
$stockid = $_POST["AJ_VAL"];
$odb = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
$odb->set_charset("utf8");
$oStk = new MyStk($odb);
$oGrid = new SimpleGrid($oStk);

$content = <<<PAGE_HEAD
        
<html>
<head>
    <meta charset="utf-8">
    <title>個股明細</title>
    <link href="/jslib/htm.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="showmore" align=left>
        
<table style="border: 1px solid #0000ff;">
<tr><td style="width:600px;border:none;font-size: large">
<label for="sm_stockid">股票代號: </label>
<input type=text name='sm_stockid' id='sm_stockid' size=10 value='$stockid' 
        onkeyup="doCompletion('sm_stockid','sm_stockid_sel', 'sm_stockid_show','ajaxIdSelSvr.php')" 
        onchange="doShowName('sm_stockid','sm_stockid_sel', 'sm_stockid_show','ajaxIdSelSvr.php')"> 
<span id="sm_stockid_show" style="displa:inline">直接打入代碼，或部分中文+空白叫出選單
</span>
<select name="sm_stockid_sel" id="sm_stockid_sel" size="1" style="display: none" onchange="doSelChange('sm_stockid','sm_stockid_sel','sm_stockid_show' )">
</select> <td  style="width:100px;border:none;font-size: large">
<button id='sm_btn'>查詢</button></tr></table><P>

PAGE_HEAD;

$content.= "<h2>股票明細一覽表</h2>";
//---獲利營收
$headStyle = "<p style='font-size:medium;color:blue;font-weight:bold'>";
$oGrid->init("", "", "", ""); //如果是"0",必須打為"0,",這是PHP的Bug
$sql = sprintf("SELECT * from v_basic1 where stockid = '%s'", $stockid);
$result = $odb->query($sql);
$content .= $oGrid->showGrid($headStyle . "獲利營收</p>", $result);
$result->free();

//---籌碼
$sql = sprintf("SELECT * from v_basic2 where stockid = '%s'", $stockid);
$result = $odb->query($sql);
$oGrid->setHide("0");
//$oGrid->setRowSpan("0");
$oGrid->setTHColspan("<th colspan=3>三大法人" .
        "<th colspan=3>投信<th colspan=3>外資<th  colspan=3>自營");
$content .= $oGrid->showGrid($headStyle . "籌碼</p>", $result);
//echo str_replace( "<","< ",$content);
$content .= "<span style='font-size:small;color:brown'>※idx=[5日買超天/賣超,20日買超天/賣超]; t5=5日買超張數</span>";
$result->free();

$sBegDte = $oStk->getPrevStockDate($oStk->sCurrDate, 20);
$sql = sprintf("SELECT * from v_basic3 where stockid = '%s' and `日期` >='%s'", $stockid, $sBegDte);
$result = $odb->query($sql);
$oGrid->setHide("0");
$oGrid->setTHColspan("");
$content .= $oGrid->showGrid("", $result);
$result->free();

//--財務比率
$sql = sprintf("SELECT * from v_basic4 where stockid = '%s'", $stockid);
$result = $odb->query($sql);
$oGrid->setHide("0");
//$oGrid->setRowSpan("0");
$oGrid->setTHColspan("");
$content .= $oGrid->showGrid($headStyle . "財務比率</p>", $result);
$result->free();

$content .= "</div></body></html>";
echo $content;
?>