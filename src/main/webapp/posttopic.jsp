<!DOCTYPE HTML>
<%@page import="th.co.gosoft.go10.util.PropertiesUtils" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"></jsp:include>


<script type="text/javascript">

// 	$(document).ready(function() {
// 		initialSelectize('txtPoll');
// 	});	


	function validateForm() {
		var title = document.forms["postForm"]["title"].value;
		var content = tinyMCE.get('articleContent').getContent({
			format : 'text'
		});
		var contentHtml = tinyMCE.get('articleContent').getContent();
		
		if (title.trim() == null || title.trim() == "") {
			$("#statusPost").text("Please insert Title.");
			$("#statusPost").css("color", "red");
			return false;
		} else if ((content.trim() == null || content.trim() == "")
				&& contentHtml.indexOf("<img") == -1) {
			$("#statusPost").text("Please insert content.");
			$("#statusPost").css("color", "red");
			return false;
		}else if (document.getElementById("checkboxPoll").checked) {
			var countQuestion = document.getElementById("dropdownCountQuestion").value;
			var defineUserPoll = $('input[name="defineUserPoll"]:checked').val();
			if (countQuestion == 0) {
				
				$("#statusPost").text("Please insert number of question.");
				$("#statusPost").css("color", "red");
				return false;
			} else{
				var txtPoll = document.getElementById("txtPoll").value;
				for (i = 1; i <= countQuestion; i++) {
					var valueQuestion = document.getElementById("questionNumberInput" + i).value;
					if (valueQuestion.trim() == null || valueQuestion.trim() == "") {
						$("#statusPost").text("Please insert question " + i);
						$("#statusPost").css("color", "red");
						return false;
					} else {
						var countChoice = document.getElementById("dropdownCountChoiceQuestionNumber" + i).value;
						if (countChoice == 0) {
							$("#statusPost").text("Please insert number of choice in question " + i + ".");
							$("#statusPost").css("color", "red");
							return false;
						} else {
							for (j = 1; j <= countChoice; j++) {
								var valueChoice = document.getElementById("questionNumber" + i + "choiceNumberInput" + j).value;
								if (valueChoice.trim() == null || valueChoice.trim() == "") {
									$("#statusPost").text("Please insert choice " + j + " in question " + i);
									$("#statusPost").css("color", "red");
									return false;
								}
							}
						}
					}
				}
			}
			if(defineUserPoll == 'specific' && (txtPoll.trim() == null || txtPoll.trim() == "")){
				$("#statusPost").text("Please enter email account in poll feild.");
				$("#statusPost").css("color", "red");
				return false;
			}
		}

	}

	function handleEnter(field, event) {
		var keyCode = event.keyCode ? event.keyCode : event.which ? event.which
				: event.charCode;
		if (keyCode == 13) {
			var i;
			for (i = 0; i < field.form.elements.length; i++)
				if (field == field.form.elements[i])
					break;
			i = (i + 1) % field.form.elements.length;
			field.form.elements[i].focus();
			return false;
		} else
			return true;
	}

	tinyMCE.init({
	 	  selector: '#articleContent',
	 	  //theme : "tinymce-advanced",
	 // 	  theme_advanced_link_targets : "someframe=Some frame,otherframe=Some other frame",
	 	  element_format : 'html',
	 	  entity_encoding : 'raw',
	 	  extended_valid_elements: "b,i,b/strong,i/em",
	 	  preview_styles: "font-size color",
	 	  plugins: "placeholder link autoresize",
	 	  //toolbar1: 'undo redo | styleselect |0 bold italic | alignleft aligncenter alignright | indent outdent | link image',
	 	  toolbar1: 'undo redo bold imageupload link ',
	 // 	  link_context_toolbar: false,
	 	  paste_data_images: true,
	 // 	  relative_urls : false,
	 // 	  remove_script_host : false,
	 	  statusbar: false,
	  	  convert_urls : true,
	 	  menubar : false,
	    setup: function(editor) {
	        var inp = $('<input id="tinymce-uploader" type="file" name="pic" accept="image/*" style="display:none">');
            $(editor.getElement()).parent().append(inp);
            
	        inp.on("change",function(){
	      	  var input = inp.get(0);
	      	  var filesToUpload = input.files;
	      	  var file = filesToUpload[0];
	      	
	      	var img = new Image();
	      	  var reader = new FileReader();  
	      	  reader.onload = function(e) {
	      		
	      		  img.src = e.target.result;
	      		  img.src = reader.result;
	      		var height,width;
	      		img.onload = function () {
	                height = this.height;
	                width = this.width; 
	      		  
	      		  // Resize the image
		 	               var canvas = document.createElement('canvas'),
		 	                    width = width,
		 	                    height = height;
	      		  	alert("width" + width);
	 	               var ratio = Math.round((width/height*100)/100);
	 	               console.log(ratio);
	 
	 	               if(ratio > 1) {
	                      if(ratio == 1.33) {
	                      	console.log("4:3 landscape")
	                          width = 295
	                          height = 222
	                      } else if(ratio == 1.78 || ratio == 1.77) {
	                      	console.log("16:9 landscape")
	                          width = 295
	                          height = 166
	                      } else {
	                      	console.log("Other Resulotion landscape")
	                          width = 295
	                          height = 166
	                      }
	                  } else if(ratio < 1) {
	                      if(ratio == 0.75) {
	                      	console.log("3:4 portrait")
	                          width = 230
	                          height = 307
	                          
	                      } else if(ratio == 0.56) {
	                      	console.log("9:16 portrait")
	                          width = 230
	                          height = 410
	 
	                      } else {
	                      	console.log("Other Resulotion protrait")
	                          width = 230
	                          height = 410
	                      }
	                  } else if(ratio == 1) {
	                  	console.log("1:1 square")
	                      width = 295
	                      height = 295
	                  }
	 
	 	                canvas.width = width;
	 	                canvas.height = height;
	 	                
	 	                canvas.getContext('2d').drawImage(img, 0, 0, width, height);
	 	                var dataurl = canvas.toDataURL('image/jpeg', 0.8);
	 	                var resizedImage = dataURItoBlob(dataurl);
	 	                
	          	  var formdata = new FormData(this);
	          	  formdata.append("file", resizedImage,"filename.jpg");
	                $.ajax({
	                		url : "/<%= PropertiesUtils.getProperties("context_root")%>/UploadServlet",
	            	        type: "POST",
	            	        data:  formdata,
	            	        contentType: false,
	    					processData: false,
	            	        success: function(url) {	
	 //            	            alert("File has been uploaded successfully");
	 //            	          	editor.insertContent('<img src="'+img.src+'"/>');
	            	          	var urlImage = url.substring(13, url.length-2);
	            	          	editor.insertContent('<img src="' + urlImage + '"  width="'+width+'" height="'+height+'" alt="insertImageUrl"' + '" />');
	                       	inp.val('');
	           	        },
	           	        error:function(msg) {
	           	        	 alert("Can't Upload file");
	           	        }
	           	    });
	     	  }
	      	  }
	     	  reader.readAsDataURL(file);
	       });
	       
	       function dataURItoBlob(dataURI) {
	     	  var byteString = atob(dataURI.split(',')[1]);
	     	  var ab = new ArrayBuffer(byteString.length);
	     	  var ia = new Uint8Array(ab);
	     	  for (var i = 0; i < byteString.length; i++) { ia[i] = byteString.charCodeAt(i); }
	     	  return new Blob([ab], { type: 'image/jpeg' });
	     	}
	       
	       
	       editor.addButton( 'imageupload', {
	           text:"IMAGE",
	           id: "imageuploadbtn",
	           icon: false,
	           onclick: function(e) {
	               inp.trigger('click');
	           }
	       });
	   } 
	});
	<%-- tinyMCE
			.init({
				selector : '#articleContent',
				//theme : "tinymce-advanced",
				// 	  theme_advanced_link_targets : "someframe=Some frame,otherframe=Some other frame",
				element_format : 'html',
				entity_encoding : 'raw',
				extended_valid_elements : "b,i,b/strong,i/em",
				preview_styles : "font-size color",
				plugins : "placeholder link autoresize",
				//toolbar1: 'undo redo | styleselect |0 bold italic | alignleft aligncenter alignright | indent outdent | link image',
				toolbar1 : 'undo redo bold imageupload link ',
				// 	  link_context_toolbar: false,
				paste_data_images : true,
				// 	  relative_urls : false,
				// 	  remove_script_host : false,
				statusbar : false,
				// 	  convert_urls : true,
				menubar : false,
				setup : function(editor) {
					var inp = $('<input id="tinymce-uploader" type="file" name="pic" accept="image/*" style="display:none">');
					$(editor.getElement()).parent().append(inp);

					inp
							.on(
									"change",
									function() {

										var randomVar = Math.round(Math
												.random()
												* (9999999999 + 1) - 0.5);
										var boundary = 90000000000 + randomVar;

										var input = inp.get(0);
										var filesToUpload = input.files;
										var file = filesToUpload[0];

										var img = new Image();

										var reader = new FileReader();
										reader.onload = function(e) {
											img.src = e.target.result;
											img.src = reader.result;
											// Resize the image
											var canvas = document
													.createElement('canvas'), width = img.width, height = img.height;
											var ratio = Math.round((width
													/ height * 100) / 100);
											console.log(ratio);

											if (ratio > 1) {
												if (ratio == 1.33) {
													console
															.log("4:3 landscape")
													width = 295
													height = 222
												} else if (ratio == 1.78
														|| ratio == 1.77) {
													console
															.log("16:9 landscape")
													width = 295
													height = 166
												} else {
													console
															.log("Other Resulotion landscape")
													width = 295
													height = 166
												}
											} else if (ratio < 1) {
												if (ratio == 0.75) {
													console.log("3:4 portrait")
													width = 230
													height = 307

												} else if (ratio == 0.56) {
													console
															.log("9:16 portrait")
													width = 230
													height = 410

												} else {
													console
															.log("Other Resulotion protrait")
													width = 230
													height = 410
												}
											} else if (ratio == 1) {
												console.log("1:1 square")
												width = 295
												height = 295
											}

											canvas.width = width;
											canvas.height = height;

											canvas.getContext('2d').drawImage(
													img, 0, 0, width, height);
											var dataurl = canvas.toDataURL(
													'image/jpeg', 0.8);
											var resizedImage = dataURItoBlob(dataurl);
											
											var formdata = new FormData(this);
											formdata.append("file",
													resizedImage,
													"filename.jpg");
											
											$.ajax({
														url : "/<%= PropertiesUtils.getProperties("context_root")%>/UploadServlet",
														type : "POST",
														data : formdata,
														contentType : false,
														processData : false,
														success : function(url) {
															//            	            alert("File has been uploaded successfully");
															//            	          	editor.insertContent('<img src="'+img.src+'"/>');
															alert(">>>>> " + url)
															var urlImage = url.substring(13,url.length - 2);
															alert(urlImage) 
															editor.insertContent('<img src="' + urlImage + '"  width="'+width+'" height="'+height+'" alt="insertImageUrl"' + '" />');
															inp.val('');
														},
														error : function(msg) {
															alert("Can't Upload file");
														}
													});
										}
										reader.readAsDataURL(file);
									});

					function dataURItoBlob(dataURI) {
						var byteString = atob(dataURI.split(',')[1]);
						var ab = new ArrayBuffer(byteString.length);
						var ia = new Uint8Array(ab);
						for (var i = 0; i < byteString.length; i++) {
							ia[i] = byteString.charCodeAt(i);
						}
						return new Blob([ ab ], {
							type : 'image/jpeg'
						});
					}

					editor.addButton('imageupload', {
						text : "IMAGE",
						id : "imageuploadbtn",
						icon : false,
						onclick : function(e) {
							inp.trigger('click');
						}
					});
				}
			}); --%>

	function checkSelected(id) {
		if (id.checked) {
			document.getElementById("selectQuestion").style.display = "inline";
			$("#dropdownCountQuestion").val(0);
			$("#question").empty();
			$("#selectUserPoll").empty();
		} else {
			document.getElementById("selectQuestion").style.display = "none";
		}
	}

	function getCountQuestion() {

		var selects = document.getElementById("dropdownCountQuestion");
		var selectedValue = selects.options[selects.selectedIndex].value;
		var divQuestion = "";
		var divSelectUserPoll = "";
		var choiceTxt = "";
		var countChoice = 5;
		$("#question").empty();
		$("#selectUserPoll").empty();
		for (i = 1; i <= selectedValue; i++) {
			divQuestion = "";
			choiceTxt = "";
			divQuestion += "<div id='divquestionNumber" + i +"'>";
			divQuestion += "<label style='height:auto' id='questionNumberLbl'>ข้อที่ "
					+ i + " &nbsp;</label>";
			divQuestion += "<label style='font-weight:normal;' id='countChoiceTxt'>(จำนวนตัวเลือกที่ต้องการ </label> "
			divQuestion += "<select id='dropdownCountChoiceQuestionNumber" + i
					+ "' name='dropdownCountChoiceQuestionNumber" + i
					+ "' onchange='getCountChoice(" + i + ")'>";
			divQuestion += "<option disabled selected value='0'> -- select option-- </option>";
			for (j = 1; j <= countChoice; j++) {
				choiceTxt += "<option value='" + j + "'>" + j + "</option>";
			}
			divQuestion += choiceTxt;
			divQuestion += "</select>"
			divQuestion += "<label style='font-weight:normal;' id='countChoiceTxt2'> )</label>"
			divQuestion += "<input type='text' id= 'questionNumberInput"
					+ i
					+ "' name='questionNumberInput"
					+ i
					+ "' style='width: 100%;' class='form-control' placeholder='Question...' onkeypress='return handleEnter(this, event)'>";
			divQuestion += "<br>";
			divQuestion += "<div id='choiceQuestionNumber" + i + "'>";
			divQuestion += "</div>";
			divQuestion += "<hr width='50%' size='20' color='red !important'>";

			$("#question").append(divQuestion);

		}

		// 		divSelectUserPoll += "<div class='row role-div'>";
		divSelectUserPoll += "<div style='text-align: left;'>";
		divSelectUserPoll += "<div class='form-group'>";
		divSelectUserPoll += "<label for='defineUserPoll' style='font-weight: normal;'><h4>Select User Poll</h4></label>"
		divSelectUserPoll += "<div>";
		divSelectUserPoll += "<label class='radio-inline'><input type='radio' name='defineUserPoll' id='postAll' value='all' checked='checked' onclick='closeTxt(\"txtPoll\")'>All Users</label>";
		divSelectUserPoll += "<label class='radio-inline'><input type='radio' name='defineUserPoll' id='pollSpecific' value='specific' onclick='openTxt(\"txtPoll\")'>Specific User</label>";
		divSelectUserPoll += "</div>";
		divSelectUserPoll += "</div>";
		divSelectUserPoll += "</div>";
		divSelectUserPoll += "<div cstyle='text-align: left;'>";
		divSelectUserPoll += "<input type='text' name='txtPoll' id='txtPoll' style='width: 100%;' class='contacts' >";
		divSelectUserPoll += "</div>";
		// 		divSelectUserPoll += "</div>";

		$("#selectUserPoll").append(divSelectUserPoll);
		initialSelectize('txtPoll');
	}

	function getCountChoice(questionNumber) {
		var dropdownCountChoice = "dropdownCountChoiceQuestionNumber"
				+ questionNumber;
		var selects = document.getElementById(dropdownCountChoice);
		var selectedValue = selects.options[selects.selectedIndex].value;
		var choiceQuestionNumber = "choiceQuestionNumber" + questionNumber;
		var divChoice = "";

		$("#" + choiceQuestionNumber).empty();
		for (i = 1; i <= selectedValue; i++) {
			divChoice = "";
			divChoice += "<div id='divchoiceNumber" + i + "'>"
			divChoice += "<div class='row'>";
			divChoice += "<div class='col-md-3'><label style='font-weight:normal;' id='choiceNumberLbl'><h4>ตัวเลือกที่ "
					+ i + " : </label></h4></div>"
			divChoice += "<div class='col-md-9'><input type='text' id='questionNumber"
					+ questionNumber
					+ "choiceNumberInput"
					+ i
					+ "' name='questionNumber"
					+ questionNumber
					+ "choiceNumberInput"
					+ i
					+ "' style='width: 100%;' class='form-control' placeholder='Choice...' onkeypress='return handleEnter(this, event)'></div>"
			divChoice += "</div>";
			divChoice += "<br>";
			divChoice += "</div>";
			$("#" + choiceQuestionNumber).append(divChoice);
		}
	}

	function initialSelectize(txtId) {
		var radioboxId = "pollSpecific";
		var valueList = "all";
		var $select = $('#' + txtId)
				.selectize(
						{
							valueField : 'empEmail',
							labelField : 'empName',
							searchField : [ 'empName', 'empEmail' ],
							// 	    options : optionObject,
							create : false,
							maxItems : null,
							render : {
								item : function(item, escape) {
									return '<div>'
											+ (item.empName ? '<span class="name">'
													+ escape(item.empName)
													+ '</span>'
													: '')
											+ (item.empEmail ? '<span class="email">'
													+ escape(item.empEmail)
													+ '</span>'
													: '') + '</div>';
								},
								option : function(item, escape) {
									var label = item.empName || item.empEmail;
									var caption = item.empName ? item.empEmail
											: null;
									return '<div>'
											+ '<span class="label">'
											+ escape(label)
											+ '</span>'
											+ (caption ? '<span class="caption">'
													+ escape(caption)
													+ '</span>'
													: '') + '</div>';
								}
							},
							load : function(query, callback) {
								var selectize = this;
								if (!query.length) {
									return callback();
								}
								$
										.ajax({
											url : '/<%= PropertiesUtils.getProperties("context_root")%>/GetEmailFullTextSearchServlet',
											type : 'GET',
											data : {
												empEmail : query
											},
											global : false,
											error : function() {
												callback();
											},
											success : function(res) {
												callback(res.slice(0, 10));
											}
										});
							}
						});
		var control = $select[0].selectize;
		control.setValue(valueList);
		if ("all" == valueList) {
			closeTxt(txtId);
		} else {
			$("#" + radioboxId).prop("checked", true);
		}
		return control;
	}

	function openTxt(txtId) {
		$('#' + txtId)[0].selectize.enable();

	}

	function closeTxt(txtId) {
		$('#' + txtId)[0].selectize.disable();
	}

	/* For Loading Popup*/
	$body = $("body");

	$(document).on({
		ajaxStart : function() {
			$body.addClass("loading");
		},
		ajaxStop : function() {
			$body.removeClass("loading");
		}
	});
