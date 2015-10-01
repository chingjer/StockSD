

function doSelChange(id, idSel, idShow) {
    var i;
    var objSel = document.getElementById(idSel);
    var objId = document.getElementById(id);
    var objIdShow = document.getElementById(idShow);
    i = objSel.value.indexOf(" ");
    objId.value = objSel.value.substring(0, i);
    objSel.style.display = "none";
    objIdShow.style.display = "inline";
    objIdShow.innerHTML = objSel.value.substring(i + 1);
}
/**
 * 根據某幾個欄位的值，使用ajax post至伺服器取得相關內容後更新前端某個<select>欄位的內容<BR>
 * ajax post後傳回data格式為：option'sValue\toption'sText\n，
 * 以\n分隔每一個option,同一option則以\t分隔value與text，
 * 如：0050\t0050台灣五十\n0055\t0055寶金融...<br>
 * 請注意：如果第一個option是all，你要再伺服端就產生，如"*\t全部\n"
 * @param {String} idSel: 要更改的<select> ID
 * @param {String} keyVal: 可以為多重欄位的組合，以','來分隔，比如"candle,@槌子"(sys,subsys)
 * @param {String} url: 對應的伺服器程式，如ajaxSvr1.php
 * @returns (void)
 */
function doSetOption(idSel, keyVal, url) {
    //alert(keyVal);
    var xajax = $.ajax({
        type: "POST",
        url: url,
        data: {
            id_sel: idSel,
            key_value: keyVal,
        },
        catch : false});//true也可以
    xajax.done(
            function (data, status) {
                var ajList, ajSel, ajId, ajOpt, ajIdShow, i;
                ajList = data.split("\n");
                ajSel = document.getElementById(idSel);
                while (ajSel.length > 0) {
                    ajSel.remove(ajSel.length - 1);
                }
                if (ajList.length == 0)
                    return;
                for (i = 0; i < ajList.length; i++) {
                    ajOpt = document.createElement("option");
                    aa = ajList[i].split("\t");
                    ajOpt.value = aa[0];
                    ajOpt.text = aa[1];
                    ajSel.add(ajOpt);
                }
                ajSel.selectedIndex = 0;
            });
}
function doCompletion(id, idSel, idshow, url) {
    var objId, i, objIdVal;
    objId = document.getElementById(id);
    objIdVal = objId.value;
    if (objIdVal.length == 0)
        return;
    if (objIdVal.substring(objIdVal.length - 1) !== " ")
        return;
    var xajax = $.ajax({//GET效果一樣
        type: "POST",
        url: url,
        data: {
            codename: id,
            pname: objIdVal
        },
        catch : false});//true也可以
    xajax.done(
            function (data, status) {
                var ajList, ajSel, ajId, ajOpt, ajIdShow, i;
                ajList = data.split("\n");
                ajSel = document.getElementById(idSel);
                ajId = document.getElementById(id);
                ajIdShow = document.getElementById(idshow);
                while (ajSel.length > 0) {
                    ajSel.remove(ajSel.length - 1);
                }
                if (ajList.length == 0)
                    return;
                for (i = 0; i < ajList.length; i++) {
                    ajOpt = document.createElement("option");
                    ajOpt.text = ajList[i];
                    ajSel.add(ajOpt);
                }
                ajSel.selectedIndex = 0;
                i = ajList[0].indexOf("\t");
                ajId.value = ajList[0].substring(0, i);
                ajIdShow.innerHTML = ajList[0].substring(i + 1);
                ajIdShow.style.display = "none";
                ajSel.style.display = "inline";
            });
}
function doShowName(id, idSel, idShow, url) {
    var objId, i, objIdVal;
    objId = document.getElementById(id);
    objIdVal = objId.value;
    if (objIdVal.length == 0)
        return;
    var xajax = $.ajax({//GET效果一樣
        type: "POST",
        url: url,
        data: {
            codename:id, 
            pid: objIdVal
        },
        catch : false});//true也可以
    xajax.done(
            function (data, status) {
                var ajSel, ajIdShow;
                ajSel = document.getElementById(idSel);
                ajIdShow = document.getElementById(idShow);
                ajIdShow.innerHTML = data;
                ajIdShow.style.display = "inline";
                ajSel.style.display = "none";
            });
}
$(document).ready(function () {
    $("[data-role='ajaxIdSelector'] input").keyup(function () {
        var xId, xSel, xShow, xUrl;
        xId = $(this).parent().find("input").eq(0).attr("id");
        xSel = $(this).parent().find("select").attr("id");
        xShow = $(this).parent().find("span").attr("id");
        xUrl = $(this).parent().find("input").eq(1).val();
        //alert ("keyup:"+xId+","+xSel+","+xShow+",url="+xUrl);
        doCompletion(xId, xSel, xShow, xUrl);
    });
    $("[data-role='ajaxIdSelector'] input").change(function () {
        var xId, xSel, xShow, xUrl;
        xId = $(this).parent().find("input").eq(0).attr("id");
        xSel = $(this).parent().find("select").attr("id");
        xShow = $(this).parent().find("span").attr("id");
        xUrl = $(this).parent().find("input").eq(1).val();
        //alert ("keyup:"+xId+","+xSel+","+xShow+",url="+xUrl);
        doShowName(xId, xSel, xShow, xUrl);
    });
    $("[data-role='ajaxIdSelector'] select").change(function () {
        var xId, xSel, xShow, xUrl;
        xId = $(this).parent().find("input").eq(0).attr("id");
        xSel = $(this).parent().find("select").attr("id");
        xShow = $(this).parent().find("span").attr("id");
        xUrl = $(this).parent().find("input").eq(1).val();
        //alert ("keyup:"+xId+","+xSel+","+xShow+",url="+xUrl);
        doSelChange(xId, xSel, xShow);
    });
});