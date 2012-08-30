<%@ include file="imports.jsp"%>

<%@ page import="java.util.Iterator" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.view.meta.MetaGroup" %>
<%@ page import="org.openxava.view.meta.PropertiesSeparator" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.model.meta.MetaReference" %>
<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.web.taglib.IdTag"%>
<%@ page import="org.openxava.web.Ids"%>
<%@ page import="org.openxava.model.meta.MetaMember"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="layoutPainterManager" class="org.openxava.web.layout.LayoutPainterManager" scope="session"/>

<%!
private final static String LAST_TABLE_NOT_CLOSED = "xava.layout.detail.lastTableNotClose";
private boolean hasFrame(MetaMember m, View view) { 
	if (m instanceof MetaProperty) {
		return WebEditors.hasFrame((MetaProperty) m, view.getViewName());
	}
  	if (m instanceof MetaReference) {
  		return !view.displayReferenceWithNoFrameEditor((MetaReference) m);  		
  	}
  	return true;
}
%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
view.setViewObject(viewObject); 
String propertyPrefix = request.getParameter("propertyPrefix");
propertyPrefix = (propertyPrefix == null)?"":propertyPrefix; 
view.setPropertyPrefix(propertyPrefix);
boolean onlySections = view.hasSections() && view.getMetaMembers().isEmpty(); 
%>

<%
if (!layoutPainterManager.renderView(view, pageContext)) {
	// Only performed if no layout painter is in effect.
	if (!onlySections) {	// IF Not Only Sections
		if (view.isFrame()) {	// IF Is Frame 
%>
<table <%=style.getFrameWidth()%>>
	<tr>
<% 
	}	// END IF Is Frame
%>
<%
	Iterator it = view.getMetaMembers().iterator();
	String sfirst = request.getParameter("first");
	boolean first = !"false".equals(sfirst);
	String slast = request.getParameter("last");
	boolean last = !"false".equals(slast);
	boolean lastWasEditor = false;
	boolean lastWasProperty = false;
	boolean firstNoFrameMember = true; 
	boolean firstFrameMember = false; 
	while (it.hasNext()) {	// WHILE hasNext
		MetaMember m = (MetaMember) it.next();
		lastWasProperty = false;	
		int frameWidth = view.isVariousMembersInSameLine(m)?0:100;	
		if (!PropertiesSeparator.INSTANCE.equals(m)) {	// IF Not Properties Separator 
			if (firstNoFrameMember && !hasFrame(m, view)) {	// IF First NoFramed Member 		
					firstNoFrameMember = false;
					firstFrameMember = true;	
				if (request.getAttribute(LAST_TABLE_NOT_CLOSED) == null) {	// IF Last Table Closed
%>
	</tr>
	<tr>
		<td>
			<table>
				<tr>
<%
				} // END IF Last Table Closed
				else {	// IF Last Table Not Closed
					request.removeAttribute(LAST_TABLE_NOT_CLOSED);
				} // END IF Last Table Not Closed
			} // END IF First NoFramed Member
			else if (firstFrameMember && hasFrame(m, view)) {	// IF First Framed Member
				firstFrameMember = false;
				firstNoFrameMember = true;
%>
			</table>
<%	
			} // END IF First Framed Member
		} // END If Not Properties Separator
		if (m instanceof MetaProperty) {	// IF MetaProperty	
			MetaProperty p = (MetaProperty) m;		
			if (!PropertiesSeparator.INSTANCE.equals(m)) {	// IF Not Properties Separator	
				boolean hasFrame = WebEditors.hasFrame(p, view.getViewName());
				lastWasEditor = !hasFrame;
				lastWasProperty = true;
				String propertyKey= Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						propertyPrefix + p.getName());
				request.setAttribute(propertyKey, p);
				String urlEditor = "editor.jsp" // in this way because websphere 6 has problems with jsp:param
					+ "?propertyKey=" + propertyKey
					+ "&first=" + first
					+ "&hasFrame=" + hasFrame;		
				boolean withFrame = hasFrame && 
					(!view.isSection() || view.getMetaMembers().size() > 1);
				if (withFrame || (view.isSection() && view.getMembersNames().size() ==1)) { // IF Framed and Section
					if (first) { // IF First MetaProperty
%>		
	<tr>
		<td colspan="4">
<%	
					}  // END IF First MetaProperty
				} // END IF Framed and Section
				if (withFrame) { // IF MetaPropertt With Frame			 					
					String labelKey = Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						"label_" + propertyPrefix + p.getName()); 
					String label = view.getLabelFor(p);
%>					 
			<%=style.getFrameHeaderStartDecoration(frameWidth) %>
			<%=style.getFrameTitleStartDecoration() %>
			<span id="<%=labelKey%>"><%=label%></span>		
			<%@ include file="editorIcons.jsp"%>
			<%=style.getFrameTitleEndDecoration() %>	
			<%=style.getFrameActionsStartDecoration()%>
<% 
					String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + p.getName());
					String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
						"&closed=" + view.isFrameClosed(frameId); 
%>
			<jsp:include page='<%=frameActionsURL%>'/>
			<%=style.getFrameActionsEndDecoration()%> 					 					
			<%=style.getFrameHeaderEndDecoration() %>
			<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId))%>
<%	
				} // END MetaProperty With Frame 
