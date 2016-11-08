// JavaScript Document
function reset(basePath,type){
	if(type =='1'){//修改个人基本信息
		window.location.href=basePath+"user/changeInfo!changeInfo.action";
	}else if(type =='2'){//修改密码
		window.location.href=basePath+"user/user_reset_password.jsp";
	}else if(type =='3'){//修改密码查询问题
		window.location.href=basePath+"user/changeInfo!changeQuestion.action";
	}
	else if(type =='4'){//修改密码查询邮箱
		window.location.href=basePath+"user/changeInfo!findPwdEmail.action";
	}
}