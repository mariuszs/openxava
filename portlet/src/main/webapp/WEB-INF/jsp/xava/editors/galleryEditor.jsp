<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String viewObject = request.getParameter("viewObject");
String viewObjectArgv = Is.emptyString(viewObject) || "xava_view".equals(viewObject)?"":(",viewObject=" + viewObject);
%>



<%@page import="org.openxava.util.Is"%><input id="<%=propertyKey%>" type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">

<xava:image action='Gallery.edit' argv='<%="galleryProperty=" + p.getName() + viewObjectArgv%>'/>


