<!DOCTYPE HTML>
<%@page import="th.co.gosoft.go10.util.PropertiesUtils" %>
<%@page language="java"	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"></jsp:include>
	
<script type="text/javascript">
	
	var initializeObject; 
	var selectizeControlPost;
	var selectizeControlComment;
	var selectizeControlRead;
	
	$(document).ready(function() {
		initialData();
	});	
	
	function validateSesstion(){
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
	
	function initialData(){
		$.ajax({
            url: '/GO10WebService/GetUserRoleManagementServlet',
            type: 'GET',
            contentType: "application/json",
            error: function() {
                alert("error");
            },
            success: function(data, textStatus, jqXHR) {
				initializeObject = data;
				initialSelectize('txtPost');
				initialSelectize('txtComment');
				initialSelectize('txtRead');
            }
       	});
	}
	
	function initialSelectize(txtId){
		var optionObject;
		var valueList;
		var radioboxId;
		switch(txtId) {
		    case "txtPost":
				radioboxId = "postSpecific";
	    		optionObject = initializeObject.postUserModelList;
	        	valueList = initializeObject.postUser;
		        break;
		    case "txtComment":
				radioboxId = "commentSpecific";
	    		optionObject = initializeObject.commentUserModelList;
	        	valueList = initializeObject.commentUser;
		        break;
		    case "txtRead":
				radioboxId = "readSpecific";
	    		optionObject = initializeObject.readUserModelList;
	        	valueList = initializeObject.readUser; 
		        break;
		}
		var $select = $('#'+txtId).selectize({
	     	valueField: 'empEmail',
		    labelField: 'empName',
		    searchField: ['empName', 'empEmail'],
		    options : optionObject,
			create: false,
			maxItems: null,
		    render: {	
		        item: function(item, escape) {
		            return '<div>' +
		                (item.empName ? '<span class="name">' + escape(item.empName) + '</span>' : '') +
		                (item.empEmail ? '<span class="email">' + escape(item.empEmail) + '</span>' : '') +
		            '</div>';
		        },
		        option: function(item, escape) {
		            var label = item.empName || item.empEmail;
		            var caption = item.empName ? item.empEmail : null;
		            return '<div>' +
		                '<span class="label">' + escape(label) + '</span>' +
		                (caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
		            '</div>';
		        }
		    },
		    load: function(query, callback) {
	    		var selectize = this;
		        if (!query.length) {
		        	return callback();
		        }
		        $.ajax({
		            url: '/GO10WebService/GetEmailFullTextSearchServlet',
		            type: 'GET',
		            data: {empEmail: query},
		            global: false,
		            error: function() {
		                callback();
		            },
		            success: function(res) {
		                callback(res.slice(0, 10));
		            }
	        	});
		    }
		});
		var control = $select[0].selectize;
		control.setValue(valueList);
		if("all" == valueList) {
			closeTxt(txtId);
		} else {
			$("#"+radioboxId).prop("checked", true);
		}
		return control;
	}
	
	function openTxt(txtId){
		$('#'+txtId)[0].selectize.enable();
	}
	
	function closeTxt(txtId){
		$('#'+txtId)[0].selectize.disable();
	}
	
	function saveData(){
		if(validateForm()){
			var obj = getData();
			$.ajax({
	            url: '/GO10WebService/SaveUserRoleManagementServlet',
	            type: 'POST',
	            data: JSON.stringify(obj),
	            contentType: "application/json",
	            error: function() {
	                $("#status").text('Error.').css("display", "block").css("color", "red");
	            },
	            success: function(res, textStatus, jqXHR) {
					if(201 == jqXHR.status){
						$("#status").text('Complete Saving.').css("display", "block").css("color", "green");
					}
	            }
        	});
		}
	}

	function getData(){
		var obj = new Object();
		var radUserPost = $("input[name=radUserPost]:checked").val();
		if(radUserPost == 'all') {
			obj.postUser = [radUserPost];
		} else if (radUserPost == 'specific') {
			obj.postUser = $("#txtPost").val().split(',');
		}
		var radUserComment = $("input[name=radUserComment]:checked").val();
		if(radUserComment == 'all') {
			obj.commentUser = [radUserComment];
		} else if (radUserComment == 'specific') {
			obj.commentUser = $("#txtComment").val().split(',');
		}
		var radUserRead = $("input[name=radUserRead]:checked").val();
		if(radUserRead == 'all') {
			obj.readUser = [radUserRead];
		} else if (radUserRead == 'specific') {
			obj.readUser = $("#txtRead").val().split(',');
		}
		return obj;
	}
	
	function validateForm(){
		var result = true;
		$("#status").text('');
		if ($("input[name=radUserPost]:checked").val() == 'specific' && $("#txtPost").val() == '') { 
			$("#status").text('Please enter email account in Post feild.').css("display", "block").css("color", "red");
			result = false;
		} else if ($("input[name=radUserComment]:checked").val() == 'specific' && $("#txtComment").val() == '') {
			$("#status").text('Please enter email account in Comment feild.').css("display", "block").css("color", "red");
			result = false;
		} else if ($("input[name=radUserRead]:checked").val() == 'specific' && $("#txtRead").val() == '') {
			$("#status").text('Please enter email account in Read feild.').css("display", "block").css("color", "red");
			result = false;
		}
		return result; 
	}
	
	$body = $("body");
	$(document).on({
		ajaxStart: function() { $body.addClass("loading"); },
		ajaxStop: function() { $body.removeClass("loading"); }    
	});
	
</script>
	<div class="modal"><!-- Place at bottom of page --></div>
	<div class="container">
		<h3>User Role Management</h3>
		
		<br>
		<div class="col-md-12 col-xs-12 col-sm-12">
			<div class="row role-div">
				<div class="col-xs-12" style="text-align: left;">
					<div class="form-group">
						<label for="radUserPost" class="col-md-2 col-xs-4 control-label"><h4>Post</h4></label>
						<div class="col-xs-8 radio-magin">
							<label class="radio-inline"><input type="radio" name="radUserPost" id="postAll" value="all" checked="checked" onclick="closeTxt('txtPost')">All Users</label>
							<label class="radio-inline"><input type="radio" name="radUserPost" id="postSpecific" value="specific" onclick="openTxt('txtPost')">Specific User</label>
						</div>
					</div>
				</div>
				<div class="col-md-10 col-md-offset-2" style="text-align: left;">
					<input type="text" id="txtPost" style="width: 100%;" class="contacts">
				</div>
			</div>
			<div class="row role-div">
				<div class="col-xs-12" style="text-align: left;">
					<div class="form-group">
						<label for="radUserComment" class="col-md-2 col-xs-4 control-label"><h4>Comment</h4></label>
						<div class="col-xs-8 radio-magin">
							<label class="radio-inline"><input type="radio" name="radUserComment" id="commentAll" value="all" checked="checked" onclick="closeTxt('txtComment')">All Users</label>
							<label class="radio-inline"><input type="radio" name="radUserComment" id="commentSpecific" value="specific" onclick="openTxt('txtComment')">Specific User</label>
						</div>
					</div>
				</div>
				<div class="col-md-10 col-md-offset-2" style="text-align: left;">
					<input type="text" id="txtComment" style="width: 100%;" class="contacts">
				</div>
			</div>
			<div class="row role-div">
				<div class="col-xs-12" style="text-align: left;">
					<div class="form-group">
						<label for="radUserRead" class="col-md-2 col-xs-4 control-label"><h4>Read</h4></label>
						<div class="col-xs-8 radio-magin">
							<label class="radio-inline"><input type="radio" name="radUserRead" id="readAll" value="all" checked="checked" onclick="closeTxt('txtRead')">All Users</label>
							<label class="radio-inline"><input type="radio" name="radUserRead" id="readSpecific" value="specific" onclick="openTxt('txtRead')">Specific User</label>
						</div>
					</div>
				</div>
				<div class="col-md-10 col-md-offset-2" style="text-align: left;">
					<input type="text" id="txtRead" style="width: 100%;" class="contacts">
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12" style="text-align: center;">
					<button class="btn btn-primary role-btn" type="button" id="btnSave" onclick="saveData()">Save</button>
					<button class="btn btn-default role-btn" type="button" id="btnCancel">Restore</button>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12" style="text-align: center; margin-bottom: 30px;">
					<br><label id="status" style="display: none;">Message From Server or Validation.</label>
				</div>
			</div>
			<br><br>
		</div>
	</div>
</body>
</html>