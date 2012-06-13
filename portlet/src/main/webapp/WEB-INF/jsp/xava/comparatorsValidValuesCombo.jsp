<%@ include file="imports.jsp"%>

<%@ page import="java.util.StringTokenizer" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String validValues = request.getParameter("validValues");
String value = request.getParameter("value");
String prefix = request.getParameter("prefix");
if (prefix == null) prefix = "";
// base is 0 when we are working with Java 5 Enum, and 1 when I working with classic OX2 valid-values
int base = "true".equals(request.getParameter("base0"))?0:1;
int ivalue = -1;
try {
	ivalue = Integer.parseInt(value);
}
catch (Exception ex) {
}
int index = Integer.parseInt(request.getParameter("index"));
%>

<input type="hidden" name="<xava:id name='<%=prefix  + "conditionComparator."  + index%>'/>" value="eq">
<input type="hidden" name="<xava:id name='<%=prefix  + "conditionValueTo."  + index%>'/>" >
<select name="<xava:id name='<%=prefix  + "conditionValue."  + index%>'/>" class=<%=style.getEditor()%>>
	<option value=""></option>
<%
	StringTokenizer st = new StringTokenizer(validValues, "|");
	int i = base;
	while (st.hasMoreTokens()) {		
		String selected =  (i == ivalue)?"selected":"";
%>
	<option value="<%=i%>" <%=selected%>><%=st.nextToken()%></option>
<%
		i++;
	} // while
%>
</select>