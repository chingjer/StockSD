<?php
    require_once("HGrid.php");

    class OpenWinGrid extends HGrid {

        protected function getColumn($ix, $row) {
            if ($ix == 0) {
                // 鉅亨 HTML5 技術線圖
                $s1 = sprintf("<span class=link style=\"background-color: #ff9933;cursor:pointer\" "
                        . "onclick=openWin(\"http://www.cnyes.com/twstock/html5chart/"
                        . "%s.htm\",\"技術線圖\",event)>%s</span>", $row["代號"], $row[$ix]);
            } else if ($ix == 1) {
                // 奇摩股市/基本資料/營收獲利
                $s1 = sprintf("<span style=\"background-color: #99ffcc;cursor:pointer\" "
                        . "onclick=openWin(\"https://tw.stock.yahoo.com/d/s/earning_"
                        . "%s.html\",\"營收獲利\",event)>%s</span>", $row["代號"], $row[$ix]);
            } else if ($ix == 2) {
                // 鉅亨 三大法人近月買賣超
                $s1 = sprintf("<span style=\"background-color:#ffff66;cursor:pointer\" "
                        . "onclick=openWin(\"http://www.cnyes.com/twstock/Institutional/"
                        . "%s.html\",\"三大法人\",event)>%s</span>", $row["代號"], $row[$ix]);
            } else {
                $s1 = $row[$ix];
            }
            return $s1;
        }

    }
?>