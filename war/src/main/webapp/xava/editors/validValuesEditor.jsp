<%@ include file="validValueEditorCommon.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<%
if (editable) { 
%>
<select id="<%=propertyKey%>" name="<%=propertyKey%>" class=<%=style.getEditor()%> <%=script%> title="<%=p.getDescription(request)%>">
	<option value="<%=baseIndex==0?"":"0"%>"></option>
<%
	java.util.Iterator it = p.validValuesLabels(request);
	for (int i = baseIndex; it.hasNext(); i++) {
		String selected = value == i ?"selected":"";
%>
	<option value="<%=i%>" <%=selected%>><%=it.next()%></option>
<%
	} // while
%>
</select>	
<% 
} else { 
	if (label) {
%>
	<%=description%>
<%
	}
	else {
%>
	<input name = "<%=propertyKey%>_DESCRIPTION_" class=<%=style.getEditor()%>
	type="text" 
	title="<%=p.getDescription(request)%>"	
	maxlength="<%=p.getSize()%>" 	
	size="<%=p.getSize()%>" 
	value="<%=description%>"
	disabled
	/>
<%  } %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value==-1?"":String.valueOf(value)%>">	
<% } %>			
