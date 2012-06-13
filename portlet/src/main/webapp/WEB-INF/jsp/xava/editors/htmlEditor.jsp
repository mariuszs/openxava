<%
boolean editable="true".equals(request.getParameter("editable"));

if (editable) {
%>
<jsp:include page="textAreaEditor.jsp">
	<jsp:param name="rich" value="true" />
</jsp:include>
<%
}
else {
	String propertyKey = request.getParameter("propertyKey");
	String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>
<input type="hidden" name="<%=propertyKey%>" value='<%=org.openxava.util.Strings.change(fvalue, "'", "&#39;")%>'>
<%=fvalue%>
<%
}
%>



