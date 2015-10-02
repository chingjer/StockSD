<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class HDataEdit {

    public $oDb;
    public $aryKey;
    public $tbl;
    public $msg;
    public $fldInfo;

    function init($p_oDb, $tblnm, $keynms) {
        $this->oDb = $p_oDb;
        $this->tbl = $tblnm;
        $this->aryKey = explode(",", $keynms);
        $sql = sprintf("select * from %s LIMIT 1", $tblnm);
        $rslt = $p_oDb->query($sql);
        $this->fldInfo = $rslt->fetch_fields();
    }

    function getPos($label, $s1, $aPos) {
        if (!defined('_START'))
            define("_START", 0);
        if (!defined('_END'))
            define("_END", 1);
        if (!defined('_LENGTH'))
            define("_LENGTH", 2);
        // [IDSEL]'s $ending is [/IDSEL]
        $ending = sprintf("%s/%s", substr($label, 0, 1), substr($label, 1));
        $offset = $aPos[1] + 1;
        $nStart = -1;
        $nEnd = -1;
        if (!$nStart = stripos($s1, $label, $offset)) {
            return false;
        }
        if (!$nEnd = stripos($s1, $ending, $offset)) {
            return false;
        }
        $aPos[_START] = $nStart;
        $aPos[_END] = $nEnd + strlen($ending);
        $aPos[_LENGTH] = $aPos[_END] - $aPos[_START];
        //echo "start=".$aPos[_START].",end=".$aPos[_END].",length=".$aPos[_LENGTH];
        return $aPos;
    }

    /**
     * [IDSEL]id=stockid&svr=ajaxIdSelSvr.php[/IDSEL]
     * @param type $s1
     * @param type $aPos
     * @return type
     */
    function parseIdSel($s1, $aPos) {
        $len = strlen("[IDSEL]");
        $cmd = substr($s1, $aPos[0] + $len, $aPos[2] - ($len * 2 + 1));
        //$cmd = substr($s1,$aPos[0]+7,$aPos[2]-15);
        //echo $cmd."<br>";
        parse_str($cmd);
        $s1 = "";
        $s1 .= sprintf("\n<input type=text name='%s' id='%s' size=10 \n", $id, $id);
        $s1 .= sprintf("onkeyup=\"doCompletion('%s','%s_sel', '%s_show','%s')\"\n", $id, $id, $id, $svr);
        $s1 .= sprintf("onchange=\"doShowName('%s','%s_sel', '%s_show','%s')\">\n", $id, $id, $id, $svr);
        $s1 .= sprintf("<span id=\"%s_show\" style=\"displa:inline\">" .
                "直接打入代碼，或部分中文+空白叫出選單</span>", $id);
        $s1 .= sprintf("<select name=\"%s_sel\" id=\"%s_sel\" size=\"1\" style=\"display: none\" ", $id, $id);
        $s1 .= sprintf("onchange=\"doSelChange('%s','%s_sel','%s_show' )\"></select>", $id, $id, $id);
        return $s1;
    }

    /**
     * [SELECT]id=account&table=stkid&option=stkname&value=stockid[/SELECT]
     * @param type $s1
     * @param type $aPos
     * @return string
     */
    function parseSelect($s1, $aPos) {
        $len = strlen("[SELECT]");
        $cmd = substr($s1, $aPos[0] + $len, $aPos[2] - ($len * 2 + 1));
        //echo $cmd."<br>";
        parse_str($cmd);
        $sql = sprintf("select %s,%s from %s ORDER BY %s LIMIT 100", $value, $option, $table, $value);
        //echo $sql;
        $result = $this->oDb->query($sql);
        $s1 = sprintf("<select name='%s' id='%s' size=1>\n", $id, $id);
        while ($row = $result->fetch_array()) {
            $s1 .= sprintf("<option value='%s'>%s%s</option>\n", $row[$value], $row[$value], $row[$option]);
        }
        $s1 .= "</select>";
        //echo $s1;
        return $s1;
    }

    protected function getColumnName($ix, $fld) {
        return $fld->name;
    }

    /**
     * 根據 query 後的 result，產生輸入畫面
     * 
     * @param type $result
     * @param type $cols : 一行攔位數
     */
    function showAuto($result, $cols) {
        $finfo = $result->fetch_fields();
        $cnt = count($finfo);
        $s1 = sprintf("<body><form name='%s_form' id='%s_form'>\n" .
                "<TABLE cellpadding='3' cellspacing='0' border='0'>\n", $this->tbl, $this->tbl);
        $s2 = "";
        $i = 0;
        foreach ($finfo as $v) {
            if ($i % $cols == 0) {
                $s2 .= "</TR><TR>\n";
            }
            $label = $this->getColumnName($i, $v);
            $s2 .= sprintf("<TD><label for='%s'>%s</label><TD>", $v->name, $label);
            switch ($v->type) {
                case 10://date
                    $sClass = "class='datepicker'";
                    break;
                case 1://tiny
                case 2://short
                case 3://long
                case 9://int24
                    $sClass = "class='integer'";
                    break;
                case 4://float
                case 5://double
                    $sClass = "class='double'";
                    break;
                default:
                    $sClass = "";
            }//switch
            if ($v->flags & 512) {//AUTO_INCREMENT_FLAG
                $flags = "disabled";
            } else if ($v->flags & 2) {//primary key
                $flags = "readonly required";
            } else {
                $flags = "";
            }
            if ($v->type == 10){//date
                $len = 10;
            }else{
                $len = ceil($v->length / 3);
            }
            $s2 .= sprintf("<input type=text name='%s' id='%s' size=%d maxlength=%d %s %s >\n", $v->name, $v->name, $len, $len, $sClass, $flags);
            $i++;
        }//for
        $s2 = substr($s2, 5);
        $s1 .= $s2 . "</TR>\n</TABLE></form>";
        echo $s1;
        $this->showCtrl();
        $this->debugHtml($s1);

        echo "</body>";
    }

    /**
     * show HTML to debug
     * @param String $s1
     */
    function debugHtml($s1) {
        echo "<p>===== HTML DEBUG =====</p>";
        $s2 = str_replace("<", "&lt;", $s1);
        $s2 = str_replace(">", "&gt;", $s2);
        $s2 = str_replace("\n", "<BR>", $s2);
        echo $s2;
    }

    /**
     * 讀取畫面格式檔，經解譯後輸出
     * @param type $fileName
     */
    function show($fileName) {
        $s1 = file_get_contents($fileName);
        $aPos1 = array(-1, -1, -1); //[0]-start,[1]-end,[2]-length  

        if ($aPos2 = $this->getPos("[IDSEL]", $s1, $aPos1)) {
            $s2 = $this->parseIdSel($s1, $aPos2);
            $s1 = substr($s1, 0, $aPos2[0]) . $s2 . substr($s1, $aPos2[1] + 1);
        }
        if ($aPos2 = $this->getPos("[SELECT]", $s1, $aPos1)) {
            $s2 = $this->parseSelect($s1, $aPos2);
            $s1 = substr($s1, 0, $aPos2[0]) . $s2 . substr($s1, $aPos2[1] + 1);
        }
        $i = strpos($s1, "<body>");
        $i = $i < 0 ? 0 : $i + 6;
        $j = strpos($s1, "</body>");
        $j = $j < 0 ? 9999 : $j;
        echo "狀態：【<span id='status' style='background-color:yellow'>顯示</span>】<p>";
        echo substr($s1, $i, $j - $i);
        //echo str_replace("<","<BR>＜",substr($s1, $i, $j - $i));
        $this->showCtrl();
    }

    function showCtrl() {
        echo <<<CTRL
<p>
<input name="btnsave" ID="btnsave" type="button" value="儲存" style="display:none" />
<input name="btncancel" ID="btncancel" type="button" value="取消" />
<input name="btndel" ID="btndel" type="button" value="刪除" />
<span id="detail_msg" style="color:blue;background:#ffff60;"></span>
</p>        
CTRL;
    }

    /**
     * 在新增記錄時，可以override來愈設欄位初始值
     * 必須使用與loadData相同的格式，即以[DATA][/DATA]為開始結束，
     * 各欄位使用key\n=\nvalue\n&\n的格式
     */
    function setAddDefault() {
        echo "";
    }

    /**
     * 將資料庫記錄內容放入於HTML的{DATA]...[/DATA]區段中傳給前端後顯示相關欄位的內容
     * @param mixed $row: row of fetch_array()
     */
    function loadData($row) {
        $fldname = array_keys($row);
        $s = "";
        for ($i = 0; $i < count($row) / 2; $i++) {
            $k = $fldname[$i * 2 + 1];
            $v = $row[$i];
            $s .= $fldname[$i * 2 + 1] . "\n=\n" . $row[$i] . "\n&\n";
        }
        $s .= $this->loadOtherData($row);
        if (substr($s, -3) === "\n&\n") {
            $s = substr($s, 0, -3);
        }
        echo "[DATA]" . $s . "[/DATA]";
    }

    /**
     * 當有作代碼轉換時，提供額外的資訊，比如stockid在顯示已有資料時，必須將其中文顯示於
     * stockid_show這個「代碼轉換區」，此時你就要提供stockid_show=XXXX;
     * @paramater array $row: ftech_array()產生的arrary，你可以用%row["欄位名稱"]取得欄位值
     * @return String : 欄位內容字串，如"stockid_show=台積電;mthd_show=大量強勢股;"
     * 　　各欄位以";"分隔，欄位名稱必須對應到網頁的欄位ID。
     */
    function loadOtherData($row) {
        return "";
    }

    /**
     * 將字串轉為有KEY的array,如stockid=2330;stkname=台積電，轉為stockid=>2330...
     * @param String $fldVals
     * @return array with KAY
     */
    function combineKeyVal($fldVals) {

        //注意：urldecode要延遲到 genUpdateSql()
        $aa = explode("&", $fldVals);
        for ($i = 0; $i < count($aa); $i++) {
            $bb = explode("=", $aa[$i], 2);
            $keys[$i] = $bb[0];
            $vals[$i] = $bb[1];
        }
        //print_r($keys);
        //print_r($vals);
        return array_combine($keys, $vals);
    }

    /**
     * 計算要update 資料庫的 primary key
     * @param array $kvs :with key's array, 由前端使用者上傳至server
     * @param array $keys : key fieldnames
     * @return String: like "stockid ='2330' AND dte='2014-01-12'
     */
    function getKeyCondition($kvs, $keys) {
        $s1 = "";
        for ($i = 0; $i < count($keys); $i++) {
            $fldnm = $keys[$i];
            $s1 .= sprintf("AND %s='%s' ", $keys[$i], urldecode($kvs[$fldnm]));
        }
        return substr($s1, 4);
    }
    /**
     * 取得適合在前端顯示欄位資料的內容，如 "account\n=001\n"<br>
     * 表示在前端的account欄位(DOM id)賦予值為"001"的內容
     * @param string $k
     * @param string $v
     * @return string
     */
    function getKeyVal($k,$v){
        return strtolower($k) . "\n=\n" . $v . "\n&\n";
    }

    function genUpdateSql($kvs, $sWhere) {
        $db = $this->oDb;
        $sql = sprintf("select * from %s where %s", $this->tbl, $sWhere);
        //echo $sql;
        $rslt = $db->query($sql);
        $s1 = "";
        if ($row = $rslt->fetch_assoc()) {
            foreach ($kvs as $k => $v) {
                $k_decode = urldecode($k);
                $v_decode = urldecode($v);
                if ($this->isField($k_decode)) {
                    if ($row[$k_decode] != $v_decode) {
                        $s1 .= sprintf(",%s='%s'", $k_decode, $v_decode);
                    }
                }
            }
            if ($s1 != "") {
                $s1 = sprintf("update %s set %s where %s", $this->tbl, substr($s1, 1), $sWhere);
            }
        }
        return $s1;
    }

    function genInsertSql($kvs) {
        $db = $this->oDb;
        $sIns1 = "";
        $sIns2 = "";
        $s1 = "";
        foreach ($kvs as $k => $v) {
            $k_decode = urldecode($k);
            $v_decode = urldecode($v);
            if ($this->isField($k_decode)) {
                $sIns1 .= "," . $k_decode;
                if (is_null($v_decode)) {
                    $tmp = "";
                } else {
                    $tmp = $v_decode;
                }
                $sIns2 .= ",'" . $tmp . "'";
            }
        }
        if ($sIns1 != "") {
            $s1 = sprintf("insert into %s (%s) Values(%s)", $this->tbl, substr($sIns1, 1), substr($sIns2, 1));
        }
        return $s1;
    }

    public function isField($fld) {
        for ($i = 0; $i < count($this->fldInfo); $i++) {
            if (strtolower($fld) == strtolower($this->fldInfo[$i]->name)) {
                return true;
            }
        }
        //echo "not correct field!".$fld;
        return false;
    }

    /**
     * 將上傳資料，判斷哪些有修改以後，更新到資料庫
     * @param String tbl: table name
     * @param array $fldVals: 網頁上傳的資料，如stockid=2330;stkname=台積電...
     * @param array $keyNames:  key field name, 使用","分隔
     * @return String : 處理的結果
     */
    function updateRow($fldVals) {
        $this->msg = "";
        $kvs = $this->combineKeyVal($fldVals);
        $sWhere = $this->getKeyCondition($kvs, $this->aryKey);
        if (!$this->validate("U", $kvs, $sWhere))
            return;
        $sql = $this->genUpdateSql($kvs, $sWhere);
        if ($sql == "") {
            $this->msg = "沒有更新的欄位！";
        } else {
            if ($this->oDb->real_query($sql)) {
                $this->msg = "更新成功！";
            } else {
                $this->msg = "更新失敗！" . $this->oDb->error;
            }
        }
    }

    function deleteRow($fldVals) {
        $this->msg = "";
        $kvs = $this->combineKeyVal($fldVals);
        $sWhere = $this->getKeyCondition($kvs, $this->aryKey);
        $sql = sprintf("delete from %s where %s", $this->tbl, $sWhere);
        if ($this->oDb->real_query($sql)) {
            $this->msg = "刪除成功！";
        } else {
            $this->msg = "刪除失敗！" . $this->oDb->error;
        }
    }

    function insertRow($fldVals) {
        $this->msg = "";
        $kvs = $this->combineKeyVal($fldVals);
        if (!$this->validate("A", $kvs, ""))
            return;
        $sql = $this->genInsertSql($kvs);
        if ($sql == "") {
            $this->msg = "沒有更新的欄位！";
        } else {
            if ($this->oDb->real_query($sql)) {
                $this->msg = "新增成功！";
            } else {
                $this->msg = "新增失敗！" . $this->oDb->error;
            }
        }
    }

    /**
     * 資料後端驗證
     * @param String $mode: 'A'--Insert,'U'--Update
     * @param array $kvs -- Data values arrary with Key
     * @param String $sWhere -- Primary key, like stockid='2330' AND dte='xxx'
     * @return boolean -- true if ok! false is not ok! 同時設定 $this->msg
     */
    function validate($mode, $kvs, $sWhere) {
        //$this->msg="";
        return true;
    }

}
