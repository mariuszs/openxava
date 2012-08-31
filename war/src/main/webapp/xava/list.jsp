<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.util.Labels"%>
<%@ page import="org.openxava.tab.impl.IXTableModel" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.web.Actions" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="java.util.prefs.Preferences" %>
<%@ page import="org.openxava.util.XavaResources"%> 

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
String collection = request.getParameter("collection"); 
String id = "list";
String collectionArgv = "";
String prefix = "";
String tabObject = request.getParameter("tabObject");
String scrollId = "list_scroll"; 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
if (collection != null && !collection.equals("")) {
	id = collection;
	collectionArgv=",collection="+collection;
	prefix = tabObject + "_";
	scrollId = "collection_scroll"; 
}
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
tab.setIgnorePageRowCount(!style.isChangingPageRowCountAllowed());
String action=request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
String viewObject = request.getParameter("viewObject");
String actionArgv = viewObject != null && !viewObject.equals("")?",viewObject=" + viewObject:"";
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject; 
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String sonlyOneActionPerRow = request.getParameter("onlyOneActionPerRow");
java.util.Collection rowActions = null;
if (sonlyOneActionPerRow == null || !Boolean.parseBoolean(sonlyOneActionPerRow)) {
	rowActions = view.isRepresentsCollection()?view.getRowActionsNames():manager.getRowActionsNames();
}
else {
	rowActions = java.util.Collections.EMPTY_SET;
}
String sfilter = request.getParameter("filter");
boolean filter = !"false".equals(sfilter);
String displayFilter=""; 
String imageFilter="hide-filter";
String filterMessage="hide_filters";
if (!tab.isFilterVisible()) {
	displayFilter="none"; 
	imageFilter ="show-filter"; 
	filterMessage="show_filters";
}
String lastRow = request.getParameter("lastRow");
boolean singleSelection="true".equalsIgnoreCase(request.getParameter("singleSelection"));
String onSelectCollectionElementAction = view.getOnSelectCollectionElementAction();
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
String selectedRowStyle = style.getSelectedRowStyle();
String rowStyle = "border-bottom: 1px solid;";
int currentRow = ((Number) context.get(request, "xava_row")).intValue(); 
String cssCurrentRow = style.getCurrentRow();
String styleOverflow = "overflow: auto;";
int totalSize = -1; 
if (request.getAttribute(org.openxava.tab.Tab.TAB_RESETED_PREFIX + tab) == null) {
	tab.setRequest(request);
	tab.reset();
	request.setAttribute(org.openxava.tab.Tab.TAB_RESETED_PREFIX + tab, Boolean.TRUE); 
}
boolean resizeColumns = style.allowsResizeColumns() && tab.isResizeColumns();
String browser = request.getHeader("user-agent");
boolean scrollSupported = !(browser != null && (browser.indexOf("MSIE 6") >= 0 || browser.indexOf("MSIE 7") >= 0));
%>

<input type="hidden" name="xava_list<%=tab.getTabName()%>_filter_visible"/>

<%
if (collection == null || collection.equals("")) { 	
%>
<table width="100%" class=<%=style.getListTitleWrapper()%>>
<tr><td class=<%=style.getListTitle()%>>
<% if (style.isShowModuleDescription()) { %>
<%=manager.getModuleDescription()%>
<% } %>
<% 
if (tab.isTitleVisible()) { 
%> 
<% if (style.isShowModuleDescription()) { %> - <% } %>
<span id="list-title"><%=tab.getTitle()%></span>
<%
}
%>
<% if (style.isShowRowCountOnTop()) { 
	totalSize = tab.getTotalSize();
	int finalIndex = Math.min(totalSize, tab.getFinalIndex());
%>
<span class="<%=style.getHeaderListCount()%>">
<%=XavaResources.getString(request, "header_list_count", new Integer(tab.getInitialIndex() + 1), new Integer(finalIndex), new Integer(totalSize))%>
</span>
<% } %>
</td></tr>
</table>
<%
} 
%>
<% if (resizeColumns && scrollSupported) { %>
<div class="<xava:id name='<%=scrollId%>'/>" style="<%=styleOverflow%>">
<% } %> 
<table id="<xava:id name='<%=id%>'/>" class="<%=style.getList()%>" <%=style.getListCellSpacing()%> style="<%=style.getListStyle()%>">  
<tr class="<%=style.getListHeader()%>">
<th class="<%=style.getListHeaderCell()%>" style="text-align: center">
	<%
		String imageFilterPrefix = request.getContextPath() + "/xava/images/";
	%>
	<input name="xava_image_filter_prefix" type="hidden" value="<%=imageFilterPrefix%>"/>     
	<a id="<xava:id name='<%="filter_link_" + id%>'/>" href="javascript:openxava.manageFilterRow('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=id%>', '<%=tabObject%>')" title="<xava:message key='<%=filterMessage%>'/>"><img id="<xava:id name='<%="filter_image_" + id%>'/>" align='middle' 
		src='<%=imageFilterPrefix%><%=imageFilter%>.gif' border='0'/></a>
	<% if (tab.isCustomizeAllowed()) { %> 
	<xava:image action="List.customize" argv="<%=collectionArgv%>"/>
	<%
		if (tab.isCustomize()) {
	%><xava:image action="List.addColumns" argv="<%=collectionArgv%>"/><%
		}
	} 
	%>
