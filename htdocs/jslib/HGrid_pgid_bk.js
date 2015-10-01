/* 
 * 配合 HGrid Class 運作的 js
 * 使用 ajax post 代替 Form post 以提高效率
 * author: t.m. Huang
 */
var g_preObj; // used by selectArrow
var g_action; // "A"-in Add g_action
var g_postFilter = ""; //filter String like "23,1"使用','分隔
var g_isChange = "N"; //是否有資料變更
$(document).ready(function () {
    var sUrl;

    //$("#tabs").tabs();
    $("#tabs").tabs({
        beforeActivate: function (event, ui) {
            if (g_isChange == "Y") {
                //alert("g_isChange = Y, reload it!");
                var tabId = ui.newPanel.attr('id');
                if (tabId == "hgrid") {
                    GoToPage();
                    g_isChange = "N";
                }
            }
        }//Activate
    });

    $("#hgrid").delegate("tr", "click", function () {
        if ($("#detail").length > 0) {
            var okeys = $(this).children("td.KEYS");
            var xx = "";
            sUrl = $("#hgrid #URL").val();
            for (i = 0; i < okeys.length; i++)
                xx += "," + okeys.eq(i).text();
            if (xx != "") {
                $("#tabs").tabs("option", "active", 1);
                g_action = "U";
                ajaxPost("DETAIL", "", "", sUrl, xx.substr(1),pgid);
            }
        }
    });
    $("#hgrid").delegate("#btnadd", "click", function () {
        g_action = "A";
        $("#tabs").tabs("option", "active", 1);
        sUrl = $("#hgrid #URL").val();
        ajaxPost("ADD", "", "", sUrl, "",pgid);
    });

    $("#detail").delegate(".datepicker", "focus", function () {
        this.select();
        $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
    });
    $("#detail").delegate(":input", "change", function () {
        $("#btndel").css({"display": "none"});
        $("#btnsave").css({"display": "inline"});
        $("#btncancel").css({"display": "inline"});
        $("#detail_msg").html("");
        if (g_action == "A")
            $("#status").text("新增");
        else
            $("#status").text("修改");

    });
    $("#detail").delegate("#btnsave", "click", function () {
        //alert(g_action);
        ret = true;
        //--- check required fields
        objs = $("#detail").find('[required]');
        for (i = 0; i < objs.length; i++) {
            if ($(objs[i]).val() == "") {
                alert(getLabel(objs[i]) + ": 本欄需要輸入資料!");
                $(objs[i]).focus();
                ret = false;
                break;
            }
        }
        if (ret) {
            objs = $("#detail :input[type!='button'][type !='radio'][type !='checkbox']");
            for (i = 0; i < objs.length; i++) {
                $o = $(objs[i]);
                x = $o.val();
                if ($o.hasClass("double") || $o.hasClass("integer")) {
                    if (isNaN(x)) {
                        alert(getLabel($o) + ": 必須為數字");
                        ret = false;
                        break;
                    }
                    if ($o.hasClass("integer")) {
                        if (!(x % 1 == 0)) {
                            alert(getLabel($o) + ": 必須為整數");
                            ret = false;
                            break;
                        }
                    }
                }
            }
            if (!ret) {
                $o.focus();
            }
        }

        if (ret) {
            // --- check 自訂 validate
            try {
                ret = validate();
            } catch (e) {
                if (e.name == "ReferenceError") {
                    //alert("no validate()");
                    ret = true;
                } else {
                    ret = false;
                    alert(e);
                }
            }
        }
        if (ret) {
            s1 = getSaveData();
            sUrl = $("#hgrid #URL").val();
            ajaxPost("SAVE", g_action, "", sUrl, s1,pgid);
        }
    });
    $("#detail").delegate("#btncancel", "click", function () {
        $("#detail").html("");
        $("#tabs").tabs("option", "active", 0);
    });
    $("#detail").delegate("#btndel", "click", function () {
        s1 = getSaveData();
        //alert("HGrid.js line 52,"+s1);
        if (confirm("確定要刪除嗎？")) {
            sUrl = $("#hgrid #URL").val();
            ajaxPost("DEL", "", "", sUrl, s1,pgid);
        } else {
            alert("你已經取消刪除！");
        }
    });
})
function getLabel(pObj) {
    label = $(pObj).attr('id');
    $oLabel = $("label[for='" + label + "']");
    if ($oLabel.length > 0)
        label = $oLabel.text();
    return label;
}
function cancelBubble(e) {
    // e is event
    if (!e)
        e = window.event;
    //IE9 & Other Browsers
    if (e.stopPropagation) {
        e.stopPropagation();
    }
    //IE8 and Lower
    else {
        e.cancelBubble = true;
    }
}
function openWin(link, winName, e) {
    window.open(link, winName);
    cancelBubble(e);
    return false;
}

