<?php

require_once("dbConfig.php");
/**
 * doSetOption() Use
 */
if (isset($_POST["id_sel"]) and isset($_POST["key_value"])){
    $db = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
    $db->set_charset("utf8");
    if ($_POST["id_sel"]=="mthd") {
        $where = "mthd like 'B%' or mthd like 'M%'";
        if ($_POST["key_value"] != "B"){//不是融券
            $where = "not (" . $where .")";
        }
        $sql = "select * from mthd where " . $where . " order by mthd";
        $rs = $db->query($sql);
        $s1 = "";
        while ($row = $rs->fetch_array()) {
            $s1 .= "\n" . $row['mthd']. "\t" . $row['mthd'] . 
                    $row['spec'] . "(" .$row['HOLDDAYS'].")";
        }
        echo substr($s1,1);
    }else{
        die("Wrong id_sel value!");
    }    
}
/**
 * ajaxIdSelector Use
 */
if (isset($_POST["codename"])) {
    $code_name=  strtolower($_POST["codename"]);
    
    if (!(strpos($code_name,"stockid")===false) ) {
        if (isset($_POST["pname"])) {//以部分名稱查詢
            $sname = trim($_POST["pname"]);
            $db = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
            $db->set_charset("utf8");
            $rs = $db->query("SELECT * from stkid where stkname like '%" . $sname . "%'");
            while ($row = $rs->fetch_array()) {
                echo $row['stockid'], "\t" . $row['stkname'] . "\n";
            }
        } else if (isset($_POST["pid"])) {//代碼轉換
            $sid = trim($_POST["pid"]);
            $db = new mysqli($_DB['host'], $_DB['username'], $_DB['password'], $_DB['dbname']);
            $db->set_charset("utf8");
            $rs = $db->query("SELECT stkname from stkid where stockid= '" . $sid . "'");
            if ($row = $rs->fetch_array()) {
                echo $row['stkname'];
            } else {
                echo "無此代碼";
            }
        } else {
            echo "no post!";
        }
    }
}