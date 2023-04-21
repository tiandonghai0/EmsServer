
function isNumber(val) {
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除
    if (val === "" || val == null) {
        return false;
    }
    if (!isNaN(val)) {
        return true;
    } else {
        return false;
    }
}

function get_remote_data(get_url,all_data){
    $.ajax({
        url:get_url,
        contentType:"application/json",
        type: "POST",
        async:false,
        success: function(data){
            console.log("data",data);
            window.all_data=data.rows;
            set_session("all_data",all_data);//每个页面上h是统一的。
            // set_session(page_data,page_data_val);//每个页面是变化的。
        }
    });
}

function btn_hidden() {
    $("#alert_table").html("");
}

function deepCopyObj(obj) {
    var result = Array.isArray(obj) ? [] : {};
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            if (typeof obj[key] === 'object' && obj[key] !== null) {
                result[key] = deepCopyObj(obj[key]);
            } else {
//                result[key] = null;
                 result[key] = obj[key];
            }
        }
    }
    return result;
}


function post_to_server(url, data) {
    var is_success = { "status": false }
    $.ajax({
        url: url,
        data: JSON.stringify(data),
        type: "POST",
        contentType: "application/json",
        async: false,
        success: function (res) {
            console.log(res);
            // var res=JSON.parse(res);
            is_success.status = res.status;
            console.log("提交数据到服务端。成功。。。。,返回结果是:", res);
        },
        error: function (res) {
            console.log(res, "ajax提交失败")
        }
    });

    return is_success.status;
}

//修改。out
function edit_data() {
    var active_tr = $(".can_bg_active");
    if (active_tr.length < 1) {
        window.alert("请选择一项")
        return;
    }
    var indexs = active_tr.eq(0).attr("indexs");
    add_data(true);//弹框。
    //修改。
    $("#btn_confirm").attr("onclick", "btn_confirm(post_to_server,[" + indexs + ",'edit'])");
}


function add_data(un_delete,last) {
    var alert_str = $(`<div class="alert_container"> <table id="alert_table" class="gridtable"> </table> </div>`);
    $("body").append(alert_str);

    console.log("add_data")
    var left_one_col = $(".can_edit");
    var str = "";

    if (un_delete !== false) {

        $.each(left_one_col, function (key, val) {
            if((key<2)&&last){
                str += `<tr id="${$(val).attr("id")}s"><td>${$(val).attr("id")}</td><td><input placeholder="${$(val).attr('place')}" /></td></tr>`;
            }else{
                str += `<tr id="${$(val).attr("id")}s"><td>${$(val).attr("id")}</td><td><input placeholder="${$(val).attr('place')}" /></td></tr>`;
            }
        });
    }

    str += `<tr >
        <td colspan="2">
            <button style="float:left;margin-left:20px;margin-right:30px;" id="btn_confirm" onclick="btn_confirm(post_to_server,[-1,'add'])">确定</button>
            <button style="float:right;margin-right:20px;margin-left:30px" onclick="btn_hidden()">取消</button>
        </td>
        </tr>`

    $("#alert_table").html(str);


}

function refill_data(n){

    var ori_data=$(`.can_bg_active td:lt(${n})`);
    var alert_table_input=$("#alert_table input");
    $.each(ori_data,function(key,vals){
        alert_table_input.eq(key-1).val($(vals).html());
    })
    

}

function isInteger(obj) {

    return obj % 1 === 0
}
function isMonth(obj) {
	if(!obj){return false}
    if (isInteger(obj)) {
        if ((0<obj)&&(obj< 13)) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}
function isWeek(obj) {
	if(!obj){return false}
    if (isInteger(obj)) {
        if ((0<=obj)&&(obj<= 6)) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}

function selected_tr() {
    $("body").on("click", ".can_bg", function () {
        $(this).toggleClass("can_bg_active");
        $(this).siblings(".can_bg").removeClass("can_bg_active");
    });
}

function GetQueryValue(queryName) {
    var query = decodeURI(window.location.search.substring(1));
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == queryName) { return pair[1]; }
    }
    return null;
}


function render_page(type, data, remark0, remark1) { //all_data.electricPricePeriod;

    console.log("type:",type,"data:",data)

    var template = "";

    var start_arr = [];

    var end_arr = [];

    var attr_arguments = Array.prototype.slice.call(arguments)[4];

    console.log("attr_arguments", attr_arguments);

    for (var i = 0; i < data.length; i++) {
        start_arr.push(data[i][attr_arguments[0]])
        end_arr.push(data[i][attr_arguments[1]])
        var num = i;
        template += '<tr class="can_bg" indexs=\"' + num + "\">" + '<td>' + (num + 1) + '</td>';

        for (var n = 0; n < attr_arguments.length; n++) {
            template += '<td>' + data[i][attr_arguments[n]] + "</td>";
        }

        if (type == "index") {
            /*index*/
            template += '<td onclick="\"' + '\"' + ' class="go_detail" indexs=\"' + num + '\" this_key="seasonPeriod" id="go_season' + num + '">详情</td>' +
                '<td onclick="\"' + '\"' + ' class="go_detail" indexs=\"' + num + '\" this_key="chargePolicy" id="go_charge">详情</td>' +
                "</tr>";
            /*charge*/
        } else if (type == "charge") {
            template += '<td onclick="\"' + '\"' + ' class="go_detail" indexs=\"' + num + '\" this_key="policy" id="go_sub_charge' + num + '">详情</td>' +
                "</tr>";
        } else {
            template += "</tr>";
        }


        if ((start_arr.indexOf(remark0) > -1) && (end_arr.indexOf(remark1) > -1)) {
            $(".span_add").hide();
        } else {
            $(".span_add").show();
        }

    }
    $("#table_01_tboby").html(template);

    setTimeout(function () {
        selected_tr();
    }, 200)
}


function del_data(is_del) {
    var active_tr = $(".can_bg_active");
    if (active_tr.length < 1) {
        window.alert("请选择一项")
        return;
    }
    console.log("点击删除")
    var indexs = active_tr.eq(0).attr("indexs");
    if (is_del == false) {
        add_data(false);//弹框。
    } else {
        add_data(true)
    }

    $("#btn_confirm").attr("onclick", "btn_confirm(post_to_server,[" + indexs + ",'delete'])");
}

function set_session(key, val) {
    window.sessionStorage.setItem(key, JSON.stringify(val));
}
function get_session(key) {
    return JSON.parse(window.sessionStorage.getItem(key));
}