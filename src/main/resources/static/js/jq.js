$(window).load(function () {


    // 图片不存在时用默认图片显示
    $('img').each(function () {

        if (!this.complete || typeof this.naturalWidth == "undefined" || parseInt(this.naturalWidth) === 0) {
            this.src = '/img/avatars/not_found.png';
            this.title = "预览图无效"

        }
    });
});


// 全局 Ajax 配置
$.ajaxSetup({
    cache: false,
    error: function (xhr, textStatus, errorThrown) {

        var msg = xhr.responseText;
        alert("error111111111111111:"+msg);
        var response = JSON.parse(msg);
        var code = response.code;
        var message = response.message;
        if (parseInt(code) === 400) {


            layer.msg("操作失败，原因：" + message, {icon: 5});

        } else if (parseInt(code) === 401) {

            setTimeout(function () {

                location.href = "/login.html";

            }, 1500);

            layer.msg('您还未登录或者登录已经过期，将为您自动跳转到登录页面', {icon: 5});


        } else if (parseInt(code)  === 403) {

            console.warn("未授权:" + message, {icon: 5});

            setTimeout(function () {

                location.href = "/login.html";

            }, 1500);

            alert("您没有权限操作");
            layer.msg('您没有权限操作', {icon: 5});

        } else if (parseInt(code) === 500) {

            console.error(message);
            layer.msg('系统错误【' + message + "】，请稍后再试或者联系管理员", {icon: 5});


        }
    }
});


function buttonDel(data, permission, pers) {
    if (permission !== "") {
        if ($.inArray(permission, pers) < 0) {
            return "";
        }
    }

    var btn = $("<button class='layui-btn layui-btn-xs' title='删除' onclick='del(\"" + data + "\")'><i class='layui-icon'>&#xe640;</i></button>");
    return btn.prop("outerHTML");
}

function buttonEdit(href, permission, pers) {
    if (permission !== "") {
        if ($.inArray(permission, pers) < 0) {
            return "";
        }
    }

    var btn = $("<button class='layui-btn layui-btn-xs' title='编辑' onclick='window.location=\"" + href + "\"'><i class='layui-icon'>&#xe642;</i></button>");
    return btn.prop("outerHTML");
}

function buttonReply(href, permission, pers) {
    if (permission !== "") {
        if ($.inArray(permission, pers) < 0) {
            return "";
        }
    }

    var btn = $("<button class='layui-btn layui-btn-xs'  title='回复消息' onclick='window.location=\"" + href + "\"'><i class=\"layui-icon\">&#xe611;</i></button>");
    return btn.prop("outerHTML");


}


function deleteCurrentTab() {
    var lay_id = $(parent.document).find("ul.layui-tab-title").children("li.layui-this").attr("lay-id");
    parent.active.tabDelete(lay_id);
}

