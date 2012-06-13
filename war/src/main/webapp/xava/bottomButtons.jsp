<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.controller.meta.MetaAction" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
boolean buttonBar = !"false".equalsIgnoreCase(request.getParameter("buttonBar"));
String mode = request.getParameter("xava_mode"); 
if (mode == null) mode = manager.isSplitMode()?"detail":manager.getModeName(); 
%>
 
<button name="xava.DEFAULT_ACTION" type="submit" 
	onclick="openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '', false, '<%=manager.getDefaultActionQualifiedName()%>')"
	style="padding: 0; border: none; background-color:transparent; size: 0"></button>
 
<% 
String defaultAction = null; 
if (manager.isDetailMode()) { 
	defaultAction = manager.getDefaultActionQualifiedName();
%>
<xava:button action="<%=defaultAction%>"/>
<% 
} 
%>

<%
java.util.Iterator it = manager.getMetaActions().iterator();
while (it.hasNext()) {
	MetaAction action = (MetaAction) it.next();
	if (action.isHidden()) continue;
	if (action.getQualifiedName().equals(defaultAction)) continue;
	if (action.appliesToMode(mode) && (!buttonBar || !action.hasImage())) {   
	%>
	<xava:button action="<%=action.getQualifiedName()%>"/>
	<%
	}
}
%>
