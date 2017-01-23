<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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

<!-- Datepicker -->
<link rel="stylesheet" href="./datepicker/css/datepicker.css" type="text/css"/>
<script type="text/javascript" src="./datepicker/js/bootstrap-datepicker.js"></script>
 
<script type="text/javascript" src="./datepicker/js/bootstrap-datepicker-thai.js"></script>
<script type="text/javascript" src="./datepicker/js/bootstrap-datepicker.locale.th.js"></script>
 
<!-- Maskedinput -->
<script type="text/javascript" src="./datepicker/js/jquery.maskedinput.min.js"></script>
	
<script>
function validateForm() {
	var surname = document.forms["regisForm"]["surname"].value;
	var lastname = document.forms["regisForm"]["lastname"].value;
	var email = document.forms["regisForm"]["email"].value;
	var birthday = document.forms["regisForm"]["birthday"].value;
	var password = document.forms["regisForm"]["password"].value;
	var confirmPassword = document.forms["regisForm"]["confirmPassword"].value;
    if (surname == null || surname == "" || lastname == null || lastname == "") {
        $("#statusRegis").text("Please insert surname and lastname.");
        $("#statusRegis").css("color", "red");
        return false;
    }else if (email == null || email == "") {
        $("#statusRegis").text("Please insert Email.");
        $("#statusRegis").css("color", "red");
        return false;
    }else if (birthday == null || birthday == "" ) {
        $("#statusRegis").text("Please insert Birthday.");
        $("#statusRegis").css("color", "red");
        return false;
    }else if(!isEmailFommat(email)||!isGosoftEmailFommat(email)){
    	$("#statusRegis").text("Please insert correct email.");
        $("#statusRegis").css("color", "red");
        return false;
    }else if(password == null || password == "" || confirmPassword == null || confirmPassword == "" || password != confirmPassword){
    	$("#statusRegis").text("Please insert correct password.");
        $("#statusRegis").css("color", "red");
        return false;
    }
}

function isGosoftEmailFommat(email){
	return email.indexOf("@gosoft.co.th") != -1;
}

function isEmailFommat(email) {
	var reg = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return reg.test(email);
}


$(document).ready(function() {
// 	 createSingleDatepicker("singleDatepickerDiv", "birthday");
	$("#birthday").mask("99-99-9999",{placeholder:"dd-mm-yyyy"});
});	

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
		<form name="regisForm" action="/GO10WebService/RegisterServlet" onsubmit="return validateForm()" method="post" style="width: 100%; text-align: center;">
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Firstname : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="text" name="surname" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Lastname : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="text" name="lastname" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Email : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="text" name="email" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Password : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="password" name="password" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Confirm Password : </h4></div>
				<div class="col-md-8" style="text-align: center;"><input type="password" name="confirmPassword" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-4" style="text-align: left;"><h4>Birthday : </h4></div>
<!-- 				<div class="col-md-8" id="singleDatepickerDiv"></div> -->
				<div class="col-md-8" style="text-align: center;"><input type="text" id="birthday" name="birthday" style="width: 100%;" class="form-control"></div>
			</div>
			<div class="row">
				<div class="col-md-12"><input class="btn btn-primary" type="submit" value="Submit" style="width: 50%; margin-top: 20px" ></div>
			</div>
		</form>
	</div>
	
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12" style="text-align: center;">
		<br><br><label id="statusRegis">${statusRegis}</label>
	</div>
	
</body>
</html>