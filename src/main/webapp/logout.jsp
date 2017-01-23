<!DOCTYPE HTML><%@page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>logout</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="jquery-ui-1.12.1/external/jquery/jquery.js"></script>
</head>
<body>
	<h2>Sample Form Logout</h2>
	<form method="POST" action="ibm_security_logout" name="logout">
	<BR>
	<strong> Click this button to log out: </strong>
	<input type="submit" name="logout" value="Logout">
	<input type="HIDDEN" name="logoutExitPage" value="/login.html">
	</form>
</body>
</html>