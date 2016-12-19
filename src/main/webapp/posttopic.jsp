<!DOCTYPE HTML>
<%@page language="java"	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"></jsp:include>
	

<script type="text/javascript">
function validateForm() {
 	var title = document.forms["postForm"]["title"].value;
	var content = tinyMCE.get('articleContent').getContent({ format: 'text' });
	var contentHtml =  tinyMCE.get('articleContent').getContent();
   if (title.trim() == null || title.trim() == "") {
        $("#statusPost").text("Please insert Title.");
        $("#statusPost").css("color", "red");
        return false;
    }else if ((content.trim() == null || content.trim() == "" ) && contentHtml.indexOf("<img") == -1) {
        $("#statusPost").text("Please insert content.");
        $("#statusPost").css("color", "red");
        return false;
    }
}
                
function handleEnter (field, event) {
		var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
		if (keyCode == 13) {
			var i;
			for (i = 0; i < field.form.elements.length; i++)
				if (field == field.form.elements[i])
					break;
			i = (i + 1) % field.form.elements.length;
			field.form.elements[i].focus();
			return false;
		} 
		else
		return true;
	}      
	
var allRadios = document.getElementsByName('checkpin');
var booRadio;
var x = 0;
for(x = 0; x < allRadios.length; x++){

    allRadios[x].onclick = function() {
        if(booRadio == this){
            this.checked = false;
            booRadio = null;
        }else{
            booRadio = this;
        }
    };
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
// 	  convert_urls : true,
	  menubar : false,
   setup: function(editor) {
       var inp = $('<input id="tinymce-uploader" type="file" name="pic" accept="image/*" style="display:none">');
       $(editor.getElement()).parent().append(inp);
       
       
       inp.on("change",function(){
     	  
     	  var randomVar = Math.round(Math.random() * (9999999999+1) - 0.5);
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
	               var canvas = document.createElement('canvas'),
	                    width = img.width,
	                    height = img.height;
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
           	        url: "/GO10WebService/UploadServlet",
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

// function checkPin(radioBtn) {
// 	if(radioBtn.checked == true) {
// 		radioBtn.checked = false;
// 		radioBtn.value = "false";
// 		return false;
// 	}
// 	else {
// 		radioBtn.checked = true;
// 		radioBtn.value = "true";
// 		return true;
// 	}
// }
/* For Loading Popup*/
$body = $("body");

$(document).on({
	ajaxStart: function() { $body.addClass("loading");    },
	ajaxStop: function() { $body.removeClass("loading"); }    
});
</script>

</head>
<body>
	<div class="modal"><!-- Place at bottom of page --></div>
	<div class="container">
		<h3>Post Topic</h3>
		<br>
		<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12">
			<form name="postForm" id="postForm" action="/GO10WebService/PostTopicServlet" onsubmit="return validateForm()" method="post" accept-charset="UTF-8" style="width: 100%;">
				<div class="row">
					<div class="col-md-12" style="text-align: center;">
						<input type="text" name="title" style="width: 100%;" class="form-control" placeholder="Title" onkeypress="return handleEnter(this, event)">
					</div>
				</div>
				<br>
				<textarea cols="80" rows="10" id="articleContent" name="articleContent" placeholder="Write something ..."></textarea>
				<div class="row">
					<!-- <div class="col-md-6"><input class='radio-button' type="radio" id="checkpin" name="checkpin" value="" style="width: 10%; margin-top: 30px" onClick="return false" onMouseDown="checkPin(this)"/>PIN</div> 
				<div class="col-md-6"><input class="btn btn-primary" type="submit" value="Post Topic" style="float: right; width: 20%; margin-top: 20px" ></div> -->
					<div class="col-md-12">
						<input class="btn btn-primary" type="submit" value="Post Topic"style="float: right; width: 20%; margin-top: 20px">
					</div>
				</div>
			</form>
		</div>
		<div class="col-md-6 col-md-offset-3 col-xs-12 col-sm-12"style="text-align: center;">
			<br>
			<br>
			<label id="statusPost" style='color:green'>${statusPost}</label>
		</div>
	</div>
</body>
</html>