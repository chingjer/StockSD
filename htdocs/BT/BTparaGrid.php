<?php

require_once("HGrid.php");
require_once("IconImg.php");

/**
 * 系統參數管理使用的HGrid 
 */
class BTparaGrid extends HGrid {

    const VARCOL_BEG = 13;//start with 1
    public $sys;
    public $subsys;
    public $varColNames;
    public $numVarCol;
    
    function getVarColNames() {
        $this->varColNames = array();
        $sql = sprintf("SELECT * from bt_paraname where sys = '%s' and subsys='%s'", 
                $this->sys, $this->subsys);
        //echo $sql;
        $result = $this->oDb->query($sql);
        if ($row = $result->fetch_array()) {
            $this->numVarCol = 0;
            for ($i = 2; $i < $result->field_count; $i++) {
                if ($row[$i] !== "") {
                    $aa = explode("=",$row[$i]);
                    $this->varColNames[$i - 2] = $aa[0];
                    $this->numVarCol++;
                }else{
                    $this->varColNames[$i - 2] = "";
                }
            }
        }
    }
    function getHideColPos($result){
        $s="";
        $beg = self::VARCOL_BEG + $this->numVarCol;
        $cnt = $result->field_count;
        //echo "beg=".$beg.",cnt=".$cnt.",numVarCol=".$this->numVarCol;
        for ($i=$beg;$i<$cnt;$i++){
            $s .= $i . ","; // start with 1
        }
        return substr($s,0,-1);//ommited last char
    }
    // @override
    protected function getColumnName($ix, $fld) {
        if ($ix < self::VARCOL_BEG ){
            return $fld->name;
        }else{
            return $this->varColNames[$ix - self::VARCOL_BEG ];
        }
    }
    protected function getMoreColumn($ix, $row) {
        if ($ix == 0) {
            //return "<span class='link' onclick='copyPara(this)' title='複製一筆到編號99'>C</span>";
            return "<img onclick='copyPara(this)' title='複製一筆到編號99' height=20 width=20 src='" . IconImg::COPY . "'>" .
            "<img onclick='delPara(this)' title='刪除本筆參數' height=20 width=20 src='". IconImg::DEL . "'>" .
            "<img onclick='togglePara(this)' title='enable/disable' height=20 width=20 src='". IconImg::TOGGLE . "'>"     ;
        } else {
            return "";
        }
    }
    function showBeforeGrid() {
        echo "<p style='color: #008000;font-weight: bold;'>SYS:$this->sys, SUBSYS:$this->subsys</p>";        
    }    
    function showScript() {
        echo <<<MY_SCRIPT
<style type="text/css">
</style>
<script>
function copyPara(obj){
    id = $(obj).closest("tr").find("td").eq(0).text();
    var sUrl = 'BTPara.php';
    keyval = id;
    //alert (keyval);
    simplePost("PARA_COPY",keyval,sUrl,true);
    g_isChange = "Y";
    GoToPage();
    g_isChange = "N";
    cancelBubble(event);
}
function delPara(obj){
    id = $(obj).closest("tr").find("td").eq(0).text();
    var sUrl = 'BTPara.php';
    keyval = id;
    simplePost("PARA_DEL",keyval,sUrl,true);
    g_isChange = "Y";
    GoToPage();
    g_isChange = "N";
    cancelBubble(event);
}
function togglePara(obj){
    id = $(obj).closest("tr").find("td").eq(0).text();
    var sUrl = 'BTPara.php';
    keyval = id;
    simplePost("PARA_TOGGLE",keyval,sUrl,true);
    g_isChange = "Y";
    GoToPage();
    g_isChange = "N";
    cancelBubble(event);
}        
</script>            
MY_SCRIPT;
    }

}

//class
?>