</th>
<th class="<%=style.getListHeaderCell()%>" width="5">
	<%
		if (!singleSelection){
			String actionOnClickAll = Actions.getActionOnClickAll(
			request.getParameter("application"), request.getParameter("module"), 
			onSelectCollectionElementAction, viewObject, prefix,
			selectedRowStyle, rowStyle);
	%>
	<INPUT type="CHECKBOX" name="<xava:id name='xava_selected_all'/>" value="<%=prefix%>selected_all" <%=actionOnClickAll%> />
	<%
		}
	%>
</th>
<%
java.util.Collection properties = tab.getMetaProperties();
java.util.Iterator it = properties.iterator();
int columnIndex = 0;
Preferences preferences = Users.getCurrentPreferences();
while (it.hasNext()) {
	MetaProperty property = (MetaProperty) it.next();
	String align = "";
	if (style.isAlignHeaderAsData()) {
		align =property.isNumber() && !property.hasValidValues()?"vertical-align: middle;text-align: right":"vertical-align: middle";
	}
	int columnWidth = tab.getColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
%>
<th class="<%=style.getListHeaderCell()%>" style="<%=align%>; padding-right: 0px" >
<div id="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):("")) %>" style="overflow: hidden; <%=width%>" > 
<%
	if (tab.isCustomize()) {
%><xava:image action="List.moveColumnToLeft" argv='<%="columnIndex="+columnIndex+collectionArgv%>'/><%
	}
%>
<%
	String label = property.getQualifiedLabel(request);
	if (resizeColumns) label = label.replaceAll(" ", "&nbsp;");
	if (property.isCalculated()) {
%>
<%=label%>&nbsp;
<%
	} else {
%>
<span class="<%=style.getListOrderBy()%>">
<xava:link action='List.orderBy' argv='<%="property="+property.getQualifiedName() + collectionArgv%>'><%=label%></xava:link>&nbsp;
</span>
<%
	if (tab.isOrderAscending(property.getQualifiedName())) {
%>
<img src="<%=request.getContextPath()%>/xava/images/<%=style.getAscendingImage()%>" border="0" align="middle"/>
<%
	}
%>
<%
	if (tab.isOrderDescending(property.getQualifiedName())) {
%>
<img src="<%=request.getContextPath()%>/xava/images/<%=style.getDescendingImage()%>" border="0" align="middle"/>
<%
	}
%>
<%
	if (tab.isOrderAscending2(property.getQualifiedName())) {
%>
<img src="<%=request.getContextPath()%>/xava/images/<%=style.getAscending2Image()%>" border="0" align="middle"/>
<%
	}
%>
<%
	if (tab.isOrderDescending2(property.getQualifiedName())) {
%>
<img src="<%=request.getContextPath()%>/xava/images/<%=style.getDescending2Image()%>" border="0" align="middle"/>
<%
	}
%>	
<%
		}
		   
		   if (tab.isCustomize()) {
	%>
	<xava:image action="List.moveColumnToRight" argv='<%="columnIndex="+columnIndex+collectionArgv%>'/>
	<xava:image action="List.removeColumn" argv='<%="columnIndex="+columnIndex+collectionArgv%>'/>
<%
	}
%>
</div> 
</th>
<%
	columnIndex++;
}
%>
</tr>
<%
	if (filter) {
%>
<tr id="<xava:id name='<%="tr_list_filter_" + id%>'/>" class=<%=style.getListSubheader()%> style="display: <%=displayFilter%>"> 
<th class="<%=style.getFilterCell()%> <%=style.getListSubheaderCell()%>">
<xava:action action="List.filter" argv="<%=collectionArgv%>"/>
</th>
<th class=<%=style.getListSubheaderCell()%> width="5">
	<a title='<xava:message key="clear_condition_values"/>' href="javascript:void(0)">
		<img src='<%=request.getContextPath()%>/xava/images/clear-right.gif'
			border='0' align='middle' onclick="openxava.clearCondition('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=prefix%>')"/>
	</a>
</th>
<%
it = properties.iterator();
String [] conditionValues = tab.getConditionValues();
String [] conditionValuesTo = tab.getConditionValuesTo(); 
String [] conditionComparators = tab.getConditionComparators();
int iConditionValues = -1;
columnIndex = 0; 
while (it.hasNext()) {
	MetaProperty property = (MetaProperty) it.next();
	if (!property.isCalculated()) {
		iConditionValues++; 
		boolean isValidValues = property.hasValidValues();
		boolean isString = "java.lang.String".equals(property.getType().getName());
		boolean isBoolean = "boolean".equals(property.getType().getName()) || "java.lang.Boolean".equals(property.getType().getName());
		boolean isDate = java.util.Date.class.isAssignableFrom(property.getType()) && !property.getType().equals(java.sql.Time.class);
		String editorURLDescriptionsList = WebEditors.getEditorURLDescriptionsList(tab.getTabName(), tab.getModelName(), Ids.decorate(request, property.getQualifiedName()), iConditionValues, prefix, property.getQualifiedName(), property.getName());
		int maxLength = property.getSize();
		int length = Math.min(isString?property.getSize()*4/5:property.getSize(), 20);
		String value= conditionValues==null?"":conditionValues[iConditionValues];
		String valueTo= conditionValuesTo==null?"":conditionValuesTo[iConditionValues];
		String comparator = conditionComparators==null?"":Strings.change(conditionComparators[iConditionValues], "=", "eq");
		int columnWidth = tab.getColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
%>
<th class="<%=style.getListSubheaderCell()%>" align="left">
<div class="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" style="overflow: hidden; <%=width%>">
<% 		
		if (isValidValues) {
%>
<%-- Boolean.toString( ) for base0 is needed in order to work in WebSphere 6 --%>
<jsp:include page="comparatorsValidValuesCombo.jsp">
	<jsp:param name="validValues" value="<%=property.getValidValuesLabels(request)%>" />
	<jsp:param name="value" value="<%=value%>" />
	<jsp:param name="base0" value="<%=Boolean.toString(!property.isNumber())%>" />
	<jsp:param name="prefix" value="<%=prefix%>"/>
	<jsp:param name="index" value="<%=iConditionValues%>"/>
</jsp:include>		
<%
		}
		else if (!Is.empty(editorURLDescriptionsList)) {
%>
<jsp:include page="<%=editorURLDescriptionsList%>" >
	<jsp:param name="value" value="<%=value%>" />
</jsp:include>
<%
		}
		else if (isBoolean) {
%>
<jsp:include page="comparatorsBooleanCombo.jsp">
	<jsp:param name="comparator" value="<%=comparator%>" />
	<jsp:param name="prefix" value="<%=prefix%>"/>
	<jsp:param name="index" value="<%=iConditionValues%>"/> 
</jsp:include>
<%
		} else { // Not boolean
	String idConditionValue = Ids.decorate(request, prefix + "conditionValue." + iConditionValues);
	String idConditionValueTo = Ids.decorate(request, prefix + "conditionValueTo." + iConditionValues);
	String styleConditionValueTo = "range_comparator".equals(comparator) ? "display: inline; " : "display: none;";
	String labelFrom = "range_comparator".equals(comparator) ? Labels.get("from") : "";
	String labelTo = Labels.get("to");
	String urlComparatorsCombo = "comparatorsCombo.jsp" // in this way because websphere 6 has problems with jsp:param
		+ "?comparator=" + comparator
		+ "&isString=" + isString
		+ "&isDate=" + isDate
		+ "&prefix=" + prefix  
		+ "&index=" + iConditionValues
		+ "&idConditionValue=" + idConditionValue
		+ "&idConditionValueTo=" + idConditionValueTo;
%>
<jsp:include page="<%=urlComparatorsCombo%>" />
<input id="<%=idConditionValue%>" name="<%=idConditionValue%>" class=<%=style.getEditor()%> type="text" maxlength="<%=maxLength%>" size="<%=length%>" value="<%=value%>" placeholder="<%=labelFrom%>"/>
<input id="<%=idConditionValueTo%>" name="<%=idConditionValueTo%>" class=<%=style.getEditor()%> type="text" maxlength="<%=maxLength%>" size="<%=length%>" value="<%=valueTo%>" placeholder="<%=labelTo%>" style="<%=styleConditionValueTo%>"/>
	<%
		}
	%>
</div>	
</th>
<%
	}
	else {
%>
<th class=<%=style.getListSubheaderCell()%>></th>
<%
	}
	columnIndex++; 
} // while
%>
</tr>
<%
	} /* if (filter) */
