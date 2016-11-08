// JavaScript Document
/*点击邮件列表*/
function submit_mail(){
	alert("提交表单");
}
/*选择框全选*/
function sel_all_mail(){
	var check = $("#select_all_mail").attr("checked");
		if(check=='checked')
			sel_all();
		else 
			sel_no_all();
}
/*点击全选时选中全部邮件*/
function sel_all(){
	$(":checkbox").attr("checked", true);
}
/*全部不选*/
function sel_no_all(){
	$(":checkbox").attr("checked", false);
}
/*选中全部未读的邮件*/
function sel_all_unread(){
	$("input[read='unread']").attr("checked", true);
	$("input[read='read']").attr("checked", false);
	$("#select_all_mail").attr("checked", false);
}
/*选中全部已读的邮件*/
function sel_all_read(){
	$("input[read='read']").attr("checked", true);
	$("input[read='unread']").attr("checked", false);
	$("#select_all_mail").attr("checked", false);
}

/*反选*/
function sel_reversal(){
	$(":checkbox").each(function(i){
		if(this.checked==true)
			this.checked=false;
		else 
			this.checked=true;
	});
	$("#select_all_mail").attr("checked", false);
}
var path ="";
var folderType="";
var page;
function setInfo(path2,folderType2,page2){
	path = path2;
	folderType=folderType2;
	page = page2;
}

//邮件是否已选择
function optation(urlString){
	var obj = $("#dataTable input:checked");
	var size = obj.size();
	var strId;
	 if( (size==1 && $(obj[0]).val()=="on") || size == 0){
		alert("请选择邮件！");
		$("#move option:first-child").attr("selected", true);
		$("#mark option:first-child").attr("selected", true);
	} else {
		var strId = "";
		obj.each(function(i) {
		if( $(this).val()!="on")
			strId += $(this).attr("value") + ",";
		});
		strId = strId.substr(0, strId.length - 1);
		window.location.href=urlString+"&mailUid="+strId;
	}
}
/* 标记为 */
function mark() {
	var type = $("#mark option:selected").attr("value");
	if (type != 'none') {
		optation(path+"mail/mark?folderType="+folderType+"&type="+type+"&page="+page);
	}
}

/* 移动到 */
function move() {
	var type = $("#move option:selected").attr("value");
	if (type != 'none') {
		optation(path+"mail/move?folderType="+folderType+"&type="+type+"&page="+page);
	}
}

/* 查看 */
function see() {
	var type = $("#see option:selected").attr("value");
	if (type != 'none') {
		if(type == 'seeAll')
			window.location.href=path+"mail/see?folderType="+folderType;
		else{
		window.location.href=path+"mail/see?folderType="+folderType+"&type="+type+"&page=1";
		}
	}
}

/*删除邮件*/
function deleteMail(){
	optation(path+"mail/delete?folderType="+folderType+"&page="+page);
}
/*彻底删除邮件*/
function deleteMailForver(){
	optation(path+"mail/deleteforever?folderType="+folderType+"&page="+page);
}

/*查看邮件*/
function readMail(uid){
	window.location.href=path+"mail/read?folderType="+folderType+"&mailUid="+uid+"&page="+page;
}

/*删除指定uid的邮件*/
function dMail(uid){
	window.location.href = path+"mail/delete?folderType="+folderType+"&mailUid="+uid+"&page="+page;
}
/*彻底删除指定uid的邮件*/
function dMailForver(uid){
	var truthBeTold = window.confirm("你确定要删除该邮件吗？");
	if (truthBeTold) {
		window.location.href = path+"mail/deleteforever?folderType="+folderType+"&mailUid="+uid+"&page="+page;
	} 
}
/* 移动指定邮件到指定的收件夹中 */
function mMail(uid) {
	var type = $("#move option:selected").attr("value");
	if (type != 'none') {
		window.location.href =path+"mail/move?folderType="+folderType+"&type="+type+"&page="+page+"&mailUid="+uid;
	}
}
 
/*快速回复*/
function quecklyReply(){
	var content = $("[name='content']").val();
	if(content==''){
		alert("请输入回复信息！");
	}else{
		$("#reply_form").submit();
	}
}
/*回复邮件*/
function replyMail(uid){
	window.location.href =path+"mail/#####?uid="+uid+"&folderType="+folderType;
}

/* 搜索结果标记为 */
function searchMark(content) {
	var type = $("#mark option:selected").attr("value");
	if (type != 'none') {
		optation(path+"####!markMail.action?folderType="+folderType+"&type="+type+"&page="+page+"&searchMailFB.content="+content);
	}
}

function forwardMail(uid){
	window.location.href=path+"mail/read?folderType="+folderType+"&mailUid="+uid+"&page="+page+"&type=0";
}