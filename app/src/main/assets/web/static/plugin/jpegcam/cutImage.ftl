<#include "/freemarker/frame.ftl"> 
<@frame title="头像采集">
<link rel="stylesheet" type="text/css" href="plugin/imgareaselect/css/imgareaselect-default.css" />
<script type="text/javascript" src="plugin/imgareaselect/scripts/jquery.imgareaselect.pack.js"></script>
<script type="text/javascript" src="plugin/jpegcam/webcam.js"></script>
<style type="text/css">
#cam,#results{width:320px;}
</style>
    <script language="JavaScript">
		function preview(img, selection) {
		    if (!selection.width || !selection.height)
		        return;
		    
		    var scaleX = 140 / selection.width;
		    var scaleY = 175 / selection.height;
		
		    $('#preview img').css({
		        width: Math.round(scaleX * 320),
		        height: Math.round(scaleY * 240),
		        marginLeft: -Math.round(scaleX * selection.x1),
		        marginTop: -Math.round(scaleY * selection.y1)
		    });
		}
		    
		function areaSelect(){
			$('img#photo').imgAreaSelect(
							{ 	aspectRatio: '4:5',
								handles: true ,
								fadeSpeed: 200, 
								onSelectChange: preview,
								x1: 90, y1: 50, 
								x2: 230, y2: 225 });
		}
	</script>  
<div id="main">
 
   <div id="results" >
   		<div id="snap_img"></div>
   		<div id="button">
	   		<p style="text-align:center">
			<input type=button value="重拍" class="btn" onClick="retake_snapshot()">
			<input type=button value="截取" class="btn" onClick="areaSelect()">
	        </p>
   		</div>
 <div style="float: left; width: 50%;">
    <p style="font-size: 110%; font-weight: bold; padding-left: 0.1em;">
      预览
    </p>
  
    <div class="frame" 
      style="margin: 0 1em; width: 140px; height: 175px;">
      <div id="preview" style=" width: 140px; height: 175px; overflow: hidden;">
        <img src="" />
      </div>
    </div>
  </div>
   </div>
</div>
</@frame>

