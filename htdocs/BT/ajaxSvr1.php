<?php

require_once("dbConfig.php");
if (!(is_null($_POST["id_sel"]) or is_null($_POST["key_value"]))){
    $db = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], "backtesting");
    $db->set_charset("utf8");
    if ($_POST["id_sel"]=="date_sel") {
        $aa = explode(",",$_POST["key_value"]);
        $sql = sprintf("select distinct b.datecode, t.* from bt_stat b " .
                "inner join bt_period t on b.datecode = t.datecode " .
                "where sys='%s' and subsys='%s' order by b.datecode", $aa[0], $aa[1]);
        $rs = $db->query($sql);
        $s1 = "*\tALL";
        while ($row = $rs->fetch_array()) {
            $s1 .= "\n" . $row['datecode']. "\t" . $row['datecode'] . $row['rmk'];
        }
        echo $s1;
    }else if ($_POST["id_sel"]=="num_sel") {
        $aa = explode(",",$_POST["key_value"]);
        $sql = sprintf("select distinct num from bt_stat " .
                "where sys='%s' and subsys='%s' and datecode='%s' order by num ", 
                $aa[0], $aa[1], $aa[2]);
        $rs = $db->query($sql);
        $s1 = "*\tALL";
        while ($row = $rs->fetch_array()) {
            $s1 .= "\n" . $row['num']. "\t" . $row['num'];
        }
        echo $s1;
    }else{
        die("Wrong id_sel value!");
    }    
}else {
        echo "no suitable post!";
    }
?>