<?php

require_once("HGrid.php");

class BTgrid extends HGrid {

    public $sys; //GET SYS=xxxxx傳入交易系統名稱，如'bbbro' 或 'lvstg'

    protected function getMoreColumn($ix, $row) {
        if ($ix == 0) {
            return "<span class='link' onclick='OpenPara(this)'>參數設定</span>";
        } else {
            return "";
        }
    }
    /**
     * 顯示測試績效統計
     * @return type
     */

    function showStat() {
        $oGrid = new SimpleGrid();
        $oGrid->init("", "", "", "");
        $sql = sprintf("select * from v_bt_stat where sys='%s' order by datecode, `參數編號`", $this->sys);
        $result = $this->oDb->query($sql);
        $s = $oGrid->showGrid("", $result);
        $result->free();
        return $s;
    }

    function showTabs($ix) {
        if ($ix == 0) {
            return "";
        } else {
            return "";
        }
    }

    function showBeforeGrid() {
        $filt = explode(",", $this->getFilter());
        $subsys = $filt[0];
        //-----產生[subsys]下拉式選單
        $sql = sprintf("select distinct subsys from bt_stat " .
                "where sys='%s' order by subsys", $this->sys);
        $rs = $this->oDb->query($sql);
        $optSubsys = "<option value='*'>ALL</option>";
        while ($row = $rs->fetch_array()) {
            $sel = $row["subsys"] == $subsys ? " selected " : "";
            $optSubsys .= sprintf("<option value='%s' %s>%s</option>", $row["subsys"], $sel, $row["subsys"]);
        }
        //-----產生[日期代號]下拉式選單
        $optDate = "<option value='*'>ALL</option>";
        if ($subsys != "*") {
            $sql = sprintf("select distinct b.datecode, t.* from bt_stat b " .
                    "inner join bt_period t on b.datecode = t.datecode " .
                    "where sys='%s' and subsys='%s' order by b.datecode", $this->sys, $subsys);
            $rs = $this->oDb->query($sql);
            while ($row = $rs->fetch_array()) {
                $sel = $row["datecode"] == $filt[1] ? " selected " : "";
                $optDate .= sprintf("<option value='%s' %s>%s</option>", $row["datecode"], $sel, $row["datecode"] . $row["rmk"]);
            }
        }
        //-----產生[參數編號]下拉式選單
        $optNum = "<option value='*'>ALL</option>";
        if ($subsys != "*") {
            $sql = sprintf("select distinct num from bt_stat " .
                    "where sys='%s' and subsys='%s' order by num ", $this->sys, $subsys);
            $rs = $this->oDb->query($sql);
            while ($row = $rs->fetch_array()) {
                $sel = $row["num"] == $filt[2] ? " selected " : "";
                $optNum .= sprintf("<option value='%s' %s>%s</option>", $row["num"], $sel, $row["num"]);
            }
        }
        echo <<<BEFORE_GRID
<div id=filter>
    <font color=brown>子系統：</font>
    <select size=1 id="subsys_sel" name="subsys_sel">$optSubsys</select>
    &nbsp;<font color=brown>期間：</font>
    <select size=1 id="date_sel" name="date_sel">$optDate</select>
    &nbsp;<font color=brown>參數編號：</font>
    <select size=1 id="num_sel" name="num_sel">$optNum</select>
    &nbsp;&nbsp;<button onclick="doFilter()">重新篩選</button> 
    <p>
</div>            
BEFORE_GRID;
    }

    function showScript() {
        echo <<<MY_SCRIPT
<style type="text/css">
</style>
<script>
$(document).ready(function () {

    $("#tabs").tabs({
        activate: function (event, ui) {
            var tabId = ui.newPanel.attr('id');
            if (tabId == "交易明細"){
                var tds  = $("#hgrid tr:eq(1)").find("td");//tr:eq(0)是標題行
                var sys = tds.eq(0).text();
                var subsys = tds.eq(1).text();        
                var sUrl = 'BTShowDet2.php?SYS='+ sys + '&SUBSYS=' + subsys + '';
                //alert(sUrl);
                ajaxPost("GRID", "1", "10", sUrl, "",tabId,null);        
            }
        }//Activate
    });
 
    $("#hgrid").delegate("#subsys_sel", "change", function () {
        keyVal = "$this->sys" + "," + document.getElementById("subsys_sel").value;
        doSetOption("date_sel", keyVal, "ajaxSvr1.php");
    });
    $("#hgrid").delegate("#date_sel", "change", function () {
        keyVal = "$this->sys" + "," + document.getElementById("subsys_sel").value
            + "," + document.getElementById("date_sel").value;
        //alert(keyVal);
        doSetOption("num_sel", keyVal, "ajaxSvr1.php");
    });

        
        
})//jQuery
        
</script>            
MY_SCRIPT;
    }

//showScript()       
}

//class
?>