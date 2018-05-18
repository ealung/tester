$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};
$.fn.parameter = function () {
    var url ="?timeversion="+new Date().getTime();
    var a = this.serializeArray();
    $.each(a, function () {
        url+="&"+this.name+"="+this.value;
    });
    return url;
};
function getPrefix(location) {
    return location.substr(0, location.indexOf("/tester"))
}

function getRequestMethod(requestMethod) {
    var startKdb = "<kbd style='background-color: #53bdc7'>";
    var html = startKdb;
    requestMethod.forEach(function (value) {
        html += startKdb + value + '</kbd>';
    })
    if (html.length === startKdb.length) {
        html += "All";
    }
    html += "</kbd>";
    return html;
}

var jsonType = "application/json";
var formType = "application/x-www-form-urlencoded";

function getResponseType(webTest) {
    var html = "<kbd class='typekbd' style='background-color: #ff779d'>";
    if (webTest.jsonReturn) {
        html += jsonType;
    } else {
        html += formType;
    }
    if (webTest.jsonParameter) {
        html += "<b value='"+jsonType+"'></b>";
    } else {
        html += "<b value='"+formType+"'></b>";
    }
    html += "</kbd>";
    return html;
}
/**
 * 获取提交方式
 */
function getMethod(parents) {
    var method = "GET";
    $(parents).find(".method>kbd").each(function () {
        var value = $(this).text();
        if (value === "All") {
            method = "GET";
        } else {
            method = value;
        }
        return;
    });
    return method;
}

/**
 * json方式提交
 * @param url 提交地址
 * @param method 提交方式
 * @param json 提交的json数据
 */
function jsonHttp(button,url, method, json,contextType) {
    $(button).attr("disabled","disabled");
    $.ajax({
        type: method,
        url: url,
        data: json,
        contentType:contextType,
        success: function (msg) {
            var maxLength = url.length + method.length+20;
            var single="";
            for(var i=0;i<maxLength;i++){
                single += "-";
            }
            single += "-";
            console.log("+" + single)
            console.log(" |  url->    " + url + "(" + method + ")");
            console.log(" |  result-> "+JSON.stringify(msg));
            console.log("+" + single);

            if(msg.code){
                if(msg.code=== 0){
                    $(button).attr("class", "btn btn-success");
                }else {
                    $(button).attr("class","btn btn-danger");
                }
            }else{
                $(button).attr("class", "btn btn-success");
            }
            $(button).removeAttr("disabled");
        },
        error:function () {
            $(button).attr("class","btn btn-danger");
            $(button).removeAttr("disabled");
        }
    });
}
function loadMapping(mappingName) {
    var url="/mapping?1=1";
    if(mappingName){
        url+="&mappingName="+mappingName;
    }
    if($("#controller").is(":checked")==true){
        url+="&mapping=1";
    }else{
        url+="&mapping=0";
    }

    $.getJSON(url, function (data) {
        $(data).each(function (i, webTest) {
            var test = $("#template").clone();
            test.removeAttr("id");
            test.show();
            test.find(".panel-title>a").text(getPrefix(window.location.href) + webTest.url);
            test.find(".method").html(getRequestMethod(webTest.requestMethod));
            test.find(".type").html(getResponseType(webTest));
            test.find(".type").append("<kbd style='margin-left: 30px;'>"+webTest.beanName+"</kdb>");
            test.find(".panel-title>a").attr("href", "#body-" + i);
            test.find(".panel-collapse").attr("id", "body-" + i);
            webTest.parameter.forEach(function (value) {
                value.forEach(function (param) {
                    var text = $("#textTemplate").clone();
                    text.find("label").text(param.k);
                    text.find(".form-control").attr("name", param.k);
                    text.find(".form-control").attr("placeholder", param.v);
                    text.find(".form-control").val(param.v);
                    text.show();
                    $(test).find("form").append(text);
                })
            });
            $("#test").append(test);
            test.find("button").bind("click", function () {
                setFocus($(this));
                var parents = $(this).parents(".panel");
                var url = $(parents).find("h4>a").text();
                url = formatUrl(parents,url);
                var responseType = $(parents).find(".typekbd").text();
                var requestType = $(parents).find("b").attr("value");
                var method = getMethod(parents);
                if (responseType === jsonType||requestType===jsonType) {
                    var json = $(parents).find("form").serializeObject();
                    if(requestType===jsonType){
                        json=JSON.stringify(json);
                    }
                    jsonHttp($(this),url,method,json,requestType);
                } else {
                    var parameter = $(parents).find("form").parameter();
                    window.open(url+parameter, '_blank');
                }
            });
        });
    });
}
function setFocus(button) {
    var parents = $(button).parents(".panel");
    $(".panel-title").removeClass("bg-primary");
    $(parents).find(".panel-title").addClass("bg-primary");
}
function changeSearch() {
    if($("#controller").is(":checked")==true){
        $("#searchText").attr("placeholder","请输入查询的classes关键字");
        $("#ctext").html("classes:");
    }else{
        $("#searchText").attr("placeholder","请输入查询的url关键字");
        $("#ctext").html("url：");
    }

}
function search() {
    $("#test").html("");
    var context = $("#searchText").val();
    loadMapping(context);
}
$(function () {
    loadMapping();
    $("#search").bind("click",function () {
        search();
    });
    $("#searchText").keydown(function (e) {
        if(e.keyCode === 13){
            search();
        }
    });
});