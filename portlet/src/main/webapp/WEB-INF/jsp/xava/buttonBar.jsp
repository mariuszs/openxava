<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.util.XavaPreferences"%>
<%@ page import="org.openxava.util.Is"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
boolean onBottom = false;
String mode = request.getParameter("xava_mode"); 
if (mode == null) mode = manager.isSplitMode()?"detail":manager.getModeName();
boolean headerButtonBar = !manager.isSplitMode() || mode.equals("list");  

if (manager.isButtonBarVisible()) {
%>
	<div class="<%=style.getButtonBar()%>">
	<%
	java.util.Iterator it = manager.getMetaActions().iterator();
	boolean showLabels = XavaPreferences.getInstance().isShowLabelsForToolBarActions(); 
	while (it.hasNext()) {
		MetaAction action = (MetaAction) it.next();
		if (action.isHidden()) continue;
		if (action.appliesToMode(mode) && action.hasImage()) {
		%>
		<jsp:include page="barButton.jsp">
			<jsp:param name="action" value="<%=action.getQualifiedName()%>"/>
		</jsp:include>		
		<%
		} 
	}
	%>
	
	<span style="float: right">	
	<%
	java.util.Stack previousViews = (java.util.Stack) context.get(request, "xava_previousViews"); 
	if (headerButtonBar && previousViews.isEmpty()) { 
		String positionClass = null;		
		java.util.Collection actions = manager.getMetaActionsMode();
		java.util.Iterator itActions = actions.iterator();
		if (style.isOnlyOneButtonForModeIfTwoModes() && actions.size() == 2) {
			while (itActions.hasNext()) {
				MetaAction action = (MetaAction) itActions.next();
				String modeNameAction = action.getName().startsWith("detail")?"detail":action.getName();
				if (!modeNameAction.equals(manager.getModeName())) {
				%>
		<jsp:include page="barButton.jsp">
			<jsp:param name="action" value="<%=action.getQualifiedName()%>"/>
		</jsp:include>
				&nbsp;						
				<%					
					break;
				}
			}
		}
		else while (itActions.hasNext()) {
			MetaAction action = (MetaAction) itActions.next();
			if (positionClass == null) {
				positionClass = style.getFirst();			
			}
			else if (!itActions.hasNext()) positionClass = style.getLast();
			else positionClass = "";						
			%>			
			<span class="<%=positionClass%>">			
			<%
			String modeNameAction = action.getName().startsWith("detail")?"detail":action.getName();
			if (modeNameAction.equals(manager.getModeName())) {			
			%>
			<span class="<%=style.getActive()%>">
				<a href="" class="<%=style.getButtonBarModeButton()%>">
					&nbsp;&nbsp;
					<%=action.getLabel(request)%>
					&nbsp;&nbsp;
				</a>
			</span>			
			<%
			}
			else {	
			%>  							
			<xava:link action="<%=action.getQualifiedName()%>" cssClass="<%=style.getButtonBarModeButton()%>">
				&nbsp;&nbsp;							 			
				<%=action.getLabel(request)%>
				&nbsp;&nbsp;					
			</xava:link>			
			<%
			}
			%>			 
			</span>			
			<%
		}
	}	

	String language = request.getLocale().getLanguage();
	String href = XavaPreferences.getInstance().getDefaultHelpPrefix() + language;
	String target = XavaPreferences.getInstance().isHelpInNewWindow() ? "_blank" : "";
	if (!Is.empty(XavaPreferences.getInstance().getHelpPrefix())) { 
		href = 
			"/" + manager.getApplicationName() + "/" + 
			XavaPreferences.getInstance().getHelpPrefix() +
			manager.getModuleName() +
			"_" + language + 
			XavaPreferences.getInstance().getHelpSuffix();
	} 
	if (style.isHelpAvailable()) {
		String helpImage = !style.getHelpImage().startsWith("/")?request.getContextPath() + "/" + style.getHelpImage():style.getHelpImage();
	%>
		<span class="<%=style.getHelp()%>">  
			<a href="<%=href%>" target="<%=target%>"><img src="<%=helpImage%>"/></a>
		</span>
	<%
	}
	%>
	&nbsp;
	</span>		

	</div>
	
<% } // end isButtonBarVisible %>