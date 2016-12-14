<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>GO10 Administrator</title>

<script src="/GO10WebService/js/jquery-1.11.3.js"></script>
<link rel="stylesheet" href="/GO10WebService/bootstrap/css/bootstrap.min.css">
<script src="/GO10WebService/bootstrap/js/bootstrap.min.js"></script>

<script>
function validateForm() {
	var username = document.forms["loginForm"]["username"].value;
	var password = document.forms["loginForm"]["password"].value;
	
    if (username == null || username == "" || password == null || password == "") {
        $("#status").text("Please insert username and password.");
        $("#status").css("color", "red");
        return false;
    }
}
</script>
</head>
<body>
<nav class="navbar navbar-default">
	  <div class="container-fluid">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	    	<img  src="/GO10WebService/images/go10_logo.png" alt="GO10 Logo" height=70 width="70">
			<b>GO10 Administrator</b>
	    </div>
	
	    
	  </div><!-- /.container-fluid -->
	</nav>
	
	<div class="col-md-4 col-md-offset-4 col-xs-12 col-sm-12">
		<form name="loginForm" action="/GO10WebService/AdminLoginServlet" onsubmit="return validateForm()" method="post" style="width: 100%; text-align: center;">
			<div class="row">
				<div class="col-md-12" style="text-align: left;"><h4>Username : </h4></div>
				<div class="col-md-12" style="text-align: center;"><input type="text" name="username" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-12" style="text-align: left;"><h4>Password : </h4></div>
				<div class="col-md-12" style="text-align: center;"><input type="text" name="password" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-12"><input class="btn btn-primary" type="submit" value="Login" style="width: 50%; margin-top: 20px" ></div>
			</div>
			
				
		</form>
	</div>
	<div class="col-md-4 col-md-offset-4 col-xs-12 col-sm-12" style="text-align: center;">
		<br><br><label id="status">${status}</label>
	</div>
</body>
</html>