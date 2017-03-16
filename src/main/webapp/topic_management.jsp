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
	var indexPin = null;
	
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
			rowString += "<td style='text-align:center'>"+obj.avatarName+"</td>";
			rowString += "<td>"+obj.subject+"</td>";
			rowString += "<td style='text-align:center'>"+obj.date+"</td>";
			if (obj.pin != null) {
				rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/pin-"+(index+1)+".png' class='icon-table' data-toggle='modal' data-target='.bs-example-modal-lg'/>"
// 				+"<input type='image' src='images/delete.png' class='icon-table'/>"
				+"</td>";
			} else {
				rowString += "<td style='text-align:center'>"
				+"<input type='image' src='images/pin.png' class='icon-table' onclick='openModal("+index+")' />"
// 				+"<input type='image' src='images/delete.png' class='icon-table'/>"
				+"</td>";
			}
			rowString += "</tr>"
			$("#topicTable > tbody").append(rowString);
			startIndex += 1;
		});
	}
	
	function openModal(index){
		var rowCount = $('#pinTable tbody tr').length;
		if(rowCount < 5){
			indexPin = index;
			$('#pinTopicModal').modal('show');
		} else {
			indexPin = false;
			alert("Cannot pin more than 5 topic!!!");
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
	
</script>

	<div class="modal-loading"></div>
	<div class="container">
		<h3>Topic Management</h3>
		<br>
		<div class="col-md-offset-1 col-md-10">
			<div class="row">
				<div id="totalRows" class="col-md-2" style="text-align: left;"></div>
				<div id="paging" class="col-md-10" style="text-align: right;">
<!-- 					<div class="paging" onclick="gotoPage(1)" style="display: inline;">1</div> -->
<!-- 					<div class="paging" onclick="gotoPage(2)" style="display: inline;">2</div> -->
<!-- 					<div class="paging" onclick="gotoPage(3)" style="display: inline;">3</div> -->
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<table id="topicTable" class="table table-striped table-responsive" style="width: 100%;">
						<thead>
							<tr>
								<th style="display: none;">_id</th>
								<th style="width: 5%; text-align: center;">No.</th>
								<th style="width: 25%; text-align: center;">Create Avatar</th>
								<th style="width: 40%; text-align: left;">Topic</th>
								<th style="width: 20%; text-align: center;">Create Date</th>
								<th style="width: 15%; text-align: right;"></th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade bs-example-modal-lg" id="pinTopicModal" tabindex="-1" role="dialog" aria-labelledby="pinTopicModalLabel">
	  	<div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">Pin Management</h4>
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

	<script type="text/javascript">
		$('#pinTopicModal').on('show.bs.modal', function (event) {
		  if(indexPin != null) {
		  	var modal = $(this)
		  	var topicModel = nopinTopicList[indexPin];
		  	var rowCount = $('#pinTable tbody tr').length;
		  	var rowString = createPinTableRowString(topicModel, rowCount);
 			$("#pinTable > tbody").append(rowString);
		  }
		});
		
		$('#pinTopicModal').on('hidden.bs.modal', function (event) {
			$("#pinTable > tbody").empty();
			insertPinTable(pinTopicList);
			deletePinList = [];
		});
	</script>
</body>
</html>