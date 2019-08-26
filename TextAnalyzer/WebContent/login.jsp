<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户登录</title>
</head>
<body>


	<br> 用户登录
	<form action="${pageContext.request.contextPath }/test" method="post">
		文本：<input type="text" name="text" value="今天中午一点在明远一区二楼教师休息室开会，这次会议是老师主持的，大家都尽量早到10分钟左右。收到请回复！@全体成员"><br>
		时间：<input type="text" name="currenttime" value="2016-12-01 12:00:00"><br> 地点：<input
			type="text" name="currentlocation" value="北京"><br> <input
			type="submit" value="登录">
	</form>
</body>
</html>