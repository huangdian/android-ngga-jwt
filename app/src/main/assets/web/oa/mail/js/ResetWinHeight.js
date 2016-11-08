/**
 * 从新设置窗口的高度
 */
//设置左边工具条高度
var h =0;
$(function($) {
	h= $("#left").height();
	$("#input").focusout(function() {
		var value = $("#input").attr("value");
		if (value == "") {
			$("#input").attr("value", "搜索邮件...");
			$("#input").removeClass("input_focse");
		}
	});
	//list_focse
	$("#list li").click(function() {
		$("#list li").removeClass("list_focse");
		$(this).addClass("list_focse");
	});
});
function SetLeftHeight(){
	var height= $("#content").height();
	if(height>h){
		$("#left").height(height);
	}else{
		$("#left").height(h);
	}
	window.setTimeout("SetWinHeight()", 300); 
}
//iframe自动适应网页的高度
function SetWinHeight()
{
	var win= document.getElementById("rightcontent");
	if (document.getElementById)
	{
		if (win && !window.opera)
		{
			if (win.contentDocument && win.contentDocument.body.offsetHeight) 
   				win.height = win.contentDocument.body.offsetHeight; 
   			else if(win.Document && win.Document.body.scrollHeight)
  				win.height = win.Document.body.scrollHeight;
		}
	}
	SetLeftHeight();
}
function clearValue(){
	var value = $("#input").attr("value");
	if (value == "搜索邮件...") {
		$("#input").attr("value", "");
		$("#input").addClass("input_focse");
	}
}

//搜索邮件判读输入值
function checkSubmit(){
	var value = $("#input").attr("value");
	if (value != "搜索邮件..." && value != "") {
		$("#searchMail").submit();
	}else{
		alert("请输入搜索内容！");
	}
}