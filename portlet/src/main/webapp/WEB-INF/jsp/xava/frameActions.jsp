<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String frameId=request.getParameter("frameId");
boolean closed="true".equals(request.getParameter("closed"));
String frameContentId=frameId + "content";
String frameShowId=frameId + "show";
String frameHideId=frameId + "hide";
String hideStyle=closed?"style='display: none'":"";
String showStyle=closed?"":"style='display: none'";

String minimizeImage=!style.getMinimizeImage().startsWith("/")?request.getContextPath() + "/" + style.getMinimizeImage():style.getMinimizeImage();
String restoreImage=!style.getRestoreImage().startsWith("/")?request.getContextPath() + "/" + style.getRestoreImage():style.getRestoreImage();
%> 		

<span id="<%=frameHideId%>" <%=hideStyle%>>
	<a href="javascript:openxava.hideFrame('<%=frameId%>')">
		<img src="<%=minimizeImage%>" border=0 align="absmiddle"/>
	</a>
</span> 
<span id="<%=frameShowId%>"' <%=showStyle%>>
	<a href="javascript:openxava.showFrame('<%=frameId%>')">
		<img src="<%=restoreImage%>" border=0 align="absmiddle"/>
	</a>
</span>