%> 
			<jsp:include page="<%=urlEditor%>" />		
<%
				if (withFrame) { // IF MetaProperty With Frame
%>
			<%=style.getFrameContentEndDecoration() %>		
<%
				} // END IF MetaProperty With Frame		
				first = false;
			} // END IF Not Properties Separator
			else { // IF Properties Separator
				if (!it.hasNext()) break; 					
				first = true;						
				if (lastWasEditor && !view.isAlignedByColumns()) { // IF LastWasEditor and Not Aligned 	
%>
	</tr></table>			
<% 
				} // END IF LastWasEditor and Not Aligned
				lastWasEditor = false;
%>
		</td>
	</tr>
	<tr>
<%		
			} // END IF Properties Separator
		} // END IF MetaProperty
		else { // IF Not MetaProperty
			lastWasEditor = false;
		  	if (m instanceof MetaReference) { // IF MetaReference
				MetaReference ref = (MetaReference) m;
				String referenceKey = Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						propertyPrefix +  ref.getName()); 
				request.setAttribute(referenceKey, ref);
				if (view.displayReferenceWithNoFrameEditor(ref)) { // IF Display Reference Without Frame	
					lastWasEditor = true;			
					String urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param
						+ "?referenceKey=" + referenceKey		
						+ "&first=" + first
						+ "&frame=false&composite=false&onlyEditor=false"; 				
%>
		<jsp:include page="<%=urlReferenceEditor%>"/>
<%
					first = false;		
				} // END IF Display MetaReference Without Frame
				else {	// IF Display MeteReference With Frame			
					String viewName = viewObject + "_" + ref.getName();
					View subview = view.getSubview(ref.getName());
					context.put(request, viewName, subview);
					String propertyInReferencePrefix = propertyPrefix + ref.getName() + ".";
					boolean withFrame = subview.isFrame() && 
						(!view.isSection() || view.getMetaMembers().size() > 1);
					lastWasEditor = !withFrame;
					boolean firstForSubdetail = first || withFrame;
					if (withFrame || (view.isSection() && view.getMembersNames().size() ==1)) { // IF MetaReference With Frame
						if (first) { // IF First MetaReference	
%>		
	<tr><td colspan="4">
<%	
						} // END IF First MetaReference
					} // END IF MetaReference With Frame
					if (withFrame) { // IF MetaReference With Frame					 					
						String labelKey = Ids.decorate(
							request.getParameter("application"),
							request.getParameter("module"),
							"label_" + propertyPrefix + ref.getName()); 
						String label = view.getLabelFor(ref);
%>				
		<%=style.getFrameHeaderStartDecoration(frameWidth) %>
		<%=style.getFrameTitleStartDecoration() %>
		<span id="<%=labelKey%>"><%=label%></span>
		<%=style.getFrameTitleEndDecoration() %>
		<%=style.getFrameActionsStartDecoration()%>
<% 
						String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + ref.getName());
						String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
							"&closed=" + view.isFrameClosed(frameId); 		
%>
		<jsp:include page='<%=frameActionsURL%>'/>
		<%=style.getFrameActionsEndDecoration()%> 					 					
		<%=style.getFrameHeaderEndDecoration() %>
		<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)) %>						
