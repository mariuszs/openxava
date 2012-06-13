<%@ include file="imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.filters.IFilter"%>
<%@ page import="org.openxava.filters.IRequestFilter"%>
<%@ page import="org.openxava.component.MetaComponent"%>
<%@ page import="org.openxava.tab.meta.MetaTab"%>
<%@ page import="org.openxava.util.KeyAndDescription" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.calculators.DescriptionsCalculator" %>
<%@ page import="org.openxava.formatters.IFormatter" %>

<%
String value = request.getParameter("value");
String propertyKey = request.getParameter("propertyKey");
int index = Integer.parseInt(request.getParameter("index"));
String prefix = request.getParameter("prefix");
if (prefix == null) prefix = "";

IFormatter formatter = null;
String descriptionsFormatterClass=request.getParameter("descriptionsFormatter");
if (descriptionsFormatterClass == null) {
	descriptionsFormatterClass=request.getParameter("formateadorDescripciones");
}
if (descriptionsFormatterClass != null) {
	String descriptionsFormatterKey = propertyKey + ".descriptionsFormatter";
	formatter = (IFormatter) request.getSession().getAttribute(descriptionsFormatterKey);	
	if (formatter == null) {
		try {
			formatter = (IFormatter) Class.forName(descriptionsFormatterClass).newInstance();
			request.getSession().setAttribute(descriptionsFormatterKey, formatter);	
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(XavaResources.getString("descriptionsEditor_descriptions_formatter_warning", propertyKey));
		}
	}
}

DescriptionsCalculator calculator = new DescriptionsCalculator();
// The arguments in English and Spanish for compatibility with
// a big amount of stereotypes made in spanish using this editor
String model = request.getParameter("model");
if (model == null) model = request.getParameter("modelo");
MetaTab metaTab = MetaComponent.get(model).getMetaTab();
if (metaTab.getMetaFilter() != null){
	if (metaTab.getMetaFilter().getFilter() != null) {
		IFilter filter = metaTab.getMetaFilter().getFilter();
		if (filter instanceof IRequestFilter) {
			((IRequestFilter) filter).setRequest(request);
		}
		calculator.setParameters(null, filter);
	}
}
calculator.setModel(model);
String condition = metaTab.getBaseCondition();
if (!Is.empty(condition) && !Is.empty(request.getParameter("condition"))) condition = condition + " AND ";
condition = condition + request.getParameter("condition");
calculator.setCondition(condition);
calculator.setOrder(metaTab.getDefaultOrder());
calculator.setUseConvertersInKeys(true);
String keyProperty = request.getParameter("keyProperty");
if (keyProperty == null) keyProperty = request.getParameter("propiedadClave");
calculator.setKeyProperty(keyProperty);
String keyProperties = request.getParameter("keyProperties");
if (keyProperties == null) keyProperties = request.getParameter("propiedadesClave");
calculator.setKeyProperties(keyProperties);
String descriptionProperty = request.getParameter("descriptionProperty");
if (descriptionProperty == null) descriptionProperty = request.getParameter("propiedadDescripcion");
calculator.setDescriptionProperty(descriptionProperty);
String descriptionProperties = request.getParameter("descriptionProperties");
if (descriptionProperties == null) descriptionProperties = request.getParameter("propiedadesDescripcion");
calculator.setDescriptionProperties(descriptionProperties);
String orderByKey = request.getParameter("orderByKey");
if (orderByKey == null) orderByKey = request.getParameter("ordenadoPorClave");
calculator.setOrderByKey(orderByKey);

java.util.Collection descriptions = calculator.getDescriptions();
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
%>
<input type="hidden" name="<xava:id name='<%=prefix  + "conditionComparator."  + index%>'/>" value="eq">
<input type="hidden" name="<xava:id name='<%=prefix  + "conditionValueTo."  + index%>'/>" >

<select name="<xava:id name='<%=prefix + "conditionValue." + index%>'/>" class=<%=style.getEditor()%>>
	<option value=""></option>
<%
	java.util.Iterator it = descriptions.iterator();
	String selectedDescription = "";	
	while (it.hasNext()) {
		KeyAndDescription cl = (KeyAndDescription) it.next();	
		String selected = "";
		String description = formatter==null?cl.getDescription().toString():formatter.format(request, cl.getDescription());
		if (Is.equalAsStringIgnoreCase(value, cl.getKey())) {
			selected = "selected"; 
			selectedDescription = description;
		} 		
%>
	<option value="<%=cl.getKey()%>" <%=selected%>><%=description%></option>
<%
	} // del while
%>
</select>	