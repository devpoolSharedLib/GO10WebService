<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>GO10 Registration</title>

<script src="js/jquery-1.11.3.js"></script>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
<script src="bootstrap/js/bootstrap.min.js"></script>

<script>
function validateForm() {
	var name = document.forms["regisForm"]["name"].value;
	var email = document.forms["regisForm"]["email"].value;
    if (name == null || name == "" || email == null || email == "") {
        $("#status").text("Please insert Name and Email.");
        $("#status").css("color", "red");
        return false;
    } else if(!validateEmail(email) || !checkGosoftEmailPattern(email)){
    	$("#status").text("Please insert @gosoft.co.th email.");
        $("#status").css("color", "red");
        return false;
    }
}

function checkGosoftEmailPattern(email){
	return email.indexOf("@gosoft.co.th") != -1;
}

function validateEmail(email) {
	var reg = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return reg.test(email);
}

</script>

</head>
<body>
	<nav class="navbar navbar-default">
	  <div class="container-fluid">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	    	<img  src="images/go10_logo.png" alt="GO10 Logo" height=70 width="70">
			<b>GO10 Registration</b>
	    </div>
	
	    
	  </div><!-- /.container-fluid -->
	</nav>
	
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12">
		<form name="regisForm" action="/GO10WebService/RegisterServlet" onsubmit="return validateForm()" method="get" style="width: 100%; text-align: center;">
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Name : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="text" name="name" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Email (@gosoft.co.th) : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="text" name="email" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-12"><input class="btn btn-primary" type="submit" value="Submit" style="width: 50%; margin-top: 20px" ></div>
			</div>
			
				
		</form>
	</div>
	
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12" style="text-align: center;">
		<br><br><label id="status">${status}</label>
	</div>
	
</body>
</html>