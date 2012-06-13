<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String [] fvalues = (String []) request.getAttribute(propertyKey + ".fvalue");
String fDate = fvalues[0];
String fTime = fvalues[1];
String fvalue = fDate + " " + fTime;
String align = p.isNumber()?"right":"left";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
String script = request.getParameter("script");
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
if (editable || !label) {
%>
<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class=<%=style.getEditor()%> title="<%=p.getDescription(request)%>"
	align='<%=align%>'
	maxlength="10" 
	size="10"  
	value="<%=fDate%>" <%=disabled%>	<%=script%>><%if (editable) {%><input type="image"
	name="<%=propertyKey%>_CALENDAR_BUTTON_"
	src="<%=request.getContextPath() %>/xava/images/calendar.gif" alt="..."
	style='vertical-align: middle;'
	onclick="return showCalendar('<%=propertyKey%>', '<%=org.openxava.util.Dates.dateFormatForJSCalendar(org.openxava.util.Locales.getCurrent())%>');"><%} %> <input name="<%=propertyKey%>" class=<%=style.getEditor()%>
	type="text" 
	title="<%=p.getDescription(request)%>"
	align='<%=align%>'
	maxlength="8" 
	size="8" 
	value="<%=fTime%>"
	<%=disabled%>
	<%=script%>	/>
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