%>
<%
if (tab.isRowsHidden()) {
%>
	<tr id="nodata"><td align="center">
	<xava:link action="List.showRows" argv="<%=collectionArgv%>"/>
	</td></tr>
<%
	}
else {
IXTableModel model = tab.getTableModel(); 
totalSize = totalSize < 0?tab.getTotalSize():totalSize; 
if (totalSize > 0) {
for (int f=tab.getInitialIndex(); f<model.getRowCount() && f < tab.getFinalIndex(); f++) {
	String checked=tab.isSelected(f)?"checked='true'":"";	
	String cssClass=f%2==0?style.getListPair():style.getListOdd();	
	String cssCellClass=f%2==0?style.getListPairCell():style.getListOddCell(); 
	String cssStyle = tab.getStyle(f);
	if (cssStyle != null) {
		cssClass = cssClass + " " + cssStyle; 
		if (style.isApplySelectedStyleToCellInList()) cssCellClass = cssCellClass + " " + cssStyle; 
	}
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents(); 
	String cssClassToActionOnClick = cssClass;
	if (tab.isSelected(f)){
		cssClass = "_XAVA_SELECTED_ROW_ " + cssClass; 
		rowStyle = rowStyle + " " + selectedRowStyle;
	}
	String prefixIdRow = Ids.decorate(request, prefix);	
%>
<tr id="<%=prefixIdRow%><%=f%>" class="<%=cssClass%>" <%=events%> style="<%=rowStyle%>">
	<td class="<%=cssCellClass%>" style="vertical-align: middle;text-align: center; <%=style.getListCellStyle()%>">
	<%if (resizeColumns) {%><nobr><%}%> 
<%
	if (!org.openxava.util.Is.emptyString(action)) { 
%>
<xava:action action='<%=action%>' argv='<%="row=" + f + actionArgv%>'/>
<%
	}
	if (style.isSeveralActionsPerRow()) 
	for (java.util.Iterator itRowActions = rowActions.iterator(); itRowActions.hasNext(); ) { 	
		String rowAction = (String) itRowActions.next();		
%>
<xava:action action='<%=rowAction%>' argv='<%="row=" + f + actionArgv%>'/>
<%
	}
	String actionOnClick = Actions.getActionOnClick(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, f, viewObject, prefixIdRow + f,
		selectedRowStyle, rowStyle, 
		onSelectCollectionElementMetaAction);
%>
	<%if (resizeColumns) {%></nobr><%}%> 
	</td>
	<td class="<%=cssCellClass%>" style="<%=style.getListCellStyle()%>">
	<INPUT type="<%=singleSelection?"RADIO":"CHECKBOX"%>" name="<xava:id name='xava_selected'/>" value="<%=prefix + "selected"%>:<%=f%>" <%=checked%> <%=actionOnClick%> />
	</td>	
<%
	for (int c=0; c<model.getColumnCount(); c++) {
		MetaProperty p = tab.getMetaProperty(c);
		String align =p.isNumber() && !p.hasValidValues()?"vertical-align: middle;text-align: right; ":"vertical-align: middle; ";
		String cellStyle = align + style.getListCellStyle();
		int columnWidth = tab.getColumnWidth(c);		 		
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px"; 
		String fvalue = null;
		if (p.hasValidValues()) {
			fvalue = p.getValidValueLabel(request, model.getValueAt(f, c));
		}
		else {
			fvalue = WebEditors.format(request, p, model.getValueAt(f, c), errors, view.getViewName(), true);
		}
		Object title = WebEditors.formatTitle(request, p, model.getValueAt(f, c), errors, view.getViewName(), true); 
%>
	<td class="<%=cssCellClass%>" style="<%=cellStyle%>; padding-right: 0px">
		<% if (style.isRowLinkable()) { %> 	
		<xava:link action='<%=action%>' argv='<%="row=" + f + actionArgv%>' cssClass='<%=cssStyle%>' cssStyle="text-decoration: none; outline: none">
			<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
				<%if (resizeColumns) {%><nobr><%}%>
				<%=fvalue%>&nbsp;
				<%if (resizeColumns) {%></nobr><%}%>
			</div>
		</xava:link>
		<% } else { %>		
		<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">	
			<%if (resizeColumns) {%><nobr><%}%>
			<%=fvalue%>&nbsp;
			<%if (resizeColumns) {%></nobr><%}%>
		</div>
		<% } %>
	</td>
<%
	}
%>
</tr>
<%
} 
%>
<tr class="<%=style.getTotalRow()%>">
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"text-align: right; ":"";
	String cellStyle = align + style.getTotalCellStyle(); 
	
	if (tab.hasTotal(c)) {
		String ftotal = WebEditors.format(request, p, tab.getTotal(c), errors, view.getViewName(), true);
	%>
	<td class="<%=style.getTotalCell()%>" style="<%=cellStyle%>">	 
	<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden;">	
	<nobr>
	<% if (!tab.isFixedTotal(c) && XavaPreferences.getInstance().isSummationInList()) { %>
	<xava:image action='List.removeColumnSum' argv='<%="property="+p.getQualifiedName() + collectionArgv%>' cssStyle="vertical-align: top;"/>
	<% } %>
	<%=ftotal%>&nbsp;
	</nobr>	
	</div>	
	</td>
	<%	
	}
	else if (XavaPreferences.getInstance().isSummationInList() && tab.isTotalCapable(c)) { 
	%>
	<td class="<%=style.getTotalCapableCell()%>" style="<%=style.getTotalCapableCellStyle() %>">	
		<xava:action action='List.sumColumn' argv='<%="property="+p.getQualifiedName() + collectionArgv%>'/>&nbsp;
	</td>
	<%
	}
	else if (tab.hasTotal(c + 1)) { 
	%>
	<td style="<%=style.getTotalLabelCellStyle()%>">	
		<%=tab.getTotalLabel(0, c + 1)%>&nbsp;
	</td>
	<%
	}	
	else {
	%>	 
	<td style="<%=style.getTotalEmptyCellStyle()%>"/>
	<%		
	}	
}
%>
</tr>
<%
int additionalTotalsCount = tab.getAdditionalTotalsCount() + 1;
for (int i=1; i<additionalTotalsCount; i++) {
%>
<tr class="<%=style.getTotalRow()%>">
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"text-align: right; ":"";
	String cellStyle = align + style.getTotalCellStyle(); 
	
	if (tab.hasTotal(i, c)) {
		String ftotal = WebEditors.format(request, p, tab.getTotal(i, c), errors, view.getViewName(), true);
	%> 	
	<td class="<%=style.getTotalCell()%>" style="<%=cellStyle%>">
	<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden;">
	<%=ftotal%>&nbsp;
	</nobr>	
	</div>	
	</td>
	<%	
	}
	else if (tab.hasTotal(i, c + 1)) { 
	%>
	<td style="<%=style.getTotalLabelCellStyle()%>">	
		<%=tab.getTotalLabel(i, c + 1)%>&nbsp;
	</td>
	<%	
	}
	else {
	%>	 
	<td style="<%=style.getTotalEmptyCellStyle()%>"/>
	<%		
	}	
}
%>
</tr>
<%
} // for additionalTotalsCount 

}
else {
%>
<tr id="nodata"><td class="<%=totalSize==0?style.getMessages():style.getErrors()%>">
<% if (totalSize == 0) { %>
<b><xava:message key="no_objects"/></b>
<% } else { %>
<b><xava:message key="list_error"/></b>
<% } %>
</td></tr>
<%
}
}

