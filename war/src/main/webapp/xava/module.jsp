<%@ include file="imports.jsp"%>

<%@page import="java.io.File"%>
<%@page import="java.util.Arrays"%>
<%@page import="org.openxava.util.XavaResources"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="org.openxava.util.XSystem"%>
<%@page import="org.openxava.util.Strings"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.web.dwr.Module"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.apache.commons.logging.LogFactory" %>
<%@page import="org.apache.commons.logging.Log" %>

<%!private static Log log = LogFactory.getLog("module.jsp");

	private String getAdditionalParameters(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		for (java.util.Enumeration en = request.getParameterNames(); en
				.hasMoreElements();) {
			String name = (String) en.nextElement();
			if ("application".equals(name) || "module".equals(name)
					|| "xava.portlet.application".equals(name)
					|| "xava.portlet.module".equals(name))
				continue;
			String value = request.getParameter(name);
			result.append('&');
			result.append(name);
			result.append('=');
			result.append(value);
		}
		return result.toString();
	}%>

<%
	request.setAttribute("style", org.openxava.web.style.Style.getInstance(request));
%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<%
	Servlets.setCharacterEncoding(request, response);
	Locales.setCurrent(request);	
	request.getSession().setAttribute("xava.user",
			request.getRemoteUser());
	Users.setCurrent(request); 
	String app = request.getParameter("application");
	String module = context.getCurrentModule(request); 
	String contextPath = (String) request.getAttribute("xava.contextPath");
	if (contextPath == null) contextPath = request.getContextPath();

	org.openxava.controller.ModuleManager managerHome = (org.openxava.controller.ModuleManager) context
			.get(request, "manager",
					"org.openxava.controller.ModuleManager");
	org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context
			.get(app, module, "manager",
					"org.openxava.controller.ModuleManager");

	manager.setSession(session);
	manager.setApplicationName(request.getParameter("application"));

	manager.setModuleName(module); // In order to show the correct description in head

	if (manager.isFormUpload()) {
		new Module().requestMultipart(request, response, app, module);
	}
	else {
		Module.restoreLastMessages(request, app, module); 
	}	
	boolean isPortlet = (session.getAttribute(Ids.decorate(app, request
			.getParameter("module"), "xava.portlet.uploadActionURL")) != null);

	Module.setPortlet(isPortlet);
	String version = org.openxava.controller.ModuleManager.getVersion();
	String realPath = request.getSession().getServletContext()
			.getRealPath("/");			
%>
<jsp:include page="execute.jsp"/>
<%
	if (!isPortlet) {
%>
 
<!DOCTYPE html>

<head>
	<title><%=managerHome.getModuleDescription()%></title>
	
	<%=style.getMetaTags()%>
	
	<%
 		String[] jsFiles = style.getNoPortalModuleJsFiles();
 			if (jsFiles != null) {
 				for (int i = 0; i < jsFiles.length; i++) {
 	%>
	<script src="<%=contextPath%>/xava/style/<%=jsFiles[i]%>?ox=<%=version%>" type="text/javascript"></script>
	<%
				}
			}
	%>

<%
	}
%>
	<%
		for (java.util.Iterator it = style.getAdditionalCssFiles()
				.iterator(); it.hasNext();) {
			String cssFile = (String) it.next();
	%> 
	<link rel="stylesheet" type="text/css" media="all" href="<%=contextPath%><%=cssFile%>?ox=<%=version%>"/>
	<%
		}
	%> 	
	<link href="<%=contextPath%>/xava/style/<%=style.getCssFile()%>?ox=<%=version%>" rel="stylesheet" type="text/css">
	<script type='text/javascript' src='<%=contextPath%>/xava/js/dwr-engine.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/util.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Module.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Tab.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/View.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/openxava.js?ox=<%=version%>'></script> 
	<script type='text/javascript'>
		openxava.lastApplication='<%=app%>'; 		
		openxava.lastModule='<%=module%>'; 	
		openxava.language='<%=request.getLocale().getLanguage()%>';
	</script>	
	<%
		if (style.isNeededToIncludeCalendar()) {
	%>
	<script type="text/javascript" src="<%=contextPath%>/xava/editors/calendar/calendar.js?ox=<%=version%>"></script>
	<script type="text/javascript" src="<%=contextPath%>/xava/editors/calendar/lang/calendar-<%=Locales.getCurrent().getLanguage()%>.js?ox=<%=version%>"></script>	
	<%
			}
		%>	
	<script type='text/javascript' src='<%=contextPath%>/xava/js/calendar.js?ox=<%=version%>'></script>
	<%
		if (new File(realPath + "/xava/js/custom-editors.js").exists()) {
	%>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/custom-editors.js?ox=<%=version%>'></script>
	<%
		log.warn(XavaResources.getString("custom_editors_deprecated"));
		}
	%>	
	<script type="text/javascript">
		if (typeof jQuery != "undefined") {  
			portalJQuery = jQuery;
		}       
	</script>				
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery.js?ox=<%=version%>"></script>
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery-ui.js?ox=<%=version%>"></script>	
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery.bgiframe.js?ox=<%=version%>"></script>
	<%
		File jsEditorsFolder = new File(realPath + "/xava/editors/js");		
		String[] jsEditors = jsEditorsFolder.list();
		Arrays.sort(jsEditors);
		for (int i = 0; i < jsEditors.length; i++) {
			if (jsEditors[i].endsWith(".js")) {
	%>
	<script type="text/javascript" src="<%=contextPath%>/xava/editors/js/<%=jsEditors[i]%>?ox=<%=version%>"></script>
	<%
			}
		}
	%>	
	<script type="text/javascript">
		$ = jQuery;
		if (typeof portalJQuery != "undefined") {  
			jQuery = portalJQuery;    
		}   
	</script>
