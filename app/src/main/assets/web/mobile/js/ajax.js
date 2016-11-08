var component = component||{};
component.ajax = new function() {
	var parent = this;
	var showProgress = function() {
		var progress = $("#progressBar");
		progress.show();
	}
	var showError = function(data) {
		closeProgress();
		var msg =data.responseText;
		//openPopup(null,msg,'html');
		alert('读取失败!');
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