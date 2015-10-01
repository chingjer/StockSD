<?php

    require_once("OpenWinGrid.php");
    require_once("IconImg.php");
    class MyStkGrid extends OpenWinGrid
    {
        function calcDays($row){
            $cnt = "";
            if ($row["出場價"] == 0){
                $stockid = $row["代號"];
                $buydate = $row["進場日"];
                $sql = sprintf("SELECT count(*) from stk where stockid =".
                        "'%s' and dte between '%s' AND '%s'",
                        $stockid,$buydate,$this->oStk->sCurrDate);
                $result2 = $this->oStk->oDb->query($sql);
                if ($row2 = $result2->fetch_array()) {
                    //$cnt = $row2[0] -1;
                    $cnt = $row2[0];//計算至盤隔日
                    //$dte1 = new DateTime($this->oStk->sCurrDate);
                    //$dte2 = new DateTime();
                    //$cnt += $dte1->diff($dte2)->format("%R%a");
                }
                $result2->free();
            }
            return $cnt;
        }
        protected function getMoreColumn($ix, $row) {
            if ($ix==0){//風險
                $earnRate = "";
                if ($row["出場價格"] == 0){
                    $earnRate = $this->oStk->calcProfit($row, "資券", "價格", "停損價格","出場價格");
                    if ($earnRate < 0) {
                        $earnRate = "<font color=red>".$earnRate."%</font>";
                    }else {
                        $earnRate .= "%";
                    }
                }
                return $earnRate;
            }else if ($ix==1){//盈虧
                $earnRate = $this->oStk->calcProfit($row, "資券", "價格", "收盤","出場價格");
                if ($earnRate < 0) {
                    $earnRate = "<font color=red>".$earnRate."%</font>";
                }else {
                    $earnRate .= "%";
                }
                return $earnRate;
            }else if ($ix==2){//天數
                return $this->calcDays($row);
            }else if ($ix==3){//more
                //return "<span class='link' onclick='doMore(this)'>MORE..</span>";
               return "<img onclick='doMore(this)' title='更多詳細資訊' height=20 width=20 src='" . IconImg::MORE . "'>";
            }else{
                return "";
            }
        }
        function showTabs($ix){
            if ($ix==2) {
                $s1 = MyStk::file_get_contents_utf8("「交易管理、編輯」操作說明.html");
                return $s1;
            }
            else {
                return "";
            }

        }
        function showScript() {
echo <<<MY_SCRIPT
<style type="text/css">
</style>
<script>
function doMore(obj){
    keyval = $(obj).closest("tr").find("td").eq(3).text();
    var sUrl = "showMore.php";
    $("#tabs").tabs("option", "active", 3);
    simplePost("明細",keyval,sUrl);
    cancelBubble(event);
}
            
$(document).ready(function () {

    $("#明細").delegate("#sm_btn", "click", function () {            
        var sUrl = "showmore.php";
        var keyval = $("#明細 #sm_stockid").val();
        simplePost("明細",keyval,sUrl);
    });
            
    $("#損益表").delegate("#FileProfit", "click", function () {            
        var sUrl = "showProfit.php";
        simplePost("損益表","FILE",sUrl);
    });

    $("#detail").delegate("input:radio[name='catgry']", "click", function () {
        keyval= $(this).val();
        //alert(keyval);
        doSetOption("mthd", keyval, "ajaxIdSelSvr.php")
    });
           
    $("#tabs").tabs({
        activate: function (event, ui) {
            var tabId = ui.newPanel.attr('id');
            var sUrl = "showProfit.php";
            if (tabId == "損益表"){
                //alert(tabId);
                simplePost(tabId,"",sUrl);
            }
        }//Activate
    });
})
        
/**
* post前資料驗證
* unction name必須為 validate()
* @return boolean: false不會post
*/
function validate(){
    obj = $("#detail #qty");
    if (obj.val() <1) {
        alert("數量必須大於 0!");
        cancelBubble(event);
        obj.focus();
        return false;
    }
    return true;
}
</script>            
MY_SCRIPT;
        }

        function showBeforeGrid() {
            
            $filt= explode(",", $this->getFilter());
            $chk1=""; $chk2="";
            if ($filt[0]=="1") $chk1="checked";
            if ($filt[0]=="2") $chk2="checked";
echo <<<BEFORE_GRID
<div id=filter>
    <input  type="radio" name="scope" value="1" $chk1>庫存
    <input  type="radio" name="scope" value="2" $chk2>歷史
    &nbsp;&nbsp;<button onclick="doFilter()">重新篩選</button> 
    <p>                    
</div>            
BEFORE_GRID;
        }
    }//class

?>