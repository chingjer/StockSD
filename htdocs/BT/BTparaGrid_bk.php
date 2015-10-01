<?php

require_once("HGrid.php");

/**
 * 系統參數管理使用的HGrid 
 */
class BTparaGrid extends HGrid {

    const VARCOL_BEG = 12;//start with 1
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

}

//class
?>