<%
	if (!isPortlet) {
%>
</head> 
<body bgcolor="#ffffff">
<%=style.getNoPortalModuleStartDecoration(managerHome
						.getModuleDescription())%>
<%
	}
%> 
<% 
boolean coreViaAJAX = !manager.getPreviousModules().isEmpty() || manager.getDialogLevel() > 0 || manager.hasInitForwardActions(); 

if (manager.isResetFormPostNeeded()) {	
%>		
	<form id="xava_reset_form">
		<% if (!"true".equals(request.getParameter("friendlyURL"))) { // To support old URL style (with xava/moduls.jsp?application=...) %>
		<input name="application" type="hidden" value="<%=request.getParameter("application")%>"/>
		<input name="module" type="hidden" value="<%=request.getParameter("module")%>"/>
		<% } %>
	</form>
<% } else  { %>	
	<input id="xava_last_module_change" type="hidden" value=""/>
	<input id="<xava:id name='loading'/>" type="hidden" value="<%=coreViaAJAX%>"/>
	<input id="<xava:id name='loaded_parts'/>" type="hidden" value=""/>
	<input id="<xava:id name='view_member'/>" type="hidden" value=""/>
		
	<%-- Layer for progress bar --%>
	<div id='xava_processing_layer' style='position:absolute;top:100px;left:150px;display:none; z-index: 9999'>
	<table cellspacing='0'>
	   <tr class='<%=style.getProcessing()%>'>
	       <td align='center' valign='middle' style='line-height:1.4;padding:25px 80px;border:2px solid #000'>
	           <%=XavaResources.getString(request, "processing")%><br/>
	           <img src='<%=contextPath%>/xava/images/processing.gif' name='xava_processingImage'/>
	       </td>
	   </tr>
	</table>
	</div>	 
	<%=style.getCoreStartDecoration()%>
	<div id="<xava:id name='core'/>" style="display: inline;" class="<%=style.getModule()%>">
		<%			
			if (!coreViaAJAX) {
		%>
		<jsp:include page="core.jsp"/>
		<%
			}
		%>		
	</div>
	<%=style.getCoreEndDecoration()%>
	
<% } %>			
	<div id="xava_console" >
	</div>
	<% String loadingImage=!style.getLoadingImage().startsWith("/")?contextPath + "/" + style.getLoadingImage():style.getLoadingImage();%>
	<div id="xava_loading">				
		<img src="<%=loadingImage%>" style="vertical-align: middle"/>
		&nbsp;<xava:message key="loading"/>...		 
	</div>
	<% if (!style.isFixedPositionSupported()) { %>
	<div id="xava_loading2">		
		<img src="<%=loadingImage%>" style="vertical-align: middle"/>
		&nbsp;<xava:message key="loading"/>...
	</div>	
	<% } %>	
<%
	if (!isPortlet) {
%>
<%=style.getNoPortalModuleEndDecoration()%>
</body>
</html>
<%
	}
%>

<% 
if (manager.isResetFormPostNeeded()) {  
	manager.setResetFormPostNeeded(false);		
%>		
	<script type="text/javascript">
	$("#xava_reset_form").submit();
	</script>		
<% } else  { 
		String browser = request.getHeader("user-agent"); 
%>

<script type="text/javascript">
<%String prefix = Strings.change(manager.getApplicationName(), "-",
					"_")
					+ "_" + Strings.change(manager.getModuleName(), "-", "_");
			String onLoadFunction = prefix + "_openxavaOnLoad";
			String initiated = prefix + "_initiated";%>
<%=onLoadFunction%> = function() { 
	if (openxava != null && openxava.<%=initiated%> == null) {
		openxava.showFiltersMessage = '<xava:message key="show_filters"/>';
		openxava.hideFiltersMessage = '<xava:message key="hide_filters"/>';
		openxava.selectedRowClass = '<%=style.getSelectedRow()%>';
		openxava.currentRowClass = '<%=style.getCurrentRow()%>';
		openxava.currentRowCellClass = '<%=style.getCurrentRowCell()%>';
		openxava.closeDialogOnEscape = <%=browser != null && browser.indexOf("Firefox") >= 0 ? "false":"true"%>;		  
		openxava.calendarAlign = '<%=browser != null && browser.indexOf("MSIE 6") >= 0 ? "tr"
					: "Br"%>';
		openxava.setHtml = <%=style.getSetHtmlFunction()%>;			
		<%String initThemeScript = style.getInitThemeScript();
			if (initThemeScript != null) {%>
		openxava.initTheme = function () { <%=style.getInitThemeScript()%> }; 
		<%}%>
		openxava.init("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>");
		<%if (coreViaAJAX) {%>
		openxava.ajaxRequest("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>", true);	
		<%}%>
		openxava.<%=initiated%> = true;
	}	
}
window.onload = <%=onLoadFunction%>;
setTimeout('<%=onLoadFunction%>()', 1000);
document.additionalParameters="<%=getAdditionalParameters(request)%>";
</script>
<% } %>