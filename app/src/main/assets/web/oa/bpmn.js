/**
 * 转发任务
 * @param taskId
 */
function forwardTask(obj){
	popUserChoose(obj,"saveForwardTask");
}

function saveForwardTask(users, target){
	var taskId = $(target).attr("taskId");
	var arr=[]
	for ( var key in users) {
		arr.push(key);
	}
	var username=arr[0];
	var url="bpmn/task/forward?taskId="+taskId+"&username="+username;
	var param = {
			title : '执行结果',
			close :function(){
				window.history.go(-1);
			}
		};
	component.dialog.popDialog(url, param, "url", target);
}