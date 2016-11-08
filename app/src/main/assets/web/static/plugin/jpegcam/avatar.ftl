<#include "/freemarker/frame.ftl"> 
<@frame title="头像采集">
<link rel="stylesheet" type="text/css" href="plugin/imgareaselect/css/imgareaselect-default.css" />
<script type="text/javascript" src="plugin/imgareaselect/scripts/jquery.imgareaselect.pack.js"></script>
<script type="text/javascript" src="plugin/jpegcam/webcam.js"></script>
<style type="text/css">
#cam{width:320px;padding-left:100px}
#cam form{
	margin-top:120px;
}
#results {
background: #EEE;
border: solid 2px #DDD;
border-radius: 4px;
padding: 0.6em;
width:530px;
}
.container::after {
clear: both;
content: ".";
display: block;
height: 0;
overflow: hidden;
visibility: hidden;
}
div.frame {
background: white;
padding: 0.8em;
border: solid 2px #DDD;
}
#button{
padding-top: 1em;
padding-left: 1em;
}
</style>
    <script language="JavaScript">
    	var imgSelect;
		function updateavatar(){
			parent.showAvatar();
			parent.component.dialog.closeWindow();
		}    	
		function preview(img, selection) {
		    if (!selection.width || !selection.height)
		        return;
		    
		    var scaleX = 140 / selection.width;
		    var scaleY = 175 / selection.height;
		    $('#preview img').css({
		        width: Math.round(scaleX * img.width),
		        height: Math.round(scaleY * img.height),
		        marginLeft: -Math.round(scaleX * selection.x1),
		        marginTop: -Math.round(scaleY * selection.y1)
		    });
		}
		    
		function areaSelect(){
			if(imgSelect){
				imgSelect.cancelSelection();
			};
			imgSelect=$('img#photo').imgAreaSelect(
							{ 	aspectRatio: '4:5',
								handles: true ,
								fadeSpeed: 200, 
								instance:true,
								onSelectChange: preview,
								x1: 90, y1: 50, 
								x2: 230, y2: 225 });
		}
		function take_snapshot() {
			jQuery('#snap_img').html('<h4 style="text-align:center">拍照中...</h4>');
			webcam.snap();
			jQuery("#cam_button").hide();
			jQuery("#results").show();
		}
		function retake_snapshot() {
			//jQuery("#results").hide();
			//jQuery("#cam").show();
			//jQuery("#cam_button").show();
			//webcam.reset();
			this.location.reload();
		}
				
		function my_completion_handler(msg) {
			jQuery("#cam").hide();
			var rst;
			if(msg  instanceof Object) {
				rst=msg;
			} else{
				rst=jQuery.parseJSON(msg);
			}
			if(rst.RESULT){
				//成功
				var image_url=rst.URL+'?'+Math.random();
				jQuery('#snap_img').html(
					'<img id="photo" width="320px" src="' + image_url + '"/>');
				jQuery('#preview img').attr('src',image_url);
				areaSelect();
			}else{
				retake_snapshot();
			}
		}
		
		function save_selection(){
			if(imgSelect){
				var selection=imgSelect.getSelection();
				//x1,y1,x2,y2,width,height;
				var str=jQuery.param(selection);
				var url="photoCap!cutImg?sfid=${RequestParameters['sfid']}&"+str;
				jQuery.get(url,function(data){
					if(data  instanceof Object) {
					} else{
						data=jQuery.parseJSON(data);
					}				
					if(data.RST){
						updateavatar();
					}else{
						alert(data.MSG);
					}
				});
			}else{
				alert('截取失败！');
			};			
		}
		function upload(){
			var filepath=$("input[name='image']").val();
			if(filepath==null){
				alert("请选择上传的图片");
				return;
			}
			var form=jQuery('#uploadForm').ajaxForm();
			form.ajaxSubmit({success:my_completion_handler});				
			jQuery("#cam_button").hide();
			jQuery("#results").show();
		}
	</script>  
	
<div id="main">
   <div id="cam" class="container">
   <#if Parameters["type"]="upload">
	   <form id="uploadForm" action='photoCap!capture?type=upload&sfid=${RequestParameters['sfid']}' method="post"
		 enctype="multipart/form-data" 
		  theme="simple" >
			<@s.file cssClass="inputbox smallbox validate[required]" name="image"/>
			<input type="button" class="btn" value="上传" onclick="javascript:upload();"/>
		</form>
		<div id="cam_button">
     	</div>
	<#else>
	   <script language="JavaScript">
			webcam.set_api_url( 'photoCap!capture?sfid=${RequestParameters['sfid']}' );
			webcam.set_quality(100); // JPEG quality (1 - 100)
			webcam.set_shutter_sound( true ); // play shutter click sound
			webcam.set_swf_url('plugin/jpegcam/webcam.swf');
			webcam.set_shutter_url('plugin/jpegcam/shutter.mp3');
			webcam.set_hook( 'onComplete', 'my_completion_handler' );
			document.write( webcam.get_html(320, 240, 320,240) );
		</script>
	<div id="cam_button">
		<p style="text-align:center">
		<input type=button value="点击这里拍照" class="btn" onClick="take_snapshot()">
        </p>
     </div>
   </#if>
   </div>
 
   <div id="results" class="container" style="display:none">
				<div id="snap_img" style="float:left;"></div>
				 	<div style="float:left;width: 30%;">
					    <div class="frame" 
					      style="margin: 0 1em; width: 140px; height: 175px;">
					      <div id="preview" style=" width: 140px; height: 175px; overflow: hidden;">
					        <img src="" />
					      </div>
					    </div>
					    <div id="button" >
					    	<p align="center">
							<input type=button value="重拍" class="btn" onClick="retake_snapshot()">
							<input type=button value="保存" class="btn" onClick="save_selection()">
							</p>
				   		</div>
				  	</div>
				 <div class="clear"  ></div>				
   </div>
</div>
</@frame>

