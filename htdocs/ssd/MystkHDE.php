<?php

require_once("HDataEdit.php");

class MystkHDE extends HDataEdit {

    /**
     * @overrride
     */
    function loadOtherData($row) {
        $sql = sprintf("select stkname from stkid where stockid ='%s'", $row["stockid"] );
        $rslt = $this->oDb->query($sql);
        $s1 = "";
        if ($row2 = $rslt->fetch_array()){
            $s1= sprintf("stockid_show\n=\n%s\n&\n",$row2["stkname"]);
        }
        $rslt->free();
        return $s1;
    }
    /**
     * @overrride
     */
    function validate($mode,$kvs,$sWhere){
        //print_r($kvs);
        if ($kvs["qty"] < 1){
            $this->msg = "數量不可小於 1";
            return false;
        }
        return true;
    }

}

?>