</script>

<div class="modal-loading">
	<!-- Place at bottom of page -->
</div>
<div class="container">
	<h3>Post Topic</h3>
	<br>
	<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12">
		<form name="postForm" id="postForm"
			action="/<%= PropertiesUtils.getProperties("context_root")%>/PostTopicServlet"
			onsubmit="return validateForm()" method="post" accept-charset="UTF-8"
			style="width: 100%;">
			<div class="row">
				<div class="col-md-12" style="text-align: center;">
					<input type="text" name="title" style="width: 100%;"
						class="form-control" placeholder="Title"
						onkeypress="return handleEnter(this, event)">
				</div>
			</div>
			<br>
			<textarea cols="80" rows="10" id="articleContent"
				name="articleContent" placeholder="Write something ..."></textarea>
			<br>

			<div>
			<!-- <label class="radio-inline"><input type="radio" class="col-md-12" name="poll" id="poll" value="all" onclick="showTxt('questionTxt')">Poll</label> -->
			<input type="checkbox" name="checkboxPoll" id="checkboxPoll"
				onchange="checkSelected(this);" /> สร้างแบบสอบถาม
			<div id="selectQuestion" style="display: none">
				<label style="font-weight: normal;" id="countQuestionTxt">(จำนวนคำถามที่ต้องการ
				</label> <select id="dropdownCountQuestion" name="dropdownCountQuestion"
					onchange="getCountQuestion()">
					<option disabled selected value="0">-- select option--</option>
						<%

 		        			for (int i=1;i<=5;i++) {
  						%>
								<option value="<%=i%>"><%=i%></option>
 						<%
 							}    
						%>
				</select> <label style='font-weight: normal;' id="countQuestionTxt2">)</label>
				<div id="question" />
			</div>
			<div id="selectUserPoll" />
			</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<input class="btn btn-primary" type="submit" value="Post Topic"
				style="float: right; width: 20%; margin-top: 20px">
		</div>
	</div>
	</form>
</div>
<div style="text-align: center;">
	<br> <br> <label id="statusPost" style='color: green'>${statusPost}</label>
</div>
</div>
</body>
</html>