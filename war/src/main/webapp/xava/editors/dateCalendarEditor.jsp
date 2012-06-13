<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
  
<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String align = p.isNumber()?"right":"left";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
String script = request.getParameter("script");
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
if (editable || !label) {
%>
<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class=<%=style.getEditor()%> title="<%=p.getDescription(request)%>"
	align='<%=align%>'
	maxlength="<%=p.getSize()%>" 
	size="<%=p.getSize()%>" 	 
	value="<%=fvalue%>" <%=disabled%>	<%=script%>><%if (editable) {%><input type="image"
	name="<%=propertyKey%>_CALENDAR_BUTTON_"
	src="<%=request.getContextPath() %>/xava/images/calendar.gif" alt="..."
	style='vertical-align: middle;'
	onclick="return showCalendar('<%=propertyKey%>', '<%=org.openxava.util.Dates.dateFormatForJSCalendar(org.openxava.util.Locales.getCurrent())%>');"><%} %>
	
<%

} else {
%>
<%=fvalue%>&nbsp;	
<%
}
%>
<% if (!editable) { %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
<% } %>			
