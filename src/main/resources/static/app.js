var stompClient = null;

function connect() {
    var socket = new SockJS('/bridge');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        
        stompClient.subscribe('/topic/init', function (data) {
            getInitData(data.body);
        });

 		stompClient.send("/app/init", {}, "GetFullList");


       	setTimeout(function(){
			stompClient.subscribe('/topic/share', function (data) {
            	getBroadCastData(data.body);
        	});

			stompClient.subscribe('/topic/deep', function (data) {
            	getBroadCastData(data.body);
        	});
			
			stompClient.subscribe('/topic/score', function (data) {
            	getBroadCastData(data.body);
        	});

		},5000);
		
    });
    
    
}

function getInitData(message) {
	var struct=message.split("<>");
	for(var i=0;i<struct.length-1;i=i+2){
		var type=struct[i];
		var item=struct[i+1];
		$('#'+type).append(item);
	}

}

function getBroadCastData(message) {
	var struct=message.split("<>");
	var type=struct[0];
	var id=struct[1];
	var event=struct[2];
	var item=struct[3];
	
	if (type=="item" && event=="SHARE"){
		UpdatedStatusAction_Share(item,id);
	}
	else if (type=="item" && event=="DEEP"){
		UpdatedStatusAction_Deep(item,id);
	}
	else if (type=="item" && event=="SCORE"){
		UpdatedStatusAction_Score(item,id,false);
	}
	else if (type=="items" && event=="SCORE"){
		UpdatedStatusAction_Score(item,"",true);
	}
}

function getOwnerData(message) {
	//If Table Close Return//
	if($('#Owner').prop("gstatus")!="true")
		return;
	

	
	var struct=message.split("<>");
	var elem=struct[1];
	$('#Owner > tbody > tr:not(:first)').remove();
	
	//Owner Data Fetched So All Of These Should Inserted
	$('#Owner > tbody > tr:first').after(elem);
}

function UpdatedStatusAction_Share(item,id){
	
	//If Table Close Return//
	if($('#Share').prop("gstatus")!="true")
		return;
	
	var element=$('#share'+id);
	
	// 1 Sec For Updated Share //
	if(element.length){
	
		element.html(item);

		$('#Share > tbody > tr:first').delay(1500).after(element);

	}
	else{
		
		element="<tr id=share"+id+">"+item+"</tr>";
		
		element=$('#Share > tbody > tr:first').delay(1500).append(element);
	}
}

function UpdatedStatusAction_Score(item,id,group){
	
	//If Table Close Return//
	if($('#Score').prop("gstatus")!="true")
		return;
	
	if(group==true){
		$('#Score > tbody > tr:not(:first)').remove();
		$('#Score > tbody > tr:first').after(item);
	}
	else{
		//If Table Close Return//
		var element=$('#score'+id);
	
		// 1 Sec For Updated Share //
		if(element.length){
			element.html(item);
		}
		else{
			element="<tr id=\"score"+id+"\">"+item+"</tr>";
			element=$('#Score > tbody').append(element);
		}
	}
}

function UpdatedStatusAction_Deep(item,id){
	//If Table Close Return//
	if($('#Deep').prop("gstatus")!="true")
		return;
	
	var element="<tr>"+item+"</tr>";
	
	// 2 Sec For Updated Deep //
	$('#Deep > tbody > tr:first').delay(1500).after(element);
	
	var sizerow=$('#Deep').find('tr').length;
	
	if(sizerow >= 500)
		$('#Deep tr:last').remove();
}

/* Page Event */
function ScoreMin(){
	stompClient.send("/app/score", {}, "GetFullList");
}

function OwnerRefresh(){
	var timer=30;
	var seconds=parseInt(timer,10);
	seconds=seconds*1000;
	
	stompClient.subscribe('/topic/owners', function (data) {
            getOwnerData(data.body);
        	});

	//Call Once//
	stompClient.send("/app/owners", {}, "GetFullList");
	
	//Continue In Interval//
	setInterval(function(){
			stompClient.send("/app/owners", {}, "GetFullList");
		},seconds);
}

function tablestatus(item){
	if($('#'+item).prop("gstatus")=="true"){
		$('#status'+item).prop("value",item+'(Off)');
		$('#'+item).prop("gstatus","false");
	}
	else{
		$('#'+item).prop("gstatus","true");
		$('#status'+item).prop("value",item+'(On)');
	}
}

$(function() {  
  // Initial Setup //
  //First Initial Of Canvas//
  $(document).ready(function(){
	//Page Ready//
	$('#Score').prop("gstatus","true");
	$('#Share').prop("gstatus","true");
	$('#Deep').prop("gstatus","true");
	$('#Owner').prop("gstatus","true");
	
	connect();
  })
  
});