function selectArow(sObject) {
    $(sObject).attr("style", "BACKGROUND-COLOR: #FFFF99");
    if (g_preObj != null && g_preObj != sObject)
        $(g_preObj).attr("style", "BACKGROUND-COLOR: white");
    g_preObj = sObject;
}
function simplePost(Id, Val, sUrl) {
    var xajax = $.ajax({
        type: "POST",
        url: sUrl,
        data: {
            AJ_KEY: Id,
            AJ_VAL: Val
        },
        catch : false}); //true也可以
    xajax.done(
            function (data, status) {
                //document.getElementById(tabId).innerHTML = data;
                $('#' + Id).html(data);
            }
    )//done           
}
function ajaxPost(sMode, sPage, sPage_rows, sUrl, sInfo,pgid) {
    //alert("xxxfilter="+g_postFilter);
    if (pgid===null) pgid = ""; 
    var xajax = $.ajax({
        type: "POST",
        url: sUrl,
        data: {
            MODE: sMode,
            PAGE: sPage,
            PAGE_ROWS: sPage_rows,
            INFO: sInfo,
            FILTER: g_postFilter
        },
        catch : false}); //true也可以
    xajax.done(
            function (data, status) {
                if (sMode == "GRID" || sMode == "FILTER") {
                    document.getElementById("hgrid"+pgid).innerHTML = data;
                } else if (sMode == "DETAIL") {
                    i = data.indexOf("[DATA]");
                    j = data.indexOf("[/DATA]");
                    document.getElementById("detail"+pgid).innerHTML = data.substr(0, i - 1);
                    dataVals = data.substr(i + 6, j - i - 6);
                    //alert(dataVals);
                    loadData(dataVals);
                } else if (sMode == "ADD") {
                    
                    i = data.indexOf("[DATA]");
                    if (i==-1){
                        document.getElementById("detail"+pgid).innerHTML = data;
                    }else{//有預設資料
                        j = data.indexOf("[/DATA]");
                        document.getElementById("detail"+pgid).innerHTML = data.substr(0, i - 1);
                        dataVals = data.substr(i + 6, j - i - 6);
                        //alert(dataVals);
                        loadData(dataVals);
                    }
                    $("#btndel"+pgid).css({"display": "none"});
                    $("#status"+pgid).text("新增");

                    var x = $("#detail"+pgid+" :input[type=text][readonly='readonly']");
                    x.css({"background": "yellow"});
                    x.removeAttr("readonly");
                    x.focus();
                    //$("#detail").find("input:disabled").css({"background": "yellow"});

                } else if (sMode == "SAVE") {
                    if (data.indexOf("成功") > -1) {
                        g_isChange = "Y";
                        if (g_action=="A"){
                            x = "您可繼續新增資料";
                        }else{
                            x = "您可繼續修改資料"
                        }
                        x += "(欄位變動才會顯示[儲存]按鈕)";
                    } else {
                        x = "";
                    }
                    document.getElementById("detail_msg"+pgid).innerHTML = data + x;
                    //if (data.indexOf("成功") > -1) {
                    //    g_action = "U";
                    //    $("#btndel").css({"display": "inline"});
                    //    $("#btnsave").css({"display": "none"});
                    //    $("#btncancel").css({"display": "none"});
                    //} else {
                    $("#btndel"+pgid).css({"display": "none"});
                    $("#btnsave"+pgid).css({"display": "none"});
                    $("#btncancel"+pgid).css({"display": "none"});
                    //}
                } else if (sMode == "DEL") {
                    document.getElementById("detail_msg"+pgid).innerHTML = data;
                    if (data.indexOf("成功") > -1) {
                        g_action = "A";
                        g_isChange = "Y";
                        $("#status"+pgid).text("新增");
                        $("#btndel"+pgid).css({"display": "none"});
                        $("#btnsave"+pgid).css({"display": "inline"});
                        $("#btncancel"+pgid).css({"display": "none"});
                    } else {
                        $("#status"+pgid).text("顯示");
                        $("#btndel"+pgid).css({"display": "none"});
                        $("#btnsave"+pgid).css({"display": "none"});
                        $("#btncancel"+pgid).css({"display": "inline"});
                    }
                }
            }
    )
}
function loadData(dv,pgid) {
    //alert(dv);
    if (pgid===null) pgid="";
    var aa = dv.split("\n&\n");
    var obj, obj2;
    for (i = 0; i < aa.length; i++) {
        bb = aa[i].split("\n=\n");
        obj = $("#detail"+pgid+" :input[name='" + bb[0] + "']");
        //if (bb[0]=="stockid_show")
        //    alert(obj.length==0?"NULL":"not null");
        if (obj.length == 0) {
            obj2 = $("#" + bb[0] + "");
            //alert(obj2.length==0?"obj2 NULL":"obj2 not null");
            obj2.html([bb[1]]);
        } else {
            obj.val([bb[1]]);
        }
    }
    /*
     aa = dv.split(";");
     for (i = 0; i < aa.length; i++) {
     bb = aa[i].split("=");
     $("#detail input[name='" + bb[0] + "']").val([bb[1]]);
     }
     */
}
/**
 * 取得輸入的資料準備上傳
 * Not serialize()方式
 * @returns {String}
 */
