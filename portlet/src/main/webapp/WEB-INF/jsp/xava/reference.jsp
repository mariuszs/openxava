<%@ include file="imports.jsp"%>

<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.openxava.model.meta.MetaReference" %>
<%@ page import="org.openxava.view.meta.MetaPropertyView" %>


<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.web.WebEditors"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.util.Is"%>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
boolean onlyEditor = "true".equalsIgnoreCase(request.getParameter("onlyEditor"));
boolean frame = "true".equalsIgnoreCase(request.getParameter("frame")); 
boolean composite = "true".equalsIgnoreCase(request.getParameter("composite"));
boolean descriptionsList = "true".equalsIgnoreCase(request.getParameter("descriptionsList")); 
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String referenceKey = request.getParameter("referenceKey");
MetaReference ref = (MetaReference) request.getAttribute(referenceKey); 
String labelKey = "xava_label_" + referenceKey;
if (!descriptionsList) descriptionsList = view.displayAsDescriptionsList(ref); 
%>

<%@ include file="htmlTagsEditor.jsp"%>

<%
String editableKey = referenceKey + "_EDITABLE_";
boolean editable = view.isEditable(ref);
int labelFormat = view.getLabelFormatForReference(ref);
String labelStyle = view.getLabelStyleForReference(ref);
if (Is.empty(labelStyle)) labelStyle = XavaPreferences.getInstance().getDefaultLabelStyle();
String label = ref.getLabel(request);
%>
<% if (!onlyEditor) { %>
<%=preLabel%>
<% if (labelFormat == MetaPropertyView.NORMAL_LABEL) { %>
<span id="<xava:id name='<%="label_" + view.getPropertyPrefix() + ref.getName()%>'/>" class="<%= labelStyle%>">
<%=label%>
</span>
<% } %>
<%=postLabel%>
<%=preIcons%>
<% if (labelFormat != MetaPropertyView.SMALL_LABEL) { %>
<%@ include file="referenceEditorIcons.jsp"%>
<% } %>
<%=postIcons%>
<%=preEditor%>
<% if (labelFormat == MetaPropertyView.SMALL_LABEL) { %>
<table border='0' cellpadding='0', cellspacing='0'><tr><td align='bottom'> 
<span id='<xava:id name='<%="label_" + view.getPropertyPrefix() + ref.getName()%>'/>' class="<%=style.getSmallLabel()%> <%=labelStyle %>">
<%=label%>
</span>
 
<%@ include file="referenceEditorIcons.jsp"%>

</td></tr>
<tr><td style='vertical-align: middle'>
<% } %>

<% } // !onlyEditor %>
<%

Collection keys = ref.getMetaModelReferenced().getAllKeyPropertiesNames(); 
String keyProperty = "";
String keyProperties = "";
String propertyKey = null;
if (keys.size() == 1) {		
	keyProperty = keys.iterator().next().toString();
	propertyKey = Ids.decorate(request, referenceKey + "." + keyProperty);
	if (!composite) { 
		Map values = (Map) view.getValue(ref.getName());	
		values = values == null?java.util.Collections.EMPTY_MAP:values;
		Object value = values.get(keyProperty);
		String valueKey = propertyKey + ".value";
		request.setAttribute(valueKey, value);		
		String fvalue = value==null?"":value.toString();
		request.setAttribute(propertyKey + ".fvalue", fvalue);
	}
}
else {	
	propertyKey = referenceKey + "__KEY__";
	Map values = null; 
	if (!composite) { 
		values = (Map) view.getValue(ref.getName());
		values = values == null?java.util.Collections.EMPTY_MAP:values;
	}
	java.util.Iterator it = keys.iterator();
	StringBuffer sb = new StringBuffer();
	while (it.hasNext()) {
		String property = (String) it.next();
		if (!composite) { 
			Object value = values.get(property);
			String valueKey = Ids.decorate(request, referenceKey + "." + property) + ".value"; 
			request.setAttribute(valueKey, value);
		}
		sb.append(property);
		if (it.hasNext()) {
			sb.append(',');
		}
	}
	if (!composite) { 
		String key = ref.getMetaModelReferenced().toString(values); 
		String fvalue = key==null?"0":key;
		request.setAttribute(propertyKey + ".fvalue", fvalue);
	}
	keyProperties = sb.toString();
}

