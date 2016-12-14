<!DOCTYPE HTML>
<%@page import="th.co.gosoft.go10.util.SecurityUtils"%>
<%@page import="th.co.gosoft.go10.model.RoomModel"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>GO10 Administration</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<script src="js/jquery-1.11.3.js"></script>
	<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
	<script src="bootstrap/js/bootstrap.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="selectize/css/selectize.bootstrap3.css" />
	<script type="text/javascript" src="selectize/js/standalone/selectize.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/user_role_management.css" />
	
	<script type="text/javascript" src="/GO10WebService/tinymce/js/tinymce/tinymce.js"></script>
	
	<!-- <script type="text/javascript">
		$(document).ready(function() {
			$(document).on({
			    ajaxStart: function() { 
				    $.ajax({   
						url: '/GO10WebService/VerifiedSessionServlet',   
						type: 'GET',  
						dataType: 'json',
						success: function(timeout) {
							alert("timeout : "+timeout);   
							if (timeout) {
								window.location.href = "/GO10WebService/login.jsp";
							}
						},
					}); 
			    },
			    ajaxStop: function() { 
			    
			    }    
			}); 
		});	
	</script> -->
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
		      	<li><a href="main.jsp"><b>GO10 Administration</b></a></li>
		        <li class="dropdown">
		          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Menu <span class="caret"></span></a>
		          <ul class="dropdown-menu">
		            <li><a href="user_role_management.jsp">User Role Management</a></li>
		            <li><a href="posttopic.jsp">Post Topic</a></li>
		            <li><a href="#">Something else here</a></li>
		            <li role="separator" class="divider"></li>
		            <li><a href="#">Separated link</a></li>
		            <li role="separator" class="divider"></li>
		            <li><a href="#">One more separated link</a></li>
		          </ul>
		        </li>
		      </ul>
		      <ul class="nav navbar-nav navbar-right">
		      	<%
					List<RoomModel> groupModelList = SecurityUtils.getInstance().getRoom(session);
				%>
		        <li class="dropdown" style="display: inline;">
		        	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Room :  <%= groupModelList.get(0).getName() %> <span class="caret"></span></a>
		        	<ul class='dropdown-menu'>
		        		<%
		        			for (RoomModel roomModel : groupModelList) {
 						%>
							<li><a href=#>Room : <%=roomModel.getName() %></a></li>
						<%
							}
 						%>
		        	</ul>
		        </li>
		        <li class="dropdown">
		          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><%=session.getAttribute("empEmail") %> <span class="caret"></span></a>
		          <ul class="dropdown-menu">
		            <li><a href="#">Action</a></li>
		            <li role="separator" class="divider"></li>
		            <li><a href="#">Logout</a></li>
		          </ul>
		        </li>
		      </ul>
		    </div><!-- /.navbar-collapse -->
	  	</div><!-- /.container-fluid -->
	</nav>