function getSaveData_NS() {
    var objs;
    var s1 = "";
    objs = $("#detail :input[type!='button'][type !='radio'][type !='checkbox']");
    s1 += getFldVal(objs);
    objs = $("#detail :checked");//radio or checkbox is checked
    s1 += getFldVal(objs);
    alert(s1);
    return (s1.substr(1));
}
/**
 * 取得輸入的資料準備上傳
 * 使用 .serialize()的方式，注意 disabled欄位以及中文資料在PHP要urldecode()
 * @returns {String}
 */
function getSaveData() {
    var $frm = $('form');
    var s1 = "";
    var fldDisabled = null;
    if (g_action != "A") {
        fldDisabled = $frm.find('input:disabled').removeAttr('disabled');
    }
    s1 = $frm.serialize();
    //alert(s1);
    if (!(fldDisabled === null))
        fldDisabled.attr('disabled', 'disabled');
    //alert(s1);
    return s1;
}
function doFilter(pgid) {
    if (pgid==null) pgid="";
    var objs, xx, page_rows, sUrl, i;
    xx = "";
    objs = $("#filter"+pgid+" input[type!=button][type !='radio'][type !='checkbox']");
    for (i = 0; i < objs.length; i++)
        xx += "," + objs.eq(i).val();
    objs = $("#filter"+pgid+" :checked");
    //if (objs.length==0) alert("no checked!");
    //else alert("checked length="+objs.length);
    for (i = 0; i < objs.length; i++)
        xx += "," + objs.eq(i).val();
    g_postFilter = xx.substr(1);
    //alert("g_postFilter="+g_postFilter);
    objs = $("#PAGE_ROWS"+pgid);
    if (objs.length > 0)
        page_rows = $("#PAGE_ROWS"+pgid).val();
    //else
    //    alert("page_rows not found");
    sUrl = document.getElementById("URL"+pgid).value;
    //alert("sUrl="+sUrl);
    ajaxPost("GRID", "1", page_rows, sUrl, "",pgid);
}
/**
 * 傳入 jquery object 轉成 fld1=val1;fld2=val2...的形式
 * @param {type} objs
 * @returns {String}
 */
