<#include "/frame.ftl"> 
<@frame title="头像采集">
<form action='photoCap!capture?type=upload&sfid=342524198609050054' method="post"
	 enctype="multipart/form-data" 
	  theme="simple" >
	<@s.file name="image"/>
	<input type="submit"/>
</form>
</@frame>

