function SetWinHeight(iframeObj ){
	if (document.getElementById){ 
		   if (iframeObj && !window.opera){ 
		    if (iframeObj.contentDocument && iframeObj.contentDocument.body.offsetHeight){ 
		     iframeObj.height = iframeObj.contentDocument.body.offsetHeight; 
		    }else if(document.frames[iframeObj.name].document && document.frames[iframeObj.name].document.body.scrollHeight){ 
		     iframeObj.height = document.frames[iframeObj.name].document.body.scrollHeight; 
		    } 
		   } 
	}
}

function iFrameHeight(id) {   
	var ifm= document.getElementById(id);   
	var subWeb = document.frames ? document.frames[id].document : ifm.contentDocument;   
	if(ifm != null && subWeb != null) {
	   ifm.height = subWeb.body.scrollHeight;
	}   
}
