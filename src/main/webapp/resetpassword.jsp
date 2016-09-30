<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>GO10 Reset Password</title>
<script src="js/jquery-1.11.3.js"></script>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
<script src="bootstrap/js/bootstrap.min.js"></script>
<script>
function validateForm() {
	var password = document.forms["resetForm"]["password"].value;
	var confirmPassword = document.forms["resetForm"]["confirmPassword"].value;
	if(password == null || password == "" || confirmPassword == null || confirmPassword == "" || password != confirmPassword){
    	$("#status").text("Please insert correct password.");
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
	    	<img  src="images/go10_logo.png" alt="GO10 Logo" height=70 width="70">
			<b>GO10 Reset Password</b>
	    </div>
	
	    
	  </div><!-- /.container-fluid -->
	</nav>
	
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12">
		<form name="resetForm" action="/GO10WebService/ResetPasswordServlet" onsubmit="return validateForm()" method="post" style="width: 100%; text-align: center;">
			
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>New Password : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="password" name="password" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Confirm Password : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="password" name="confirmPassword" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-12"><input class="btn btn-primary" type="submit" value="Submit" style="width: 50%; margin-top: 20px" ></div>
			</div>
			<input type="hidden" name="token" value="${param.token}">
		</form>
	</div>
	
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12" style="text-align: center;">
		<br><br><label id="status">${status}</label>
	</div>
</body>
</html>