/**
 * Created by Administrator on 2017/12/11.
 */

var countdown = 60;
//获取验证码按钮计时器
function settime(val) {
    if (countdown == 0) {
        val.removeAttribute("disabled");
        val.value = "免费获取验证码";
        countdown = 60;
        return;
    } else {
        val.setAttribute("disabled", true);
        val.value = "重新发送(" + countdown + ")";
        countdown--;
    }
    setTimeout(function () {
        settime(val)
    }, 1000)
}

//发送短信
function sendSms(val) {
    var phoneNumber = $("#phoneNumber").val();
    var result = phoneNumber.match(/^[1][3,4,5,7,8][0-9]{9}$/);
    var captchaCode = $("#captchaCode").val();
    if (phoneNumber == "") {
        $("#div").html("请填写手机号");
    } else if (result == null) {
        $("#div").html("手机号格式错误");
    } else if (captchaCode == "") {
        $("#div").html("图片验证码为空");
    } else {
        settime(val);
        $.ajax({
            type: "post",
            datetype: "html",
            url: "/login/verifyCaptcha/" + captchaCode,
            error: function () { //失败
                alert('发送异常');
            },
            success: function (data) { //成功
                var obj = JSON.parse(data)
                if (obj.msg == "验证失败") {
                    $("#div").html("图片验证码错误");
                }
                if (obj.msg == "验证成功") {
                    $.ajax({
                        type: "post",
                        datetype: "html",
                        url: "/login/requestSmsCode",
                        data: {"phoneNumber": phoneNumber},
                        error: function () { //失败
                            alert('发送失败');
                        },
                        success: function (data) { //成功
                            var obj = JSON.parse(data)
                            alert(obj.msg);
                            if (obj.msg == "发送失败") {
                                alert("服务器内部错误");
                            }
                            if (obj.msg == "该ip限流") {
                                $("#div").html("该ip限流");
                            }
                            if (obj.msg == "操作频繁") {
                                $("#div").html("操作频繁");
                            }
                            if (obj.msg == "系统繁忙") {
                                $("#div").html("系统繁忙");
                            }
                            if (obj.msg == "短信发送成功") {
                                $("#div").html("短信发送成功")
                            }
                        }
                    });
                }
            }
        });
    }
}

//登录
function login() {
    var phoneNumber = $("#phoneNumber").val();
    var code = $("#code").val();
    var result = phoneNumber.match(/^[1][3,4,5,7,8][0-9]{9}$/);
    var captchaCode = $("#captchaCode").val();
    if (phoneNumber == "") {
        $("#div").html("请填写手机号");
    } else if (result == null) {
        $("#div").html("手机号格式错误");
    } else if (captchaCode == "") {
        $("#div").html("图片验证码为空");
    } else if (code == "") {
        $("#div").html("短信验证码为空");
    } else {
        $.ajax({
            type: "post",
            datetype: "html",
            url: "/login/verifyCaptcha/" + captchaCode,
            error: function () { //失败
                alert('发送异常');
            },
            success: function (data) { //成功
                var obj = JSON.parse(data)
                if (obj.msg == "验证失败") {
                    $("#div").html("图片验证码错误");
                }
                if (obj.msg == "验证成功") {
                    $.ajax({
                        type: "post",
                        datetype: "html",
                        url: "/login/verifySmsCode/" + code,
                        data: {"phoneNumber": phoneNumber},
                        error: function () { //失败
                            alert('发送失败');
                        },
                        success: function (data) { //成功
                            var obj = JSON.parse(data)
                            if (obj.msg == "验证失败") {
                                $("#div").html("验证码不存在或已过期,请重新输入");
                            }
                            if (obj.msg == "发送失败") {
                                alert("服务器内部错误");
                            }
                            if (obj.msg == "该ip限流") {
                                $("#div").html("该ip限流");
                            }
                            if (obj.msg == "验证成功") {
                                window.location.href = "/login/success";
                            }
                        }
                    });
                }
            }
        });
    }
}

//更换图片
function changeImg() {
    var img = document.getElementById("img");
    img.src = "${pageContext.request.contextPath}/login/requestCaptcha/staticCode?date="
    + new Date();
}