<%		
					} // END IF MetaReference With Frame
			
					String urlReferenceEditor = null;
					if (view.displayReferenceWithNotCompositeEditor(ref)) { // IF Display Reference Without Composite Editor
						urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param					
							+ "?referenceKey=" + referenceKey
							+ "&onlyEditor=true&frame=true&composite=false"		
							+ "&first=" + first;				
					} // END IF Display Reference Without Composite Editor
					else { // IF Display Reference With Composite Editor
						urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param
							+ "?referenceKey=" + referenceKey
							+ "&onlyEditor=true&frame=true&composite=true"  
							+ "&viewObject=" + viewName					
							+ "&propertyPrefix=" + propertyInReferencePrefix 
							+ "&first=" + firstForSubdetail  
							+ "&last=" + !it.hasNext();
					} // END IF Display Reference With Composite Editor		
%>  
		<jsp:include page="<%=urlReferenceEditor%>"/>
<%
					if (withFrame) { // IF MetaReference With Frame
%>			
		<%=style.getFrameContentEndDecoration() %>		
<%
					} // END IF MetaReference With Frame
				} // END Display MetaReference With Frame
				first = false; 
			} else if (m instanceof MetaCollection) { // IF MetaCollection
				MetaCollection collection = (MetaCollection) m;			
				boolean withFrame = !view.isSection() || view.getMetaMembers().size() > 1;
				boolean variousCollectionInLine = view.isVariousCollectionsInSameLine((MetaMember) m);
				boolean firstCollectionInLine = view.isFirstInLine((MetaMember) m);
				String styleCollectionTogether = 
					!variousCollectionInLine ? "" : 
					(firstCollectionInLine ? "float: left; " : "float: right; ") + 
					"overflow: auto; display: block ; border: 1px solid black; width: 49%; ";
				if (!variousCollectionInLine || (variousCollectionInLine && firstCollectionInLine)) { // IF Not Various Collection or First Collection In Line
%>
	<tr><td colspan="4">		
<%
				} // END IF Not Various Collection or First Collection In Line
%>
			<div style="<%=styleCollectionTogether %>">
<%			
				if (withFrame) { // IF MetaCollection With Frame
%>	
				<%=style.getFrameHeaderStartDecoration(frameWidth)%>
				<%=style.getFrameTitleStartDecoration()%>
				<%=collection.getLabel(request) %>
<% 
				String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + collection.getName());
				String colletionHeaderId = frameId + "header";
%>				
				<span id="<xava:id name='<%=colletionHeaderId%>'/>">
					<jsp:include page="collectionFrameHeader.jsp"> 
						<jsp:param name="collectionName" value="<%=collection.getName()%>"/>
						<jsp:param name="viewObject" value="<%=viewObject%>"/>			
					</jsp:include>			
				</span>	
				<%=style.getFrameTitleEndDecoration()%>
				<%=style.getFrameActionsStartDecoration()%>
<% 
				String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
					"&closed=" + view.isFrameClosed(frameId);
%>
				<jsp:include page='<%=frameActionsURL%>'/>
				<%=style.getFrameActionsEndDecoration()%> 					 					
				<%=style.getFrameHeaderEndDecoration()%>
				<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId))%>
<%
				} // END IF MetaCollection With Frame
%>	
				<jsp:include page="collection.jsp"> 
					<jsp:param name="collectionName" value="<%=collection.getName()%>"/>
					<jsp:param name="viewObject" value="<%=viewObject%>"/>			
				</jsp:include>
