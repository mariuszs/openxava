
<%
String sfirst = request.getParameter("first"); 
boolean first="true".equals(sfirst)?true:false;

String preLabel=null;
String postLabel=null;
String preIcons=null;
String postIcons=null;
String preEditor=null;
String postEditor=null;

if (first && !view.isAlignedByColumns()) {  
	preLabel="<td style='vertical-align: middle;' class='" + style.getLabel() + "'>"; 
	postLabel="</td>";
	preIcons="<td style='vertical-align: middle' class='" + style.getEditorWrapper()+ "'>";
	postIcons="</td>";	
	preEditor="<td style='vertical-align: middle;'><table border='0' cellpadding='0' cellspacing='0'><tr><td style='vertical-align: middle' class='" + style.getEditorWrapper()+ "'>";
	postEditor="</td>";
} 
else {	
	preLabel="<td style='vertical-align: middle;' class='" + style.getLabel() + "'>&nbsp;&nbsp;"; 
	postLabel="</td>";
	preIcons="<td style='vertical-align: middle' class='" + style.getEditorWrapper()+ "'>";
	postIcons="</td>";
	preEditor="<td style='vertical-align: middle' class='" + style.getEditorWrapper()+ "'>";
	postEditor="</td>";
}
%>