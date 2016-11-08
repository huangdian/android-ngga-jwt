$(document).ready(function() {
	var form = $("form");
	if (form) {
		form.validationEngine({
			promptPosition : "topRight"
		});
	}

	// 初始化导航
	$("#nav ul li").bind("mouseover", function() {
		$("ul.sub-nav", this).show();
	});
	$("#nav ul li").bind("mouseout", function() {
		$("ul.sub-nav", this).hide();
	});

	// 初始化通知
	readMsgCount();// 页面刷新一次读取一次
	//setInterval("readMsgCount()", 5000);
	$(window).resize(adjustSize);
	adjustSize();
});

function readMsgCount() {
	var url = "/message/count"
	$.get(url, function(count) {
		if (count > 0) {
			if(count>=100){count="..."};
			$("#util .hint").text(count).show();
		} else {
			$("#util .hint").text("").hide()
		}
	});
}
function submitForm(target) {
	var form = $(target).parents('form');
	form.submit();
	return false;
}
function ajaxSubmit(target) {
	var form = $(target).parents('form');
	if (!form.validationEngine('validate')) {
		return false;
	}
	var callback = eval($(target).attr("callback"));
	if (!callback) {
		callback = function(data) {
			window.location.reload()
		}
	}
	var param = {
		form : form,
		callback : callback
	}
	component.ajax.submit(param);
}
function ajaxDo(target) {
	var ajaxUrl = $(target).attr("url");
	var callback = eval($(target).attr("callback"));
	if (!callback) {
		callback = function(data) {
			// alert("操作成功!");
			window.location.reload();
		}
	}
	var param = {
		url : ajaxUrl,
		callback : callback
	}
	component.ajax.doGet(param);
}
function openNewTab(e) {
	var event = $.event.fix(e);
	var text = $(event.target).attr('alt');
	if (!text || text == '') {
		text = $(event.target).text();
	}
	var url = $(event.target).attr('url');
	var topFrame = $(window.top.document).contents().find("#top")[0].contentWindow;
	topFrame.addTab(url, text);
}

function gotoPage(id, page) {
	var pageBar = "#" + id + " #ajax_pager";
	var url = $(pageBar).attr('url');
	$("#" + id).load(url, {
		"page" : page
	});
}

function popUserSelect(obj) {
	var url = "oauser/pop-select?type=checkbox";
	if (arguments[1]  && typeof(arguments[1])=="string") {
		var callback = arguments[1];
		url = url + '&callback=' + callback
	}
	var param = {
		height : 520,
		width : 800,
		title : '人员选择'
	};
	var popPage = component.dialog.popDialog(url, param, "url", obj);
}

function popUserChoose(obj) {
	var url = "oauser/pop-select?type=radio";
	if (arguments[1] && typeof(arguments[1])=="string") {
		var callback = arguments[1];
		url = url + '&callback=' + callback
	}
	var param = {
		height : 520,
		width : 800,
		title : '人员选择'
	};
	var popPage = component.dialog.popDialog(url, param, "url", obj);
}
/**
 * 设置用户选择页面中的值
 */
function getSelectedUser(target) {
	var users;
	if (target) {
		var selDiv = $(target).parent("#user_select");

		var fieldName = $(target).attr('field');
		if (!fieldName) {
			fieldName = 'users';
		}
		users = $("input[name=" + fieldName + "]", selDiv);
	} else {
		users = $("input[name='users']");
	}

	var selectedArr = {};
	$.each(users, function(i, el) {
		var val = $(el).val();
		var text = $(el).attr('text');
		selectedArr[val] = text;
	});
	return selectedArr;
}

/**
 * 用户选择页面回调值
 * 
 * @param {Object}
 *            data
 */
function setSelectedUser(users, target) {
	var selector = $(target).parent("#user_select");
	if (users) {
		$(selector).find(".user_box").remove();
	}

	var fieldName = $(target).attr('field');
	if (!fieldName) {
		fieldName = 'users';
	}

	for ( var key in users) {
		var div = $("<div class='user_box'/>");
		div.bind('dblclick', function() {
			$(this).remove();
		});
		var span = $("<span/>");
		var text = users[key];
		span.text(text);
		div.append(span);
		var hide = $("<input type='hidden'/>");
		hide.attr('name', fieldName);
		hide.val(key);
		hide.attr('text', text);
		div.append(hide);
		var boxes = selector.find(".user_add");
		div.insertBefore(boxes);
	}
	$(selector).sortable();
	$(selector).disableSelection();
}

/**
 * 站内信息通知
 */
function showMsgBox(event) {
	var url = "/oamsg/list";
	var param = {
		title : "待办事项",
		width : 700,
		height : 300,
		close:readMsgCount
	}
	component.dialog.popDialog(url, param, "url", event);
}

function showEmailReadState(event,mid) {
	var url = "/mail/emailreadsts?mid="+mid;
	var param = {
		title : "邮件状态",
		width : 600,
		height : 300,
		close:readMsgCount
	}
	component.dialog.popDialog(url, param, "url", event);
}


//function adjustSize(){
//	var initWidth=1240;
//	var screenWidth=window.screen.width;
//	var scale = (screenWidth/initWidth).toFixed(2);
//	var userAgent = navigator.userAgent;
//	if(scale>1&&userAgent.indexOf("MSIE")==-1){
//		document.body.style.zoom=scale;
//	}
//}
function adjustSize(){
	var initWidth=1440;
	var screenWidth=window.screen.width;
	var scale = (screenWidth/initWidth).toFixed(2);
	if(scale>1){
		var view = $("#viewport");
		//计算居中
		$(view).css({"zoom":scale});
		var left = ($(window).width() - view.outerWidth()*scale)/2;
		var userAgent = navigator.userAgent;
		if(userAgent.indexOf("MSIE")!=-1){
			$(view).css({"left":left,"position":"relative"});
		}
	}
}

function deleteConfirm(){
	if(confirm("即将删除，是否继续执行？")){
		return true;
	}
	return false;
}

function showProgress() {
	$("#progressBar").show();
}
function closeProgress(){
	$("#progressBar").hide();
}