function getFldVal(objs) {
    var s1 = "";
    for (i = 0; i < objs.length; i++) {
        fldname = $(objs[i]).attr("name");
        //if($(this).is(':disabled, [readonly]')) 
        if (g_action == "A" && $(objs[i]).is(':disabled')) {
            //alert("disabled--"+fldname);
        } else {
            if (fldname !== null) {
                val = $(objs[i]).val();
                s1 += "&" + fldname + "=" + val;
            }
        }
    }
    //alert(s1);
    return (s1);
}

function doNextPage(pgid) {
    if (pgid==null){
        pgid="";
    }
    var oPage, oNum_rows, oPage_rows, sUrl, maxPage;
    oPage = document.getElementById("PAGE"+pgid);
    oPage_rows = document.getElementById("PAGE_ROWS"+pgid);
    oNum_rows = document.getElementById("NUM_ROWS"+pgid);
    sUrl = document.getElementById("URL"+pgid).value;
    maxPage = Math.ceil(oNum_rows.value / oPage_rows.value);
    if (oPage.value < maxPage) {
        oPage.value = parseInt(oPage.value) + 1;
        ajaxPost("GRID", oPage.value, oPage_rows.value, sUrl, "",pgid);
        //document.getElementById("HGridForm").submit();
    } else {
        alert("最多只有" + maxPage + "頁");
    }
}
function doPrevPage(pgid) {
    if (pgid==null){
        pgid="";
    }
    var oPage, oNum_rows, oPage_rows, sUrl;
    oPage = document.getElementById("PAGE"+pgid);
    oPage_rows = document.getElementById("PAGE_ROWS"+pgid);
    oNum_rows = document.getElementById("NUM_ROWS"+pgid);
    sUrl = document.getElementById("URL"+pgid).value;
    if (oPage.value > 1) {
        oPage.value = parseInt(oPage.value) - 1;
        ajaxPost("GRID", oPage.value, oPage_rows.value, sUrl, "",pgid);
        //document.getElementById("HGridForm").submit();
    } else {
        alert("已到第1頁");
    }
}
function GoToPage(pgid) {
    if (pgid==null){
        pgid="";
    }
    //alert(pgid);
    var oPage, oNum_rows, oPage_rows, sUrl, maxPage, n;
    oPage = document.getElementById("PAGE"+pgid);
    oPage_rows = document.getElementById("PAGE_ROWS"+pgid);
    oNum_rows = document.getElementById("NUM_ROWS"+pgid);
    sUrl = document.getElementById("URL"+pgid).value;
    if (oPage==null){
            ajaxPost("GRID", "1", 10, sUrl, "",pgid);
    }else{
        maxPage = Math.ceil(oNum_rows.value / oPage_rows.value);
        n = parseInt(oPage.value);
        if (n > 0 && n <= maxPage) {
            ajaxPost("GRID", oPage.value, oPage_rows.value, sUrl, "",pgid);
            //document.getElementById("HGridForm").submit();
        } else {
            alert("頁數須為 1~" + maxPage);
        }
    }
}
function ChgPageRows(pgid) {
    if (pgid==null){
        pgid="";
    }    
    var oPage, oPage_rows, sUrl;
    oPage = document.getElementById("PAGE"+pgid);
    oPage_rows = document.getElementById("PAGE_ROWS"+pgid);
    sUrl = document.getElementById("URL"+pgid).value;
    oPage.value = 1;
    if (oPage_rows.value >= 4) {
        ajaxPost("GRID", oPage.value, oPage_rows.value, sUrl, "",pgid);
        //document.getElementById("HGridForm").submit();
    } else {
        alert("每頁行數至少要有4行")
    }
}

function show_jMenu($obj) {
    $obj.jMenu({
        openClick: false,
        ulWidth: '110px',
        effects: {
            effectSpeedOpen: 150,
            effectSpeedClose: 150,
            effectTypeOpen: 'slide',
            effectTypeClose: 'slide',
            effectOpen: 'linear',
            effectClose: 'linear'
        },
        TimeBeforeOpening: 100,
        TimeBeforeClosing: 11,
        animatedText: false,
        paddingLeft: 1
    });
}