boolean throwChanged=view.throwsReferenceChanged(ref);
String script = throwChanged?
	"onchange='openxava.throwPropertyChanged(\"" + 
			request.getParameter("application") + "\", \"" + 
			request.getParameter("module") + "\", \"" +			
			propertyKey + "\")'":"";
%>

<% if (!composite) { %>
<span id="<xava:id name='<%="reference_editor_" + view.getPropertyPrefix() + ref.getName()%>'/>">
<% } %> 
<input type="hidden" name="<%=editableKey%>" value="<%=editable%>"/>

<%
if (descriptionsList) {
	String descriptionProperty = view.getDescriptionPropertyInDescriptionsList(ref);
	String descriptionProperties = view.getDescriptionPropertiesInDescriptionsList(ref);
	String parameterValuesProperties=view.getParameterValuesPropertiesInDescriptionsList(ref);
	String condition = view.getConditionInDescriptionsList(ref);
	boolean orderByKey = view.isOrderByKeyInDescriptionsList(ref);
	String order = view.getOrderInDescriptionsList(ref); 
	org.openxava.tab.meta.MetaTab metaTab = ref.getMetaModelReferenced().getMetaComponent().getMetaTab();
	String filter = "";
	if (metaTab.hasFilter()) {
		filter = metaTab.getMetaFilter().getClassName(); 
	}
	if (metaTab.hasBaseCondition()) {
		if (org.openxava.util.Is.emptyString(condition)) {
			condition = metaTab.getBaseCondition();
		}
		else {
			condition = metaTab.getBaseCondition() + " AND " + condition;
		}
	}	
%>
	<jsp:include page="editors/descriptionsEditor.jsp">
		<jsp:param name="script" value="<%=script%>"/>
		<jsp:param name="propertyKey" value="<%=propertyKey%>"/>
		<jsp:param name="editable" value="<%=editable%>"/>
		<jsp:param name="model" value="<%=ref.getReferencedModelName()%>"/>
		<jsp:param name="keyProperty" value="<%=keyProperty%>"/>
		<jsp:param name="keyProperties" value="<%=keyProperties%>"/>
		<jsp:param name="descriptionProperty" value="<%=descriptionProperty%>"/>
		<jsp:param name="descriptionProperties" value="<%=descriptionProperties%>"/>
		<jsp:param name="parameterValuesProperties" value="<%=parameterValuesProperties%>"/>
		<jsp:param name="condition" value="<%=condition%>"/>
		<jsp:param name="orderByKey" value="<%=orderByKey%>"/>
		<jsp:param name="order" value="<%=order%>"/>
		<jsp:param name="filter" value="<%=filter%>"/>
	</jsp:include>	
<%
}
else {
	String editorURL = "editors/" + WebEditors.getMetaEditorFor(ref, view.getViewName()).getUrl()
		+ "?script=" + script
		+ "&propertyKey=" + propertyKey	
		+ "&editable=" + editable;
%>
	<jsp:include page="<%=editorURL%>" />
<%	
}
%>




<%
String keyPropertyForAction = Ids.undecorate(propertyKey); 

if (!frame && editable && view.isCreateNewForReference(ref)) { 
%>
<xava:action action='Reference.createNew' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
}
%>
<%
if (!frame && editable && view.isModifyForReference(ref)) { 
%>
<xava:action action='Reference.modify' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
}
%>


<%
java.util.Iterator itActions = view.getActionsNamesForReference(ref, editable).iterator();
if (!frame) while (itActions.hasNext()) {
	String action = (String) itActions.next();
%>
<xava:action action="<%=action%>"/>
<%
}
%>
<% if (!composite) { %>
</span>
<% } %>
<%=postEditor%>
<% if (!onlyEditor && labelFormat == MetaPropertyView.SMALL_LABEL) { %>
</td></tr>
</table>
<% } %>
