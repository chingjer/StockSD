<?php

require_once("HDataEdit.php");

class BTparaHDE extends HDataEdit {

    const POS_BEFVAR = 19;//start with 1
    public $sys;
    public $subsys;
    public $varColNames;
    public $numVarCol;

    function setAddDefault() {
        $s1 = "[DATA]";

        $s1 .= $this->getKeyVal("sys" , $this->sys);
        $s1 .= $this->getKeyVal("subsys" , $this->subsys);
        $s1 .= $this->getKeyVal("num" , "1");
        $s1 .= $this->getKeyVal("ADD_MAXTIMES" , "1");
        $s1 .= $this->getKeyVal("ADD_CRI" , "0.05");
        $s1 .= $this->getKeyVal("SUB_CRI" , "0.03");
        $s1 .= $this->getKeyVal("MAX_LOSE" , "0");
        $s1 .= $this->getKeyVal("NUM_CHECK" , "3");
        $s1 .= $this->getKeyVal("MAXDROP_CRI" , "99");
        $s1 .= $this->getKeyVal("BUY_MODE" , "收盤");
        $s1 .= $this->getKeyVal("STOP_MA" , "ma5");

        $s1 .= "[/DATA]";
        echo $s1;
    }
    function getKeyVal($k,$v){
        return strtolower($k) . "\n=\n" . $v . "\n&\n";
    }
    
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
    // @override
    protected function getColumnName($ix, $fld) {
        if ($ix < self::POS_BEFVAR ){
            return $fld->name;
        }else{
            return $this->varColNames[$ix - self::POS_BEFVAR ]."*";
        }
    }    

}

?>