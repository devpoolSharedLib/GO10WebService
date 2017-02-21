<!DOCTYPE HTML><%@page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>GO10 Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script src="js/jquery-1.11.3.js"></script>
	<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
	<script src="bootstrap/js/bootstrap.min.js"></script>
	
	<style type="text/css">
		.navbar-brand {
		  padding: 0px;
		}
		.navbar-brand>img {
		  height: 100%;
		  padding: 5px;
		  width: auto;
		}
		.header-word {
		  height: 100%;
		  width: auto;
		  padding: 15px 15px 15px 0;
		  margin: 0;
		  line-height: 20px
		}
	</style>
	
	<script type="text/javascript">
		function validateForm() {
			var email = document.forms["loginForm"]["email"].value;
			var password = document.forms["loginForm"]["password"].value;
		    if (email == null || email == "" || password == null || password == "") {
		        $("#status").text("Please Email and Password.");
		        $("#status").css("display", "block").css("color", "red");
		        return false;
		    }
		    return true;
		}
	</script>
</head>
<body>
	<nav class="navbar navbar-default">
	  <div class="container-fluid">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
			<a class="navbar-brand" href="#">
    			<img src="images/go10_logo.png" alt="GO10 Logo">
			</a>
	    </div>
	    
	    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
	    	<ul class="nav navbar-nav">
		      	<li><h4 class="header-word">GO10 Administration</h4></li>
	      	</ul>
	    </div>
	  </div><!-- /.container-fluid -->
	</nav>
	<div class="container">
		<div class="row">
			<div class="col-md-6 col-md-offset-3 thumbnail" style="background-color: #;">
				<form name="loginForm" method="post" action="/GO10WebService/AdminLoginServlet" onsubmit="return validateForm()">
					<div class="row" style="margin: 0 20px 20px 20px; padding: 0 20px 20px 20px" >
						<h1 style="text-align: center">Login</h1>
						<div class="row">
							<div class="col-md-12" style="text-align: left;"><h4>Email : </h4></div>
							<div class="col-md-12" style="text-align: center;"><input type="text" name="j_username" style="width: 100%;" class="form-control"></div>
						</div>
						<div class="row" style="margin-top: 10px">
							<div class="col-md-12" style="text-align: left;"><h4>Password : </h4></div>
							<div class="col-md-12" style="text-align: center;"><input type="password" name="j_password" style="width: 100%;" class="form-control"></div>
						</div>
					</div>
					<div class="row" style="margin: 0 20px 20px 20px; text-align: center">
						<button type="submit" class="btn btn-primary" id="action" name="action" style="width: 75px;">Login</button>&nbsp; 
						<button type="button" class="btn btn-default" id="clear" style="width: 75px;" onclick="clearCriteria();">Clear</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>