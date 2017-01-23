<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<title>GO10 Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<%
	session.invalidate();
	request.getRequestDispatcher("./login.jsp").forward(request, response);
%>
</body>
</html> 
