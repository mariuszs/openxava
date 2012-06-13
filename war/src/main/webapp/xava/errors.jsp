<%@ include file="imports.jsp"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>


<%
if (errors.contains()) {
%>
<div class='<%=style.getMessagesWrapper()%>'>
<table id="<xava:id name='errors_table'/>">
<%
	java.util.Iterator it = errors.getStrings(request).iterator();
	while (it.hasNext()) {		
%>
<tr><td class='<%=style.getErrors()%>'>
<%=style.getErrorStartDecoration()%>
<%=it.next()%>
<%=style.getErrorEndDecoration()%>
</td></tr>
<% } %>
</table>
</div>
<% } %>
