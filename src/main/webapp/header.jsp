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
	
	<script src="jquery-ui-1.12.1/jquery-ui.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="selectize/css/selectize.bootstrap3.css" />
	<script type="text/javascript" src="selectize/js/standalone/selectize.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/user_role_management.css" />
	
	<script type="text/javascript" src="/GO10WebService/tinymce/js/tinymce/tinymce.js"></script>

	<style type="text/css">
		/* Start by setting display:none to make this hidden.
		   Then we position it in relation to the viewport window
		   with position:fixed. Width, height, top and left speak
		   for themselves. Background we set to 80% white with
		   our animation centered, and no-repeating */
		.modal {
			display: none;
			position: fixed;
			z-index: 1000;
			top: 0;
			left: 0;
			height: 100%;
			width: 100%;
			background: rgba(255, 255, 255, .8)
				url('http://i.stack.imgur.com/FhHRx.gif') 50% 50% no-repeat;
		}
		
		/* When the body has the loading class, we turn
		   the scrollbar off with overflow:hidden */
		body.loading {
			overflow: hidden;
		}
		
		/* Anytime the body has the loading class, our
		   modal element will be visible */
		body.loading .modal {
			display: block;
		}
		
	</style>
	
	<script type="text/javascript" >
		function gotoSessionServlet(roomId,roomName) {
			var path = window.location.pathname;
			var currentPage = path.split("/").pop();
			window.location.href = "/GO10WebService/SessionServlet?roomId=" + roomId + "&roomName=" + roomName + "&currentPage=" + currentPage
		}
	</script>

	<script type="text/javascript">
		$(document).ready(function() {
// 			validateSesstion();
// 			$(document).on({
// 			    ajaxStart: function() { 
// 				    $.ajax({   
// 						url: '/GO10WebService/VerifiedSessionServlet',   
// 						type: 'GET',  
// 						dataType: 'json',
// 						success: function(timeout) {
// 							if (timeout) {
// 								window.location.href = "/GO10WebService/login.jsp";
// 							}
// 						},
// 					}); 
// 			    },
// 			    ajaxStop: function() { 
			    
// 			    }    
// 			}); 
		});	
		
		function validateSesstion() {
			$.ajax({   
				url: '/GO10WebService/VerifiedSessionServlet',   
				type: 'GET',  
				dataType: 'json',
				success: function(timeout) {
					if (timeout) {
						window.location.href = "/GO10WebService/login.jsp";
					}
				},
			}); 
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
		      	<li><a href="main.jsp"><b>GO10 Administration</b></a></li>
		        <li class="dropdown">
		          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Menu <span class="caret"></span></a>
		          <ul class="dropdown-menu">
		            <li><a href="user_role_management.jsp">User Role Management</a></li>
		            <li><a href="posttopic.jsp">Post Topic</a></li>
		            <!-- <li><a href="#">Something else here</a></li>
		            <li role="separator" class="divider"></li>
		            <li><a href="#">Separated link</a></li>
		            <li role="separator" class="divider"></li>
		            <li><a href="#">One more separated link</a></li> -->
		          </ul>
		        </li>
		      </ul>
		      <ul class="nav navbar-nav navbar-right">
		      	<%
 		      		System.out.print("session : "+session != null);
 		      		if(session != null) {
 						List<RoomModel> groupModelList = SecurityUtils.getInstance().getRoom(session);
				%>
 		        <li class="dropdown" style="display: inline;">
 		        	<a href="#" class="dropdown-toggle" id="roomName" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Room :  <%=session.getAttribute("roomName") %> <span  class="caret"></span></a>
 		        	<ul class='dropdown-menu' id="menuRoomName">
 		        		<%
 		        			for (RoomModel roomModel : groupModelList) {
  						%>
							<li><a href="javascript:gotoSessionServlet('<%=roomModel.get_id() %>','<%=roomModel.getName() %>');">Room : <%=roomModel.getName() %></a></li>
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
 		            <li><a href="/GO10WebService/LogoutServlet">Logout</a></li>
 		          </ul>
 		        </li>
 		        <%
 		        	}
				%>
		      </ul>
		    </div><!-- /.navbar-collapse -->
	  	</div><!-- /.container-fluid -->
	</nav>
