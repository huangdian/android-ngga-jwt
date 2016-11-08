var component = component||{};
/*
 * version 20150514 修复component.dialog.popWindow 修复 弹窗覆盖的问题，弹窗中再弹的问题 version
 * 20150507 修复弹窗问题 修复错误提示的问题 修复ff下事件对象的问题
 * 20160731 修复弹出判断是否弹出的 问题
 */
component.popWindow = function(e) {
	var event = $.event.fix(e)
	event.stopPropagation();
	var obj = $(event.target);
	var text = $(obj).text()|| $(obj).attr('title');
	var width = $(obj).attr('width');
	if(!width){width=400;}
	var height = $(obj).attr('height');
	if(!height){height=230;}
	var onClose = $(obj).attr('close');
	onClose = eval(onClose);
	if (!onClose) {
		onClose = function() {
			window.location.reload()
		}
	}
	var param = {
		title : text,
		width : width,
		height : height,
		close : onClose
	};

	var src = $(obj).attr('url');
	component.dialog.popDialog(src, param, "url", e);
}
component.dialog = {
	win : null,
	popEvent : null,
	stack : []
};
component.dialog.isOpen = function(e) {
	if (this.win == null || (typeof this.win) == "undefined") {
		return false;
	} else if ((typeof this.win) == "object") {
		if(e!=null) {return e == this.popEvent&&this.win.dialog('isOpen');}
		else{return this.win.dialog('isOpen');}
	}
	return false;
};
component.dialog.clear = function(obj) {
	$(obj).parent().find('input[type!="button"]').val('');
};
component.dialog.returnValue = function(return_text, return_value) {
	var contetnFrame = [];
	var e = parent.component.dialog.popEvent;
	var target = $(e.target);
	$(target).attr('value', return_text);

	var valueId = $(target).attr("value_id");

	if (!(return_value instanceof Array)) {
		return_value = [ return_value ];
	}

	var id_arry = valueId.split(',');
	for (var i = 0; i < id_arry.length; i++) {
		var valueInput = $(target).parent().find("#" + id_arry[i]);
		valueInput.attr('value', return_value[i]);
	}

	parent.component.dialog.closeWindow();
};
component.dialog.closeWindow = function() {
	this.win.dialog('close');
	this.stack.pop();
	var len = this.stack.length;
	if (len > 0) {
		this.win = this.stack[len - 1];
	} else {
		this.win = null;
	}
};

/*
 * 带returnValue的弹窗
 */
component.dialog.popWindow = function(e) {
	var event = $.event.fix(e)
	event.stopPropagation();
	var obj = $(event.target);

	var param = obj.attr('parameter');
	param = jQuery.parseJSON(param);
	var page = param.src;
	var value_id = obj.attr('value_id');
	var text_id = obj.attr('id');
	if (value_id && text_id) {
		page = page + (page.indexOf("?") > 0 ? "&" : "?") + "value_id="
				+ value_id + "&text_id=" + text_id;
	}
	component.dialog.popDialog(page, param, "url", e);
};

component.dialog.popDialog = function(page, param, type, e) {
	// 判断当前页面是否在主页面中，如果不是则弹窗到主页面
	var p = parent;
	var target = self;
	var contetnFrame = $("div[type='window']", p.document);
	while (target != top && contetnFrame.length == 0) {
		target = p;
		p = p.parent;
		contetnFrame = $("div[type='window']", p.document);
	}

	if (target != self) {// target可能是content中的页面 也有可能是top
		target.component.dialog.popDialog(page, param, type, e);
		return;
	}

	if (!arguments[2])
		type = "url";

	if (this.isOpen(e)) {
		return;
	}
	this.popEvent = e;
	this.win = jQuery('<div id="pop_windows"></div>');
	this.stack.push(this.win);// 压入

	var frame = jQuery('<iframe frameborder=0 style="border: 0px;height:100%;width:100%;z-index:-1;"  ></iframe>');
	var progress = jQuery('<div style="position: absolute;top: 0;text-align: center;width: 100%;height:100%;background:#FFF;"  id="progressBar" class="progressBar">'
			+ '<table style="width:100%;height:100%" border=0><tr><td><img  src="res/static/plugin/LigerUI/loading.gif"/></td></tr></table></div>');

	this.win.dialog({
		modal : true,
		autoOpen : false,
		close : function() {
			if (this.win) {
				this.win.dialog("destroy");
				this.popEvent = undefined;
				this.win = undefined;
			}
		},
		//position:{my: "center", at: "center", of: "body"},
		open : function(e, ui) {
			var height = $(e.target).height();
			progress.css({
				'position' : 'absolute ',
				'padding-top' : function() {
					return (height - progress.height()) / 2;
				}
			});
			//修改弹出窗口被遮挡的问题
			$(".ui-dialog").append("<iframe style=\"position: absolute; z-index: -1; width: 100%; height: 100%; top: 0;left:0;scrolling:no;\" frameborder=\"0\"></iframe>");
		}
	});
	this.win.dialog(param);

	if (!($.browser.msie && ($.browser.version == "6.0") && !$.support.style)) {
		frame.hide();
		this.win.append(progress);
	}
	this.win.append(frame);
	this.win.dialog('open');
	if ("url" == type) {
		frame.attr('src', page);
	} else if ("html" == type) {
		var doc = frame.contents();
		d = doc[0];
		d.open();
		d.write(page);
		d.close();
		progress.hide();
		frame.show();
	}

	frame.bind('load', function() {
		progress.hide();
		frame.fadeIn();
		contentHeight = frame.contents().outerHeight();
		contentWidth = frame.contents().outerWidth();
	});
};

/**
 * 修改全局alert为弹窗提示
 */
//window.alert=function(msg){
//	var param={title:"提示"};
//	component.dialog.popDialog(msg, param, "html",this);
//}

component.table = {};
component.table.eidtRow = function(row_id) {
	// 在调用页面中实现
	alert('修改row_id:' + row_id);
};
component.table.deletRow = function(row_id) {
	// 在调用页面中实现
	alert('删除row_id:' + row_id);
};

component.datePicker = function(id) {
	var target = jQuery('#' + id);
	target.datepicker({
		dateFormat : 'yy-mm-dd',
		showAnim : "slide",
		changeMonth : true,
		changeYear : true,
		showWeek : true,
		showButtonPanel : true,
		closeText : 'close',
		yearRange : '1912:2050'
	});
};

/**
 * tree lingerUiTree
 */
component.tree = function(tree) {
	this.onNodeClick = function(event) {
		alert("点击!");
	};

	this.onNodeDelete = function(event) {
		alert("删除!");
	};

	this.onNodeModify = function(event) {
		alert("修改!");
	};
};
