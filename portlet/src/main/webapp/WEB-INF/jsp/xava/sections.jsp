<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.view.meta.MetaView" %>


<%@page import="org.openxava.view.View"%><jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="layoutPainterManager" class="org.openxava.web.layout.LayoutPainterManager" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
java.util.Collection sections = view.getSections();
int activeSection = view.getActiveSection();
if (!layoutPainterManager.renderSection(view, pageContext)) {

%>

<table width='100%' cellspacing="0" border="0" cellpadding="0">
	<tr><td>

<div class="<%=style.getSection()%>">
	<table <%=style.getSectionTableAttributes()%>>
    	<tr>
    		<%=style.getSectionBarStartDecoration()%>    		
	<% 
	java.util.Iterator itSections = sections.iterator();
	int i=0;
	while (itSections.hasNext()) {
		MetaView section = (MetaView) itSections.next();
		if (activeSection == i) {
	%>        
			<%=style.getActiveSectionTabStartDecoration(i == 0, !itSections.hasNext())%>
			<%=section.getLabel(request)%>
			<%=style.getActiveSectionTabEndDecoration()%>
    <%
		}
		else {
    %>
    		<%=style.getSectionTabStartDecoration(i == 0, !itSections.hasNext())%>
				<%
				String viewObjectArgv = "xava_view".equals(viewObject)?"":",viewObject=" + viewObject;
				%>
				<xava:link action='Sections.change' argv='<%="activeSection=" + i + viewObjectArgv%>' cssClass='<%=style.getSectionLink()%>' cssStyle='<%=style.getSectionLinkStyle()%>'>
				<%=section.getLabel(request)%>
				</xava:link>      
			<%=style.getSectionTabEndDecoration()%>	
  	<%   	
		}
		i++;
  	} 
  	%>                
  			<%=style.getSectionBarEndDecoration()%>  	
	</tr>
  </table>
</div>      
	
	</td></tr>
	
	<tr><td class="<%=style.getActiveSection()%>">
		<%
			String viewName = viewObject + "_section" + activeSection;
			context.put(request, viewName, view.getSectionView(activeSection));
		%>			
		<table width='100%' cellspacing="0" border="0" cellpadding="2"><tr><td>
		<jsp:include page="detail.jsp"> 
			<jsp:param name="viewObject" value="<%=viewName%>" />
		</jsp:include>				
		</td></tr></table>
	</td></tr>	
</table>
<br>
<%
} // END IF Not painter is in use
%>