if (lastRow != null) {
%>
<tr>
	<jsp:include page="<%=lastRow%>"/>
</tr>
<%
}
%>
</table>
<% if (resizeColumns && scrollSupported) { %>
</div> 
<% } %>

<% if (!tab.isRowsHidden()) { %>
<table width="100%" class="<%=style.getListInfo()%>" cellspacing=0 cellpadding=0>
<tr class='<%=style.getListInfoDetail()%>'>
<td class='<%=style.getListInfoDetail()%>'>
<%
int last=tab.getLastPage();
int current=tab.getPage();
if (current > 1) {
%>
<span class='<%=style.getFirst()%>'><span class='<%=style.getPageNavigationArrow()%>' <%=style.getPreviousPageNavigationEvents(Ids.decorate(request, id))%>><xava:image action='List.goPreviousPage' argv='<%=collectionArgv%>'/></span></span>
<%
}
else {
%>
<span class='<%=style.getFirst()%>'><span class='<%=style.getPageNavigationArrowDisable()%>'><img 
	src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getPreviousPageDisableImage()%>' 
	border=0 align="absmiddle"/></span></span>
<%	
} 
%>
<span class="<%=style.getPageNavigationPages()%>">
<%
for (int i=1; i<=last; i++) {
if (i == current) {
	if (style.isShowPageNumber()) {  
%>
<span class="<%=style.getPageNavigationSelected()%>"><%=i%></span>
	<% } else {%>
<span class="<%=style.getPageNavigationSelected()%>">
	<img 
		src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getPageNavigationSelectedImage()%>' 
		border=0 align="absmiddle"/>
</span>	
	<% } %>
<% } else { 
		if (style.isShowPageNumber()) { 
%>
<xava:link action='List.goPage' argv='<%="page=" + i + collectionArgv%>' cssClass="<%=style.getPageNavigation()%>"><%=i%></xava:link>
<% 
		} else {
%>
<span class="<%=style.getPageNavigation()%>">
	<img 
		src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getPageNavigationImage()%>' 
		border=0 align="absmiddle"/>
</span>
<%				
		}
	}
} 
%>
</span>
<%
if (!tab.isLastPage()) {
%>
<span class='<%=style.getLast()%>'>
<span class='<%=style.getPageNavigationArrow()%>' <%=style.getNextPageNavigationEvents(Ids.decorate(request, id)) %>>
<xava:image action='List.goNextPage' argv='<%=collectionArgv%>'/>
</span>
</span>
<% 
} 
else {
%>
<span class='<%=style.getLast()%>'>
<span class='<%=style.getPageNavigationArrowDisable()%>'><img 
	src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getNextPageDisableImage()%>' 
	border=0 align="absmiddle"/>
</span>
</span>
<%	
} 
%>
<% if (style.isChangingPageRowCountAllowed()) { %>
&nbsp;
<select id="<xava:id name='<%=id + "_rowCount"%>'/>" class=<%=style.getEditor()%>
	onchange="openxava.setPageRowCount('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=collection==null?"":collection%>', this)">
	<% 
	int [] rowCounts = { 5, 10, 15, 20 }; // The peformance with more than 20 rows is poor for page reloading 
	for (int i=0; i<rowCounts.length; i++) {
		String selected = rowCounts[i] == tab.getPageRowCount()?"selected='selected'":""; 	
	%>	
	<option value="<%=rowCounts[i]%>" <%=selected %>><%=rowCounts[i]%></option>
	<%
	}
	%>
</select>
<span class="<%=style.getRowsPerPage()%>">	 
<xava:message key="rows_per_page"/>
</span>
<% } // of if (style.isChangingPageRowCountAllowed()) %>
</td>
<td style='text-align: right; vertical-align: middle' class='<%=style.getListInfoDetail()%>'>
<% if (XavaPreferences.getInstance().isShowCountInList() && !style.isShowRowCountOnTop()) { %>
<xava:message key="list_count" intParam="<%=totalSize%>"/>
<% } %>
<% if (collection == null && style.isHideRowsAllowed()) { %>
(<xava:link action="List.hideRows" argv="<%=collectionArgv%>"/>)
<% } %>
</td>
</tr>
</table>
<% } %>