<%			
				if (withFrame) { // IF MetaCollection With Frame
%>
				<%=style.getFrameContentEndDecoration()%>			
<%
				} // END IF MetaCollection With Frame
%>
			</div>
<%
			} else if (m instanceof MetaGroup) { // IF MetaGroup
				MetaGroup group = (MetaGroup) m;			
				String viewName = viewObject + "_" + group.getName();
				View subview = view.getGroupView(group.getName());			
				context.put(request, viewName, subview);
%>
<%
				if (first) { // IF First MetaGroup
					first = false;
%>
		<tr><td colspan="4">
<% 
				} // END IF First MetaGroup
%>
			<%=style.getFrameHeaderStartDecoration(frameWidth)%>
			<%=style.getFrameTitleStartDecoration()%>
			<%=group.getLabel(request)%>
			<%=style.getFrameTitleEndDecoration()%>
			<%=style.getFrameActionsStartDecoration()%>
<% 
				String frameId = Ids.decorate(request, "frame_group_" + view.getPropertyPrefix() + group.getName());
				String frameActionsURL = "frameActions.jsp?frameId=" + frameId + 
					"&closed=" + view.isFrameClosed(frameId); 
%>
			<jsp:include page='<%=frameActionsURL%>'/>
			<%=style.getFrameActionsEndDecoration()%> 					 			
			<%=style.getFrameHeaderEndDecoration()%>
			<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)) %>
			<jsp:include page="detail.jsp">
				<jsp:param name="viewObject" value="<%=viewName%>" />
			</jsp:include>
			<%=style.getFrameContentEndDecoration() %>
<%
			} // END IF MetaGroup
		} // END IF Not MetaProperty
	} // END While hasNext
%>
<% 
	if (lastWasEditor) { // IF Last Was Editor
		if (!(view.isRepresentsEntityReference() || view.isRepresentsAggregate()) || view.isFrame()) { // IF Not Entity or Aggregate or Frame
%>
</tr></table> 
<% 
		} // END IF Not Entity or Aggregate or Frame
%>
		</td>			
<%
	} // END IF Last Was Editor
%>


<% 
	if (firstFrameMember) {	// IF First Frame Member
		if (!(view.isSubview() && !view.isFrame())) { // IF Not Subview and Not Frame	
%>
	</table>
<%
		} // END IF Not Subview and Not Frame
		else { // IF Subview or Frame
			request.setAttribute(LAST_TABLE_NOT_CLOSED, new Boolean(true));
		} // END IF Subview or Frame 
	} // END IF First Frame Member
%>

<% 	
	if (view.isFrame() && 
			!(last && view.getParent() != null && !view.getParent().isFrame()) && 			  		
			!(!lastWasProperty && view.isSection() && view.getMembersNamesWithoutSectionsAndCollections().size() == 1 
					&& view.getParent() != null && view.getParent().isFrame())) { // IF Frame	
%>
		</tr>
	</table>
<% 
	} // END IF Frame
%>



<% 
} // END IF Not Only Sections 

%>

<%
if (view.hasSections()) { // IF Has Sections
%>
<% 
	if (!onlySections && view.isSubview() && !view.isFrame()) {  // IF Not Only Sections and Subview and Not Frame
%> 
	          </tr>                
              </table>
              </td>
            </tr>
            <tr>
              <td colspan="4">
              <table>                
                  <tr>
                    <td>
<%
	} // END IF Not Only Sections and Subview and Not Frame
%>
	<div id="<xava:id name='<%="sections_" + viewObject%>'/>"> 
	<jsp:include page="sections.jsp"/>
	</div>
	
<%
	if (!onlySections && view.isSubview() && !view.isFrame()) { // IF Not Only Sections and Subview and Not Frame
%>
		 			</td>
<% 
	} // END IF Not Only Sections and Subview and Not Frame
  } // END IF using non-layout renderer
}
%>
