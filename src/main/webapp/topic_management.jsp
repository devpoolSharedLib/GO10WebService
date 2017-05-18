<!DOCTYPE HTML>
<%@page language="java"	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"></jsp:include>

<style type="text/css">
	.icon-table {
		width: 25px;
		height: 25px;
		margin: 0px 5px 0px 5px;
	}
	
	div.paging {
		cursor: pointer;
		margin-left: 5px;
	}
	
	.no-boarder {
		border: none;
	}

	div.dropdown {
		display: inline;
	}
</style>

<script type="text/javascript">

	$body = $("body");
	$(document).on({
		ajaxStart: function() { $body.addClass("loading"); },
		ajaxStop: function() { $body.removeClass("loading"); }    
	});
	
	var rowsPerPage = 30;
	var iconSize = 20;
	var pinTopicList;
	var nopinTopicList;
	var deletePinList = [];
	var bookmarkList = [];
	var currentPage = 1;
	var elePin =  null
	
	$(document).ready(function(){
		clearAllTable();
		initialData();
	});
	
	function clearAllTable(){
		$("#topicTable > tbody").empty();
		$("#pinTable > tbody").empty();
	}	
	
	function initialData(){
		$.ajax({
            url: '/GO10WebService/GetToppicManagementServlet',
            type: 'GET',
            contentType: "application/json",
            error: function() {
                alert("initialData error");
            },
            success: function(data, textStatus, jqXHR) {
				pinTopicList = data.pinTopicList;
				nopinTopicList = data.noPinTopicList;
				showTotalRowsAndSetPaging(data.totalRows);
				addBookmarkToObject(2, data.bookmark);
				insertDataToTable(pinTopicList, nopinTopicList);
				insertPinTable(pinTopicList);
            }
       	});
	}
	
	function addBookmarkToObject(paging, bookmark){
		bookmarkList[paging] = bookmark;
	}
	
	function insertDataToTable(pinTopicList, nopinTopicList){
		$("#topicTable > tbody").html("");
		var startIndex = 1 + ((currentPage-1)*rowsPerPage);
		var allTopicList = concatList(pinTopicList, nopinTopicList);
		insertEachRow(startIndex, allTopicList);
	}
	
	function concatList(pinTopicList, nopinTopicList){
		return pinTopicList.concat(nopinTopicList);
	}
	
	function insertEachRow(startIndex, topicList){
		$.each(topicList, function(index, obj){
			var rowCount = $('#topicTable tbody tr').length;
			var rowString = "<tr>";
			rowString += "<td style='display: none;''>"+obj._id+"</td>";
			rowString += "<td style='text-align:center'>"+(startIndex)+"</td>";
			rowString += "<td style='text-align:center'>"+obj.empEmail+"</td>";
			rowString += "<td>"+obj.subject+"</td>";
			rowString += "<td style='text-align:center'>"+obj.date+"</td>";
			if (obj.pin != null) {
				rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/pin-"+(index+1)+".png' class='icon-table' data-toggle='modal' data-target='#pinTopicModal'/>"
				+"</td>";
			} else {
				rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/pin.png' class='icon-table' onclick='openModal(this)' />"
				+"</td>";
			}
			rowString += "<td style='text-align:right'>";
			rowString += "<table style='width: 100%';>";
			rowString += "<tr>";
			
			rowString += "<td style='text-align:center; width:33%;'>";
			if(obj.countRead != 0){
				rowString += "<a href='#' onclick='showEmpEmailReadModal(\""+obj._id+"\")'><img src='./images/readCount.png' alt='Smiley face' height='20' width='20' title='จำนวนผู้ตอบคำถาม' styel='vertical-align: bottom;'>  "+obj.countRead+"</a>";
			}
			rowString += "</td>";
			
			rowString += "<td style='text-align:center; width:33%;'>";
			if(obj.countLike != 0){
				rowString += "<a href='#' onclick='showEmpEmailLikeModal(\""+obj._id+"\")'><img src='./images/likeCounts.png' alt='Smiley face' height='20' width='20' title='จำนวนผู้ตอบคำถาม' styel='vertical-align: bottom;'>  "+obj.countLike+"</a>";
			}
			rowString += "</td>";
			
			rowString += "<td style='text-align:center; width:33%;'>";
			if(obj.countAcceptPoll != null) {
				rowString += "<a href='#' onclick='showAcceptPollPopup(\""+obj._id+"\")'><img src='./images/pollCount.png' alt='Smiley face' height='20' width='20' title='จำนวนผู้ตอบคำถาม' styel='vertical-align: bottom;'>  "+obj.countAcceptPoll+"</a>";
			}
			rowString += "</td>";
			
			rowString += "</tr>";
			rowString += "</table>";
			rowString += "</td>";
			rowString += "</tr>"
			$("#topicTable > tbody").append(rowString);
			startIndex += 1;
		});
	}
	
	function showAcceptPollPopup(id){
		$("#reportPollTable > tbody").empty();
		$("#acceptEmailTable > tbody").empty();
		$('#pollUserModal').modal('show');
		$.ajax({
			url: '/GO10WebService/GetPollReportServlet',
            type: 'GET',
            data: {"topicId": id},
            contentType: "application/json",
            error: function() {
            	alert("Error");
            },
            success: function(data, textStatus, jqXHR) {
            	insertPollUserTable(data);
            }
       	});
	}
	
	function insertPollUserTable(obj){
		var questionReport = obj.questionReport;
		var countAcceptPoll = obj.countAcceptPoll;
		var rowString = "";
		$.each(questionReport, function(index, data){
			var choiceMaster = data.choiceMaster;
			rowString += "<tr>";
			rowString += "<td><b>"+(index+1)+". "+data.questionTitle+"</b></td>";
			rowString += "<td style='text-align:center;'><b>"+countAcceptPoll+"</></td>";
			rowString += "<td style='text-align:center;'><b>100</b></td>";
			rowString += "</tr>"
			$.each(choiceMaster, function(ind, data){
				rowString += "<tr>";
				rowString += "<td style='text-indent: 40px;'>"+(index+1)+"."+(ind+1)+" "+data.choiceTitle+"</td>";
				rowString += "<td style='text-align: center;'>"+data.countChoice+"</td>";
				rowString += "<td style='text-align: center;'>"+Math.round((data.countChoice/countAcceptPoll)*100)+"</td>";
				rowString += "</tr>"
			});
		});
		$("#reportPollTable > tbody").append(rowString);
		
		var empEmailAcceptPoll = obj.empEmailAcceptPoll;
		var rowStr = "";
		$.each(empEmailAcceptPoll, function(index, data){
			rowStr += "<tr>";
			rowStr += "<td style='text-align: center'>"+(index+1)+"</td>";
			rowStr += "<td>"+data+"</td>";
			rowStr += "</tr>";
		});
		$("#acceptEmailTable > tbody").append(rowStr);
	}
	
	function showEmpEmailReadModal(id){
		$("#readListUserTable > tbody").empty();
		$('#readListUserModal').modal('show');
		$.ajax({
			url: '/GO10WebService/GetReadUserServlet',
            type: 'GET',
            data: {"topicId": id},
            contentType: "application/json",
            error: function() {
            	alert("Error");
            },
            success: function(data, textStatus, jqXHR) {
            	insertDataToReadListTable(data);
            }
       	});
	}
	
	function insertDataToReadListTable(readList){
		var rowString = "";
		$.each(readList, function(index, data){
			rowString += "<tr>";
			rowString += "<td>"+data.empEmail+"</td>";
			rowString += "<td style='text-align:center'>"+data.date+"</td>";
			rowString += "</tr>"
		});
		$("#readListUserTable > tbody").append(rowString);
	}
	
	function showEmpEmailLikeModal(id){
		$("#likeListUserTable > tbody").empty();
		$('#likeListUserModal').modal('show');
			$.ajax({
			url: '/GO10WebService/GetLikeUserServlet',
            type: 'GET',
            data: {"topicId": id},
            contentType: "application/json",
            error: function() {
            	alert("Error");
            },
            success: function(data, textStatus, jqXHR) {
				insertDataToLikeListTable(data);
            }
       	});
	}
	
	function insertDataToLikeListTable(likeList){
		var rowString = "";
		$.each(likeList, function(index, data){
			rowString += "<tr>";
			rowString += "<td>"+data.empEmail+"</td>";
			rowString += "<td style='text-align:center'>"+data.date+"</td>";
			rowString += "</tr>"
		});
		$("#likeListUserTable > tbody").append(rowString);
	}
	
	function openModal(ele){
		var rowCount = $('#pinTable tbody tr').length;
		if(rowCount < 5){
			elePin = ele.closest('tr');
			$('#pinTopicModal').modal('show');
		} else {
			elePin = false;
			alert("Cannot pin more than 5 topics!!!");
		}
	}
	
	function insertPinTable(topicList){
		$("#pinTable > tbody").html("");
		$.each(topicList, function(index, topicModel){
			var rowString = createPinTableRowString(topicModel, index);
			$("#pinTable > tbody").append(rowString);
		});
	}
	
	function showTotalRowsAndSetPaging(length){
		$("#totalRows").text("Total Rows : "+length);
		
		$("#paging").html("");
		var totalPage = Math.ceil(length/30);
		if (totalPage > 1) {
			var pageHtml = "";
			if(currentPage > 1 && currentPage < totalPage) {
				pageHtml += "<div class='paging' onclick='prevPage()' style='display: inline;'>Back</div>";
				pageHtml += "<div class='paging' onclick='nextPage()' style='display: inline;'>Next</div>";
			} else if(currentPage == totalPage){
				pageHtml += "<div class='paging' onclick='prevPage()' style='display: inline;'>Back</div>";
			} else if(currentPage == 1){
				pageHtml += "<div class='paging' onclick='nextPage()' style='display: inline;'>Next</div>";
			}
			$("#paging").append(pageHtml);
		}
	}
	
	function closeModal(){
		$('#pinTopicModal').modal('toggle');
		$("#pinTable > tbody").empty();
		insertPinTable(pinTopicList);
	}
	
	function savePinList(){
		var obj = getData();
		$('#pinTopicModal').modal('hide');
		$.ajax({
			url: '/GO10WebService/UpdatePinServlet',
            type: 'POST',
            data: JSON.stringify(obj),
            contentType: "application/json",
            error: function() {
            	alert("Error");
            },
            success: function(res, textStatus, jqXHR) {
				if(201 == jqXHR.status){
					clearAllTable();
					bookmarkList = [];
		 			currentPage = 1;
					initialData();
				}
            }
       	});
	}
	
	function getData(){
		var obj = new Object();
		obj.unsavePinList = getPinFromTable();
		obj.deletePinList = deletePinList;
		return obj;
	}
	
	function getPinFromTable(){
		var resultList = [];
		$('#pinTable tbody tr').each(function(index, tr) {
		  	var tds = $('td', tr).map(function(index, td) {
		  	 	return $(td).text();
		    });
		    var object = {
		    	_id : tds[0],
		    	pin : index
		    }
		    resultList.push(object);
		});
		
		return resultList;
	}
	
	function createPinTableRowString(topicModel, index){
		var rowString = "<tr>";
		  	rowString += "<td style='display: none;'>"+topicModel._id+"</td>";
		  	rowString += "<td style='text-align:center'>"
				+"<img src='images/pin-"+(index+1)+".png' class='icon-table'>"
				+"</td>"
			rowString += "<td>"+topicModel.subject+"</td>";
			rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/up.png' width='25px' height='25px' onclick='rowUp(this)'/>"
				+"<input type='image' src='images/down.png' width='25px' height='25px' onclick='rowDown(this)'/>"
				+"</td>";
			rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/remove.png' class='icon-table' onclick='removeRow(this, \""+topicModel._id+"\")'/>"
				+"</td>"
			rowString += "</tr>"
		return rowString;
	}
	
	function removeRow(ele, _id){
	    var object = {
	    	_id : _id
	    }
	    deletePinList.push(object);
	    $(ele).closest('tr').remove();
	    rearangePinNumber();
	}
	
	function rearangePinNumber(){
		$('#pinTable tbody tr').each(function(index, tr) {
			var firstTd =$(this).children('td').slice(1, 2);
			firstTd.html("<img src='images/pin-"+(index+1)+".png' class='icon-table'>");
		});
	}
	
	function rowUp(ele){
		$(ele).closest('tr').prev().before($(ele).closest('tr'));
		rearangePinNumber();
	}
	
	function rowDown(ele){
		$(ele).closest('tr').next().after($(ele).closest('tr'));
		rearangePinNumber();
	}
	
	function nextPage(){
		currentPage += 1;
		reloadData();
	}
	
	function prevPage(){
		currentPage -= 1;
		reloadData();
	}
	
	function reloadData(){
		$.ajax({
            url: '/GO10WebService/GetToppicManagementServlet',
            type: 'GET',
            contentType: "application/json",
            data: {bookmark: bookmarkList[currentPage]},
            error: function() {
                alert("initialData error");
            },
            success: function(data, textStatus, jqXHR) {
				if(data.pinTopicList.length != 0){
					pinTopicList = data.pinTopicList;
				}
				nopinTopicList = data.noPinTopicList;
				showTotalRowsAndSetPaging(data.totalRows);
				addBookmarkToObject(currentPage+1, data.bookmark);
				insertDataToTable(data.pinTopicList, nopinTopicList);
				insertPinTable(pinTopicList);
            }
       	});
	}
	
	function getModel(ele){
		var tds = $('td', ele).map(function(index, td) {
	  	 	return $(td).text();
	    });
	    var object = {
	    	_id : tds[0],
	    	subject : tds[3]
	    }
	    return object;
	}
	
	function changeContentModal(mode){
		if(mode == 'acceptList') {
			$('#pollUserModalMenu').html("Accept User <span class='caret'></span>");
// 			<span class='caret'></span>
			$('#acceptEmailTable').show();
			$('#reportPollTable').hide();
		} else if(mode == 'report') {
			$('#pollUserModalMenu').html("Answer Report <span class='caret'></span>");
			$('#acceptEmailTable').hide();
			$('#reportPollTable').show();
		}
	}
	
</script>

	<div class="modal-loading"></div>
	<div class="container">
		<h3>Topic Management</h3>
		<br>
		<div class="col-md-12">
			<div class="row">
				<div id="totalRows" class="col-md-2" style="text-align: left;"></div>
				<div id="paging" class="col-md-10" style="text-align: right;"></div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<table id="topicTable" class="table table-striped table-responsive" style="width: 100%;">
						<thead>
							<tr>
								<th style="display: none;">_id</th>
								<th style="width: 5%; text-align: center;">No.</th>
								<th style="width: 15%; text-align: center;">Create Account</th>
								<th style="width: 25%; text-align: left;">Topic</th>
								<th style="width: 15%; text-align: center;">Create Date</th>
								<th style="width: 7%; text-align: center;">Pin</th>
								<th style="width: 15%; text-align: center;"></th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="pinTopicModal" tabindex="-1" role="dialog" aria-labelledby="pinTopicModalLabel">
	  	<div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="pinTopicModalLabel">Pin Management</h4>
		      </div>
		      <div class="modal-body">
		        <table id="pinTable" class="table table-striped table-responsive" style="width: 100%;">
					<thead>
						<tr>
							<th style="display: none;">_id</th>
							<th style="width: 15%; text-align: center;">Pin</th>
							<th style="width: 60%; text-align: left;">Topic</th>
							<th style="twidth: 15%"></th>
							<th style="width: 10%"></th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		        <button type="button" class="btn btn-primary" onclick="savePinList()">Save changes</button>
		      </div>
		    </div>
  		</div>
	</div>
	
	<div class="modal fade" id="readListUserModal" tabindex="-1" role="dialog" aria-labelledby="readListUserLabel">
	  	<div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="readListUserLabel">Read User</h4>
		      </div>
		      <div class="modal-body">
		        <table id="readListUserTable" class="table table-striped table-responsive" style="width: 100%;">
					<thead>
						<tr>
							<th style="width: 60%;">Email</th>
							<th style="width: 40%; text-align: center;">Read Time</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		      </div>
		    </div>
  		</div>
	</div>
	
	<div class="modal fade" id="likeListUserModal" tabindex="-1" role="dialog" aria-labelledby="likeListUserLabel">
	  	<div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="likeListUserLabel">Liked User</h4>
		      </div>
		      <div class="modal-body">
		        <table id="likeListUserTable" class="table table-striped table-responsive" style="width: 100%;">
					<thead>
						<tr>
							<th style="display: none;">_id</th>
							<th style="width: 60%;">Email</th>
							<th style="width: 40%; text-align: center;">Like Time</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		      </div>
		    </div>
  		</div>
	</div>
	
	<div class="modal fade" id="pollUserModal" tabindex="-1" role="dialog" aria-labelledby="pollUserLabel">
	  	<div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <!-- <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> -->
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<div class="dropdown">
				  <button class="btn btn-default btn-lg dropdown-toggle no-boarder" type="button" id="pollUserModalMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
				    Answer Report
				    <span class="caret"></span>
				  </button>
				  <ul class="dropdown-menu" aria-labelledby="pollUserModalMenu">
				  	<li><a href="#" onclick="changeContentModal('report');">Answer Report</a></li>
				    <li><a href="#" onclick="changeContentModal('acceptList');">Accept User</a></li>
				  </ul>
				</div>
		      </div>
		      <div class="modal-body">
		        <table id="reportPollTable" class="table table-striped table-responsive" style="width: 100%;">
					<thead>
						<tr>
							<th style="width: 60%;">Question/Answer</th>
							<th style="width: 20%; text-align: center;">Number of Answer User</th>
							<th style="width: 20%; text-align: center;">Percent (%)</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
				<table id="acceptEmailTable" class="table table-striped table-responsive" style="width: 100%; display: none;">
					<thead>
						<tr>
							<th style="width: 20%; text-align: center;">No.</th>
							<th style="width: 80%;">Email</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		      </div>
		    </div>
  		</div>
	</div>
	
	<script type="text/javascript">
		$('#pinTopicModal').on('show.bs.modal', function (event) {
		  if(elePin != null) {
		  	var modal = $(this)
		  	var topicModel = getModel(elePin);
		  	var rowCount = $('#pinTable tbody tr').length;
		  	var rowString = createPinTableRowString(topicModel, rowCount);
 			$("#pinTable > tbody").append(rowString);
		  }
		});
		
		$('#pinTopicModal').on('hidden.bs.modal', function (event) {
			$("#pinTable > tbody").empty();
			insertPinTable(pinTopicList);
			deletePinList = [];
			elePin = null;
		});
		
	</script>
</body>
</html>