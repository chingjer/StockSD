<?php

    require_once("SimplePage.php");
    class BTShowDetGrid2 extends SimplePage
    {
        public $sys; //GET SYS=xxxxx傳入交易系統名稱，如'bbbro' 或 'lvstg'
        function showBeforeGrid() {
            $div = $this->getDiv();
            $filt= explode(",", $this->getFilter());
            $chk1=""; $chk2=""; 
            if ($filt[0]=="1") $chk1="checked";
            if ($filt[0]=="2") $chk2="checked";
            $sql = sprintf("select distinct b.datecode, t.* from %s b ".
                    "inner join bt_period t on b.datecode = t.datecode",$this->sys);
            $rs = $this->oDb->query($sql);
            $opt = "<option value='*'>ALL</option>";
            while ($row = $rs->fetch_array()) {
                $sel = $row["datecode"]==$filt[1]?" selected ":"";
                $opt .= sprintf("<option value='%s' %s>%s</option>",
                        $row["datecode"],$sel,$row["datecode"].$row["rmk"]);
            }
echo <<<BEFORE_GRID
<div id=filter$div>
    <span style='font-size:large;color:blue;font-weight:bold;'>【 $this->sys 】</span>
    <font color=brown>內容：</font>
    <input  type="radio" name="scope" value="1" $chk1>僅交易
    <input  type="radio" name="scope" value="2" $chk2>全部 &nbsp;
    <font color=brown>期間：</font>
    <select size=1 id="datesel" name="datesel">$opt</select>
    &nbsp;&nbsp;<button onclick="doFilter('$div',this)">重新篩選</button> 
    <p>
</div>            
BEFORE_GRID;
        }
    }//class

?>