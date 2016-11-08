component.ajax = new function() {
	var parent = this;
	var showProgress = function() {
		var progress = $("#progressBar");
		if (!progress[0]) {
			progress = jQuery('<div style="display:none;position: absolute;bottom: 0;left:0;text-align: center;width: 100%;height:100%;background:#FFF; opacity:0.7; filter: alpha(opacity=70); "  id="progressBar" class="progressBar">'
					+ '<table style="width:100%;height:100%" border=0><tr><td><img  src="res/static/plugin/LigerUI/loading.gif"/></td></tr></table></div>');
			$("body").append(progress);
		}
		progress.show();
	}
	var showError = function(data) {
		closeProgress();
		var param = {
			title : "错误",
			width : "450",
			height : "190"
		};
		component.dialog.popDialog(data.responseText, param, "html",this);
	};
	var closeProgress = function() {
		var progress = $("#progressBar");
		progress.hide();
	}
	

	this.doGet = function(param) {
		showProgress();
		$.ajax({
			url : param.url,
			type : "GET",
			success : function(data) {
				closeProgress();
				param.callback(data);
			},
			error : showError
		});
	}

	this.submit = function(param) {
		showProgress();
		$(param.form).ajaxSubmit({
			success : function(data) {
				closeProgress();
				if(param.callback){
					param.callback(data);
				}
			},
			error : showError
		});
	};

	this.doPost = function(param) {
		$("body").append(parent.progress);
		$.ajax({
			url : param.url,
			data : param.data,
			type : "POST",
			success : function(data) {
				closeProgress();
				param.callback(data);
			},
			error : showError
		});
	};

}
$.ajaxSetup({cache:false,error:component.ajax.showError});