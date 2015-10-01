<?php

require_once("HGrid.php");

class ParaNameGrid extends HGrid {

    protected function getColumn($ix, $row) {
        if ($ix > 1) {
            $aa = explode("=", $row[$ix]);
            $s1 = $aa[0];
        } else {
            $s1 = $row[$ix];
        }
        return $s1;
    }

    protected function getMoreColumn($ix, $row) {
        if ($ix == 0) {
            return "<span class='link' onclick='OpenPara(this)'>參數設定</span>";
        } else {
            return "";
        }
    }

    function showScript() {
        echo <<<MY_SCRIPT
<style type="text/css">
</style>
<script>
function OpenPara(obj){
    sys = $(obj).closest("tr").find("td").eq(0).text();
    subsys = $(obj).closest("tr").find("td").eq(1).text();
    var sUrl = 'BTPara.php?sys='+ sys + '&subsys=' + subsys + '';
    //alert (sUrl);
    window.open(encodeURI(sUrl),"_blank");
    cancelBubble(event);
}
</script>            
MY_SCRIPT;
    }

}

?>