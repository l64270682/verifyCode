<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/js/jquery-1.6.1.js"></script>
</head>
<body>
	<script type="text/javascript">
		function sendSms() {
			var phoneNumber = $("#phoneNumber").val();
			var result = phoneNumber.match(/^[1][3,4,5,7,8][0-9]{9}$/);
			var captchaCode = $("#captchaCode").val();
			if (phoneNumber == "") {
				$("#div").html("请填写手机号");
			} else if (result == null) {
				$("#div").html("手机号格式错误");
			}else if (captchaCode == "") {
				$("#div").html("图片验证码为空");
			} else {
				$.ajax({
					type : "post",
					datetype : "html",
					url : "/login/verifyCaptcha/" + captchaCode,
					error : function() { //失败
						alert('发送异常');
					},
					success : function(data) { //成功   
						var obj = JSON.parse(data)
						if (obj.msg == "验证失败") {
							$("#div").html("图片验证码错误");
						}
						if (obj.msg == "验证成功") {
							$.ajax({
								type : "post",
								datetype : "html",
								url : "/login/requestSmsCode" ,
								data:{"phoneNumber":phoneNumber},
								error : function() { //失败
									alert('发送失败');
								},
								success : function(data) { //成功   
									var obj = JSON.parse(data)
									if(obj.msg=="发送失败"){
										alert("服务器内部错误");
									}if(obj.msg=="该ip限流"){
										$("#div").html("该ip限流");			
									}if(obj.msg=="操作频繁"){
										$("#div").html("操作频繁");
									}if(obj.msg=="系统繁忙"){
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
					type : "post",
					datetype : "html",
					url : "/login/verifyCaptcha/" + captchaCode,
					error : function() { //失败
						alert('发送异常');
					},
					success : function(data) { //成功   
						var obj = JSON.parse(data)
						if (obj.msg == "验证失败") {
							$("#div").html("图片验证码错误");
						}
						if (obj.msg == "验证成功") {
							$.ajax({
								type : "post",
								datetype : "html",
								url : "/login/verifySmsCode/" +code,			
								data:{"phoneNumber":phoneNumber},
								error : function() { //失败
									alert('发送失败');
								},
								success : function(data) { //成功   
									var obj = JSON.parse(data)
									if (obj.msg == "验证失败") {
										$("#div").html("验证码不存在或已过期,请重新输入");
									}if(obj.msg=="发送失败"){
										alert("服务器内部错误");
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

		function changeImg() {
			var img = document.getElementById("img");
			img.src = "${pageContext.request.contextPath}/login/requestCaptcha/staticCode?date="
					+ new Date();
		}
	</script>
	<div id="div" style="color: red"></div>
	手机号:
	<input id="phoneNumber" type="text">
	<br />
	<tr>
		<td nowrap width="50"></td>
		<td>图片验证码:<input type="text" id="captchaCode" name="verCode">
		</td>
		<td><img id="img"src="${pageContext.request.contextPath}/login/requestCaptcha/staticCode" /> 
		<a href='#' onclick="javascript:changeImg()" style="color: black;"> <label
				style="color: green;">看不清？换一张</label></a></td>
		</tr><br/>
	 短信验证码:<input type="text" id="code">
	<input type="button" value="获取验证码" onclick="sendSms()"><br/>
	<input id="login" type="button" value="登陆" onclick="login()">
</body>
</html>