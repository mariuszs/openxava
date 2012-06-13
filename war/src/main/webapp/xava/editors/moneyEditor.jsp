<%@page import="java.util.Currency"%>
<%@page import="java.util.Locale"%>

<%@ include file="textEditor.jsp"%>

<% 
String symbol = null;
try {
	symbol = Currency.getInstance(Locale.getDefault()).getSymbol(); 
}
catch (Exception ex) { // Because Locale.getDefault() may not contain the country
	symbol = "?";
}
%>

<b> <%=symbol%></b>